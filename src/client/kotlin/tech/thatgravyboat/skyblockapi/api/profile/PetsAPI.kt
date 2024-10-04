package tech.thatgravyboat.skyblockapi.api.profile

import tech.thatgravyboat.skyblockapi.api.data.SkyblockRarity
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedDouble
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match

@Module
object PetsAPI {

    private val petGroup = RegexGroup.TABLIST_WIDGET.group("pet")

    private val petLevelRegex = petGroup.create(
        "level",
        "\\[Lvl (?<level>\\d+)] "
    )

    private val petXpRegex = petGroup.create(
        "xp",
        " (?<xp>[\\d,.]+)/(?<nextXp>[\\d,.mbkMBK]+) XP \\((?<percent>[\\d.]+)%\\)"
    )

    private val petOverflowXpRegex = petGroup.create(
        "overflowxp",
        " \\+(?<xp>[\\d,.]+) XP"
    )

    private val petMaxLevelRegex = petGroup.create(
        "max_level",
        " MAX LEVEL"
    )

    var pet: String? = null
        private set

    var rarity: SkyblockRarity? = null
        private set

    var level: Int = 0
        private set

    var isMaxLevel: Boolean = false
        private set

    var xp: Double = 0.0
        private set

    var xpToNextLevel: Double = 0.0
        private set

    @Subscription
    fun onTabWidgetChange(event: TabWidgetChangeEvent) {
        if (event.widget != TabWidget.PET) return
        this.reset()
        if (event.new.size < 2) return
        event.newComponents[1].siblings.takeIf { it.size == 3 }?.let { (_, level, pet) ->
            petLevelRegex.match(level.string, "level") { (level) ->
                this.level = level.toIntOrNull() ?: 0
            }
            this.pet = pet.string
            this.rarity = SkyblockRarity.fromColorOrNull(pet.style.color?.value ?: 0)
        }
        petXpRegex.anyMatch(event.new, "xp", "nextXp", "percent") { (xp, nextXp, _) ->
            this.xp = xp.parseFormattedDouble()
            this.xpToNextLevel = nextXp.parseFormattedDouble()
        }
        petOverflowXpRegex.anyMatch(event.new, "xp") { (xp) ->
            this.xp += xp.parseFormattedDouble()
            this.isMaxLevel = true
        }
        this.isMaxLevel = petMaxLevelRegex.anyMatch(event.new)
    }

    private fun reset() {
        pet = null
        rarity = null
        level = 0
        isMaxLevel = false
        xp = 0.0
        xpToNextLevel = 0.0
    }
}
