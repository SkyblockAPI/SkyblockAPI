package tech.thatgravyboat.skyblockapi.api.profile

import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedDouble
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentRegex
import tech.thatgravyboat.skyblockapi.utils.regex.component.anyFound
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@Module
object PetsAPI {

    private val petGroup = RegexGroup.TABLIST_WIDGET.group("pet")

    private val petRegex = ComponentRegex(petGroup.create(
        "pet",
        "^ \\[Lvl (?<level>\\d+)] (?<pet>[\\w ]+)"
    ))

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

    var rarity: SkyBlockRarity? = null
        private set

    var level: Int = 0
        private set

    var isMaxLevel: Boolean = false
        private set

    var xp: Double = 0.0
        private set

    var xpToNextLevel: Double = 0.0
        private set

    // {
    //   "text": "",
    //   "extra": [
    //     {
    //       "text": "§9Party §8\u003e §b[MVP§2+§b] Empa_§f: ",
    //       "hoverEvent": {
    //         "contents": {
    //           "text": "§eClick here to view §bEmpa_§e\u0027s profile",
    //           "strikethrough": false
    //         },
    //         "action": "show_text"
    //       },
    //       "clickEvent": {
    //         "action": "run_command",
    //         "value": "/viewprofile ecdf4cc9-0487-4d6f-bf09-8497deaf8b33"
    //       },
    //       "strikethrough": false
    //     },
    //     {
    //       "text": "a",
    //       "obfuscated": false,
    //       "italic": false,
    //       "underlined": false,
    //       "strikethrough": false,
    //       "bold": false
    //     }
    //   ],
    //   "strikethrough": false
    // }

    @Subscription
    fun onTabWidgetChange(event: TabWidgetChangeEvent) {
        if (event.widget != TabWidget.PET) return
        this.reset()
        if (event.new.size < 2) return
        petRegex.anyFound(event.newComponents, "level", "pet") { (level, pet) ->
            this.level = level.stripped.toIntOrNull() ?: 0
            this.pet = pet.stripped
            this.rarity = SkyBlockRarity.fromColorOrNull(pet.style.color?.value ?: 0)
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
