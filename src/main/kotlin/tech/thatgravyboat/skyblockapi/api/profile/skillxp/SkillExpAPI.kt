package tech.thatgravyboat.skyblockapi.api.profile.skillxp

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.SkillExpStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.InventoryTitle
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.hypixel.SkillExpGainedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.SkillXpLiteralActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.SkillXpPercentActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.HypixelSkillAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send

@Module
object SkillExpAPI {

    val skills: Map<HypixelSkillAPI.Skill, Float> get() = SkillExpStorage.data?.exp ?: emptyMap()

    private val group = RegexGroup.INVENTORY.group("skillexp")
    private val itemNameRegex = group.create("itemName", "(?<name>.*) (?<level>\\d+)")
    private val itemLoreXpRegex = group.create("itemLoreExp", "^\\s+(?<current>[\\d,.]+)(?:/(?<needed>[\\d,.]+[kmbKMB]?))?")

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
                    current.toFloatValue()
                } else {
                    val xpTillLevel = skill.data.getTotalExpForLevel(level)
                    xpTillLevel.toFloat() + current.toFloatValue()
                }

                SkillExpStorage.setXp(skill, xp)
            }
        }
    }

    @Subscription
    fun onActionbarLiteral(event: SkillXpLiteralActionBarWidgetChangeEvent) {
        val skill = event.skill ?: return

        // if needed is 0, they are at max level
        val skillxp = if (event.needed == 0L) skill.data.getTotalExpForLevel(skill.data.maxLevel)
        else skill.data.getTotalExpForLevel(SkillExpStorage.getLevel(skill))
        val diff = (event.current + skillxp) - SkillExpStorage.getXp(skill)
        if (diff != 0f) {
            SkillExpStorage.addXp(skill, diff)
            SkillExpGainedEvent(skill, diff, event.current.toFloat()).post()
        }
    }

    @Subscription
    fun onActionbarPercent(event: SkillXpPercentActionBarWidgetChangeEvent) {
        val skill = event.skill ?: return
        val level = SkillExpStorage.getLevel(skill)

        val needed = skill.data.getXpForLevel(level + 1).toFloat()
        val current = event.percent * needed

        val neededFromBefore = skill.data.getTotalExpForLevel(level).toFloat()
        val old = SkillExpStorage.getXp(skill)
        val diff = neededFromBefore + current - old
        if (diff != 0f) {
            SkillExpStorage.addXp(skill, diff)
            SkillExpGainedEvent(skill, diff, current).post()
        }
    }

    @Subscription
    fun onCommand(event: RegisterCommandsEvent) {
        event.register("sbapi skill") {
            thenCallback("list") {
                val skills = SkillExpStorage.data?.exp ?: return@thenCallback Text.of("No skill data found.").send()
                skills.entries.sortedByDescending { it.value }.forEach { (skill, xp) ->
                    val level = skill.data.getLevelForExp(xp.toLong())
                    Text.of("${skill.name}: Level $level (${xp.toFormattedString()} XP)").send()
                }
            }
            thenCallback("clear") {
                SkillExpStorage.data?.exp?.clear()
                SkillExpStorage.save()
                Text.of("Cleared skill xp data.").send()
            }
        }
    }
}
