package tech.thatgravyboat.skyblockapi.api.repo.apis

import com.google.gson.JsonParser
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.ktmodules.Module
import net.minecraft.core.component.DataComponents
import tech.thatgravyboat.repolib.api.PetsAPI
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.LoreDataTypes
import tech.thatgravyboat.skyblockapi.api.remote.api.SimpleItemAPI
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockPetsRepo.Query
import tech.thatgravyboat.skyblockapi.utils.command.EnumArgument
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic
import java.text.DecimalFormat


private val schema: RepoItemQuerySchema<Query>.() -> Unit = {
    field("id", StringArgumentType.string(), Query::id, RepoAPI.pets().pets().keys)
    optionalField("level", IntegerArgumentType.integer(1), Query::level)
    optionalField("rarity", EnumArgument.create(SkyBlockRarity::class.java), Query::rarity)
    optionalField("skin", StringArgumentType.string(), Query::skin) { suggestions ->
        SimpleItemAPI.getAllIds().filter { it.isItem && it.cleanId.startsWith("pet_skin_") }.map { it.cleanId.lowercase() }.forEach {
            suggestions(it)
            suggestions(it.removePrefix("pet_skin_"))
        }
    }
    optionalField("heldItem", StringArgumentType.string(), Query::heldItem)
    flag("showStatBounds", Query::showStatBounds)
}

@Module
object SkyBlockPetsRepo : RepoItemCacheAsQuery<Query>("Pets", ::Query, schema) {

    private val repo get() = RepoAPI.pets()
    private val loreFormatter = DecimalFormat("0.####")
    private val variablePattern = Regex("\\{([a-zA-Z0-9_]+)}")

    override fun create(key: Query): LazyItemStack? {
        val data = repo.getPet(key.id) ?: return null
        val pet = data.tiers()[key.rarity.name] ?: return null

        val itemString = pet.item.toString().replace(variablePattern) { match ->
            val variable = match.groupValues[1]
            if (variable == "LVL") {
                if (key.showStatBounds) "1➡${key.level}"
                else key.level.toString()
            } else if (pet.variables.containsKey(variable)) {
                if (key.showStatBounds) {
                    val levelOneStat = pet.getStat(variable, 1, key.heldItem)
                    val maxLevelStat = pet.getStat(variable, key.level, key.heldItem)
                    "${loreFormatter.format(levelOneStat)}➡${loreFormatter.format(maxLevelStat)}"
                } else {
                    val stat = pet.getStat(variable, key.level, key.heldItem)
                    loreFormatter.format(stat)
                }
            } else {
                match.value
            }
        }

        var baseItem = JsonParser.parseString(itemString).asJsonObject.let(::LazyItemStack) ?: return null

        val skin = key.skin
        if (skin != null) {
            val skin = SkyBlockItemsRepo.getLazyItemStack("PET_SKIN_${skin.removePrefix("PET_SKIN_")}")
            val skinRarity = skin?.let { LoreDataTypes.getRarityLine(it[DataComponents.LORE]) }?.second

            val newName = Text.join(
                baseItem[DataComponents.CUSTOM_NAME] ?: Text.of(""),
                Text.of(" ✦", skinRarity?.color ?: TextColor.LIGHT_PURPLE),
            ) {
                this.italic = false
            }

            baseItem = baseItem.withComponents {
                this[DataComponents.CUSTOM_NAME] = newName
                skin?.get(DataComponents.PROFILE)?.let { this[DataComponents.PROFILE] = it }
            }
        }

        return baseItem
    }

    fun get(id: String): PetsAPI.Data? = ifInitialized { this.repo.getPet(id) }

    data class Query(
        var id: String = "",
        var rarity: SkyBlockRarity = SkyBlockRarity.COMMON,
        var level: Int = 100,
        var skin: String? = null,
        var heldItem: String? = null,
        var showStatBounds: Boolean = false,
    ) {
        constructor(id: String = "", rarity: SkyBlockRarity = SkyBlockRarity.COMMON, level: Int = 100, skin: String? = null, heldItem: String? = null): this(id, rarity, level, skin, heldItem, false)
    }
}
