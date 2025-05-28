package tech.thatgravyboat.skyblockapi.api.profile.skillxp

import tech.thatgravyboat.skyblockapi.api.data.stored.SkillXpStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.InventoryTitle
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.info.SkillXpLiteralActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.SkillXpPercentActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.HypixelSkillAPI
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.toFloatValue
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match

@Module
object SkillXpAPI {

    val skills: Map<HypixelSkillAPI.Skill, Float>? get() = SkillXpStorage.data?.xp

    private val group = RegexGroup.INVENTORY.group("skillxp")
    private val itemNameRegex = group.create("itemName", "(?<name>.*) (?<level>\\d+)")
    private val itemLoreXpRegex = group.create("itemLoreXp", "\\s+(?<current>[\\d,.]+)(?:/(?<needed>[\\d,.]+[kmbKMB]?))?")

    @Subscription
    @OnlyOnSkyBlock
    @InventoryTitle("Your Skills")
    fun onInventory(event: InventoryChangeEvent) {
        if (!event.isInMainPart) return
        if (event.isSkyBlockFiller) return

        itemNameRegex.match(event.item.cleanName, "name", "level") { (name, level) ->
            val skill = HypixelSkillAPI.Skill.getByName(name) ?: return@match
            val level = level.toIntValue()

            itemLoreXpRegex.anyMatch(event.item.getRawLore(), "current") { (current) ->
                val xp = if (skill.data.maxLevel == level) {
                    println("Setting XP for ${skill.id} to ${current.toFloatValue()} at max level $level")
                    current.toFloatValue()
                } else {
                    println("Setting XP for ${skill.id} to ${current.toFloatValue()} at level $level")
                    val xpTillLevel = skill.data.getTotalExpForLevel(level)
                    xpTillLevel.toFloat() + current.toFloatValue()
                }

                SkillXpStorage.setXp(skill, xp)
            }
        }
    }

    @Subscription
    fun onActionbarLiteral(event: SkillXpLiteralActionBarWidgetChangeEvent) {

    }

    @Subscription
    fun onActionbarPercent(event: SkillXpPercentActionBarWidgetChangeEvent) {

    }
}
