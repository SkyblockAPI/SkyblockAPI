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
import tech.thatgravyboat.skyblockapi.utils.extentions.toLongValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match

@Module
object SkillXpAPI {

    val skills: Map<HypixelSkillAPI.Skill, Long>? get() = SkillXpStorage.data?.xp

    private val regex = RegexGroup.INVENTORY.create("aaa", "(?<name>.*) (?<level>\\d+)")

    @Subscription
    @OnlyOnSkyBlock
    @InventoryTitle("Your Skills")
    fun onInventory(event: InventoryChangeEvent) {
        if (!event.isInMainPart) return
        if (event.isSkyBlockFiller) return

        regex.match(event.item.cleanName, "name", "level") { (name, level) ->
            val skill = HypixelSkillAPI.Skill.getByName(name) ?: return@match
            val xp = level.toLongValue() * 150L

            SkillXpStorage.setXp(skill, xp)
        }
    }

    @Subscription
    fun onActionbarLiteral(event: SkillXpLiteralActionBarWidgetChangeEvent) {

    }

    @Subscription
    fun onActionbarPercent(event: SkillXpPercentActionBarWidgetChangeEvent) {

    }
}
