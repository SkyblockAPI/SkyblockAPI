package tech.thatgravyboat.skyblockapi.api.profile.skillxp

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.SkillXpLiteralActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.SkillXpPercentActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.modules.Module

@Module
object SkillAPI {

    @Subscription
    fun onActionbarLiteral(event: SkillXpLiteralActionBarWidgetChangeEvent) {

    }

    @Subscription
    fun onActionbarPercent(event: SkillXpPercentActionBarWidgetChangeEvent) {

    }
}
