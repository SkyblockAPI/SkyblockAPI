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
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.item.replaceVisually
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockAttributesRepo
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTag
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils
import tech.thatgravyboat.skyblockapi.utils.codecs.IncludedCodecs
import tech.thatgravyboat.skyblockapi.utils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import kotlin.math.max

@Module
object AttributeAPI {

    private val isDebugEnabled by debugToggle("attribute_api", "Adds debug information in both the hunting box and the attribute menu that shows stored data.")

    private val attributeRarities = SkyBlockRarity.COMMON.rangeTo(SkyBlockRarity.LEGENDARY)

    private const val FIRST_PARENT = 12
    private const val SECOND_PARENT = 14
    private const val FUSION_RESULT = 31
    private const val FUSION_RESULT_AMOUNT = 33
    private val anyFusionSlot = listOf(FIRST_PARENT, SECOND_PARENT, FUSION_RESULT, FUSION_RESULT_AMOUNT)

    //region Regex
    private val inventoryGroup = RegexGroup.INVENTORY.group("attribute")

    private val attributeMenuGroup = inventoryGroup.group("attribute_menu")
    val attributeMenuRegex = attributeMenuGroup.create("title", "^(?:\\((?<currentPage>\\d+)/\\d+\\) )?Attribute Menu$")
    private val syphonMoreRegex = attributeMenuGroup.create("more", "^Syphon (\\d+) more to level up!")

    private val huntingBoxGroup = inventoryGroup.group("hunting_box")
    val huntingBoxMenuRegex = huntingBoxGroup.create("title", "^(?:\\((?<currentPage>\\d+)/\\d+\\) )?Hunting Box$")
    private val ownedRegex = huntingBoxGroup.create("owned", "^Owned: (?<amount>[\\d,.]+) Shards?$")
    private val attributeMaxedRegex = huntingBoxGroup.create("maxed", "^Attribute Maxed!$")
    private val levelRegex = huntingBoxGroup.create("level", "[IXV0-9]+")

    private val fusionInventoryGroup = inventoryGroup.group("fusion")
    private val confirmFusionRegex = fusionInventoryGroup.create("title", "^Confirm Fusion$")
    private val fusionItemRegex = fusionInventoryGroup.create("item_title", "^Parent|Fusion Result$")

    private val chatGroup = RegexGroup.CHAT.group("attribute")

    private val trapGroup = chatGroup.group("trap")
    private val foundShardRegex = trapGroup.create("caught", "^(?:You caught|LOOT SHARE You received) (?<amount>an?|x?\\d+) (?<name>.*?) Shards?(?: for assisting \\w+)?!$")

    private val fusionChatGroup = chatGroup.group("fusion")
    private val fusionObtainedRegex = fusionChatGroup.create("obtained", "FUSION! You obtained (?:an? )?(.*?)(?: (x\\d+))?!.*")
    private val fusionPureReptileRegex = fusionChatGroup.create("pure_reptile", "^PURE REPTILE You received double shards from the fusion!$")

    private val syphonGroup = chatGroup.group("syphon")
    private val syphonedRegex = syphonGroup.create("syphoned", "\\+(?<amount>\\d{1,2}) (?<name>.*?) Attribute \\(Level (?<level>\\d+)\\).*")

    private val saltGroup = chatGroup.group("salt")
    private val saltSingularRegex = saltGroup.create("singular", "(?:CHARM|SALT|NAGA) You charmed an? (?<name>.*?) and captured its Shard\\.")
    private val saltMultipleRegex = saltGroup.create("multiple", "(?:CHARM|SALT|NAGA) You charmed an? (?<name>.*?) and captured (?<amount>\\d+) Shards from it\\.")

    private val sentToHuntingBoxRegex = chatGroup.create("sent_to_hunting_box", "You sent (?<amount>an?|\\d+) (?<shard>.*? Shard)s? to your Hunting Box.")

    private val fishingRegex = chatGroup.create("fishing", "^\uE025 .*? CATCH! You caught a (?<name>.*) Shard!$")
    private val fishingMultipleRegex = chatGroup.create("fishing_multiple", "^\uE025 .*? CATCH! You caught (?<name>.*) Shard! x(?<amount>\\d+)$")
    //endregion

    private val deferredFusion = DeferredFusion()

    private val _attributeMap: MutableMap<SkyBlockId, AttributeData> get() = AttributeStorage.data ?: mutableMapOf()

    val attributeMap: Map<SkyBlockId, AttributeData> get() = _attributeMap

    val attributeLevelData: MutableMap<SkyBlockRarity, List<Int>> =
        SkyBlockAPI.getRepo("attribute_levels", CodecUtils.map(SkyblockAPICodecs.getCodec<SkyBlockRarity>(), IncludedCodecs.CUMULATIVE_INT_LIST))

    @Subscription
    @MustBeContainer
    @OnlyOnSkyBlock
    fun attributeMenu(event: InventoryChangeEvent) {
        if (!event.title.matches(attributeMenuRegex)) return
        if (event.isOnSides) return

        val id = event.item[DataTypes.SKYBLOCK_ID] ?: return
        val data = SkyBlockAttributesRepo.get(id.cleanId) ?: return
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
        if (!event.isInMainPart) return

        val id = event.item[DataTypes.SKYBLOCK_ID] ?: return
        val data = SkyBlockAttributesRepo.get(id.cleanId) ?: return

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
            val actualAmount = if (amount.startsWith("a")) 1 else amount.filter { it.isDigit() }.toIntValue()
            val id = SkyBlockId.fromName(name) ?: return@match
            addOwnedAttributeAmount(id, actualAmount)
        }
        AttributeStorage.save()
    }

    @Subscription
    @MustBeContainer
    @OnlyOnSkyBlock
    fun fusionMenu(event: InventoryChangeEvent) {
        if (!event.title.matches(confirmFusionRegex)) return
        if (event.isOnSides) return
        if (event.slot.index !in anyFusionSlot) return

        when (event.slot.index) {
            FUSION_RESULT_AMOUNT -> {
                deferredFusion.output = deferredFusion.output?.let { (id, amount) -> id to max(amount, event.item.count) }
                return
            }
        }

        val shard = event.item.getRawLore().dropWhile {
            it.matches(fusionItemRegex)
        }.firstOrNull()?.removeSuffix(" NEW SHARD") ?: return
        val id = SkyBlockId.fromName(shard) ?: return

        when (event.slot.index) {
            FIRST_PARENT -> deferredFusion.first = id to event.item.count
            SECOND_PARENT -> deferredFusion.second = id to event.item.count
            FUSION_RESULT -> deferredFusion.output = id to event.item.count
        }
    }

    @Subscription
    @OnlyOnSkyBlock
    fun fusionComplete(event: ChatReceivedEvent.Pre) {
        if (!event.text.matches(fusionObtainedRegex)) return
        if (!deferredFusion.isComplete()) return
        deferredFusion.submit()
        deferredFusion.reset()
    }

    @Subscription
    @OnlyOnSkyBlock
    fun syphoned(event: ChatReceivedEvent.Pre) {
        syphonedRegex.match(event.text, "amount", "name") { (amount, name) ->
            val id = SkyBlockId.fromName(name) ?: return@match
            val amount = amount.toIntValue()
            addSyphonedAttributeAmount(id, amount)
            removeOwnedAttributeAmount(id, amount)
        }
        if (fusionPureReptileRegex.matches(event.text)) {
            deferredFusion.doubleOutput = true
        }
    }

    @Subscription
    @OnlyOnSkyBlock
    fun salt(event: ChatReceivedEvent.Pre) {
        saltSingularRegex.match(event.text, "name") { (name) ->
            val id = SkyBlockId.fromName(name) ?: return@match
            addOwnedAttributeAmount(id, 1)
        }
        saltMultipleRegex.match(event.text, "name", "amount") { (name, amount) ->
            val id = SkyBlockId.fromName(name) ?: return@match
            addOwnedAttributeAmount(id, amount.toIntValue())
        }
    }

    @Subscription
    @OnlyOnSkyBlock
    fun huntingBox(event: ChatReceivedEvent.Pre) {
        sentToHuntingBoxRegex.match(event.text, "amount", "shard") { (amount, shard) ->
            val actualAmount = if (amount.startsWith("a")) 1 else amount.filter { it.isDigit() }.toIntValue()
            val id = SkyBlockId.fromName(shard, true) ?: return@match

            addOwnedAttributeAmount(id, actualAmount)
        }
    }

    @Subscription
    @OnlyOnSkyBlock
    fun fishing(event: ChatReceivedEvent.Pre) {
        fishingRegex.match(event.text, "name") { (name) ->
            val id = SkyBlockId.fromName(name, true) ?: return@match

            addOwnedAttributeAmount(id, 1)
        }
        fishingMultipleRegex.match(event.text, "name", "amount") { (name, amount) ->
            val id = SkyBlockId.fromName(name, true) ?: return@match
            val amount = amount.toIntValue()

            addOwnedAttributeAmount(id, amount)
        }
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

    internal fun addOwnedAttributeAmount(id: SkyBlockId, amount: Int) {
        if (isDebugEnabled) {
            Text.debug("Owned ") {
                append(id.toItem().hoverName)
                if (amount >= 0) {
                    append(" +$amount") { this.color = TextColor.GREEN }
                } else {
                    append(" $amount") { this.color = TextColor.RED }
                }
            }.send()
        }
        getData(id).owned += amount
        AttributeStorage.save()
    }

    internal fun removeOwnedAttributeAmount(id: SkyBlockId, amount: Int) = addOwnedAttributeAmount(id, -amount)

    internal fun addSyphonedAttributeAmount(id: SkyBlockId, amount: Int) {
        if (isDebugEnabled) {
            Text.debug("Syphoned ") {
                append(id.toItem().hoverName)
                if (amount >= 0) {
                    append(" +$amount") { this.color = TextColor.GREEN }
                } else {
                    append(" $amount") { this.color = TextColor.RED }
                }
            }.send()
        }
        getData(id).syphoned += amount
        AttributeStorage.save()
    }

    private fun resetOwnedAttributeAmounts() {
        for (key in _attributeMap.keys) {
            _attributeMap[key]?.owned = 0
        }
        AttributeStorage.save()
    }

    @Subscription
    fun onCommandRegister(event: RegisterCommandsEvent) {
        event.register("sbapi attributes") {
            then("reset") {
                then("owned") {
                    callback {
                        resetOwnedAttributeAmounts()
                        Text.debug("Reset Owned Shards!").send()
                    }
                }
            }
        }
    }

}

private data class DeferredFusion(
    var first: Pair<SkyBlockId, Int>? = null,
    var second: Pair<SkyBlockId, Int>? = null,
    var output: Pair<SkyBlockId, Int>? = null,
) {
    var doubleOutput = false

    fun isComplete() = first != null && second != null && output != null

    fun reset() {
        first = null
        second = null
        output = null
        doubleOutput = false
    }

    fun submit() {
        first?.let { (id, amount) -> AttributeAPI.removeOwnedAttributeAmount(id, amount) }
        second?.let { (id, amount) -> AttributeAPI.removeOwnedAttributeAmount(id, amount) }
        output?.let { (id, amount) -> AttributeAPI.addOwnedAttributeAmount(id, amount) }
        if (doubleOutput) {
            output?.let { (id, amount) -> AttributeAPI.addOwnedAttributeAmount(id, amount) }
        }
    }
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
