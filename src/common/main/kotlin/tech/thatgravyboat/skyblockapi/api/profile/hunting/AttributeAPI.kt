package tech.thatgravyboat.skyblockapi.api.profile.hunting

import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.data.stored.AttributeStorage
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.MustBeContainer
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.item.replaceVisually
import tech.thatgravyboat.skyblockapi.api.remote.api.RepoAttributeAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTag
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils
import tech.thatgravyboat.skyblockapi.utils.codecs.IncludedCodecs
import tech.thatgravyboat.skyblockapi.utils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match

@Module
object AttributeAPI {

    private val isDebugEnabled by debugToggle("attribute_api", "Adds debug information in both the hunting box and the attribute menu that shows stored data.")

    val attributeLevelData: MutableMap<SkyBlockRarity, List<Int>> =
        SkyBlockAPI.getRepo("attribute_levels", CodecUtils.map(SkyblockAPICodecs.getCodec<SkyBlockRarity>(), IncludedCodecs.CUMULATIVE_INT_LIST))

    private val attributeRarities = SkyBlockRarity.COMMON.rangeTo(SkyBlockRarity.LEGENDARY)

    private val inventoryGroup = RegexGroup.INVENTORY.group("attribute")

    private val attributeMenuRegex = inventoryGroup.create("attribute_menu", "^Attribute Menu$")
    private val syphonMoreRegex = inventoryGroup.create("syphon_more", "^Syphon (\\d+) more to level up!")

    private val huntingBoxMenuRegex = inventoryGroup.create("hunting_box", "^Hunting Box$")
    private val ownedRegex = inventoryGroup.create("owned", "^Owned: (?<amount>[\\d,.]+) Shards?$")
    private val attributeMaxedRegex = inventoryGroup.create("attribute_maxed", "^Attribute Maxed!$")
    private val levelRegex = inventoryGroup.create("level", "[IXV0-9]+")

    private val chatGroup = RegexGroup.CHAT.group("attribute")

    private val foundShardRegex = chatGroup.create("found_shard", "^You caught (?<amount>a|x\\d+) (?<name>.*?) Shards?!$")

    private val _attributeMap: MutableMap<SkyBlockId, AttributeData> get() = AttributeStorage.data ?: mutableMapOf()
    val attributeMap: Map<SkyBlockId, AttributeData> get() = _attributeMap

    @Subscription
    @MustBeContainer
    @OnlyOnSkyBlock
    fun attributeMenu(event: InventoryChangeEvent) {
        if (!event.title.matches(attributeMenuRegex)) return
        if (event.isOnSides) return

        val id = event.item[DataTypes.SKYBLOCK_ID] ?: return
        val data = RepoAttributeAPI.getAttributeDataById(id.cleanId) ?: return
        val isLocked = event.item in ItemTag.HUNTING_NOT_FOUND

        val attributeData = getData(id)


        event.item.debugInfo(attributeData)

        if (isLocked) {
            attributeData.syphoned = 0
        } else {
            val level = event.item.cleanName.removePrefix(data.name()).trim().parseRomanOrArabic()
            val syphonMore = event.item.getRawLore().firstOrNull { it.matches(syphonMoreRegex) }?.replace(syphonMoreRegex, "$1").parseFormattedInt(-1)

            attributeData.calculateSyphoned(level, syphonMore)
        }
        AttributeStorage.save()
    }

    @Subscription
    @MustBeContainer
    @OnlyOnSkyBlock
    fun huntingBox(event: InventoryChangeEvent) {
        if (!event.title.matches(huntingBoxMenuRegex)) return
        if (event.isOnSides) return

        val id = event.item[DataTypes.SKYBLOCK_ID] ?: return
        val data = RepoAttributeAPI.getAttributeDataById(id.cleanId) ?: return

        val attributeData = getData(id)
        val rarity = attributeData.rarity ?: return

        event.item.debugInfo(attributeData)

        val rawLore = event.item.getRawLore()

        ownedRegex.anyMatch(rawLore, "amount") { (amount) ->
            val amount = amount.toIntValue()
            attributeData.owned = amount
        }

        attributeMaxedRegex.anyMatch(rawLore) {
            attributeLevelData[rarity]?.getOrNull(10)?.let { attributeData.syphoned = it }
        }
        val syphonMore = event.item.getRawLore().firstOrNull { it.matches(syphonMoreRegex) }?.replace(syphonMoreRegex, "$1").parseFormattedInt(-1)

        rawLore.find { it.startsWith(data.name()) }?.let {
            val level = it.removePrefix(data.name()).trim().substringBefore(" ")
            if (!level.matches(levelRegex)) return@let
            val actualLevel = level.parseRomanOrArabic().takeUnless { level -> level == 0 } ?: return
            attributeData.calculateSyphoned(actualLevel, syphonMore)
        }
        AttributeStorage.save()
    }

    @Subscription
    @OnlyOnSkyBlock
    fun foundShard(event: ChatReceivedEvent.Pre) {
        if (!event.text.matches(foundShardRegex)) return
        foundShardRegex.match(event.text, "amount", "name") { (amount, name) ->
            val actualAmount = if (amount == "a") 1 else amount.filter { it.isDigit() }.toIntValue()
            val id = SkyBlockId.fromName(name) ?: return@match
            val data = getData(id)
            data.owned += actualAmount
        }
        AttributeStorage.save()
    }

    private fun getData(id: SkyBlockId) = _attributeMap.getOrPut(id) { id.toAttributeData() }

    private fun AttributeData.calculateSyphoned(level: Int, syphonMore: Int) {
        val syphoned = attributeLevelData[rarity]?.get(level) ?: return
        val nextLevelSyphoned = attributeLevelData[rarity]?.getOrNull(level + 1)

        if (syphonMore == -1 || nextLevelSyphoned == null) {
            this.syphoned = syphoned
        } else {
            this.syphoned = nextLevelSyphoned - syphonMore
        }
        AttributeStorage.save()
    }

    private fun ItemStack.debugInfo(attributeData: AttributeData) {
        if (!isDebugEnabled) return
        replaceVisually {
            copyFrom(this@debugInfo)
            tooltip {
                lines().addAll(this@debugInfo.getLore())
                space()
                add("Syphoned: ${attributeData.syphoned}")
                add("Level: ${attributeData.level}")
                add("Owned: ${attributeData.owned}")
                add("Rarity: ${attributeData.rarity}")
                add("Unlocked: ${attributeData.unlocked}")
            }
        }
    }

    private fun SkyBlockId.toAttributeData() = AttributeData(
        rarity = attributeRarities.find { it.name.startsWith(this.cleanId.take(1), true) },
    )

}

@GenerateCodec
data class AttributeData(
    var owned: Int = 0,
    var syphoned: Int = 0,
    var rarity: SkyBlockRarity?,
) {
    val level: Int get() = AttributeAPI.attributeLevelData[rarity]?.indexOfLast { it <= syphoned } ?: -1
    val unlocked: Boolean get() = syphoned >= 1
}
