package tech.thatgravyboat.skyblockapi.api.profile.effects

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.EffectsStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabListHeaderFooterChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.DebugBuilder
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.profile.community.CommunityCenterAPI.cookieAteRegex
import tech.thatgravyboat.skyblockapi.utils.ApiDebug
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.contains
import tech.thatgravyboat.skyblockapi.utils.text.Text
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

@Module
public object EffectsAPI {

    private val cookieTabWidgetRegex = RegexGroup.TABLIST_WIDGET.create(
        "effects.cookie",
        "\\s*Cookie Buff: (?<duration>.*)",
    )
    private val cookieInventoryRegex = RegexGroup.INVENTORY.create(
        "effects.cookie.inventory",
        "\\s*Duration: (?<duration>.*)",
    )
    private val cookieNotActiveRegex = RegexGroup.INVENTORY.create(
        "effects.cookie.not_active",
        "\\s*Status: Not active!",
    )
    private val godPotionWidgetRegex = RegexGroup.TABLIST_WIDGET.create(
        "effects.god_potion",
        "\\s*God Potion: (?<duration>.*)",
    )
    private val godPotionFooterRegex = RegexGroup.TABLIST.create(
        "effects.god_potion.footer",
        "You have a God Potion active! (?<duration>.*)",
    )


    public val boosterCookieExpireTime get() = EffectsStorage.boosterCookieExpireTime
    public val godPotionDuration get() = EffectsStorage.godPotionDuration

    public val isBoosterCookieActive get() = EffectsStorage.boosterCookieExpireTime.until().isPositive()
    public val isGodPotionActive get() = EffectsStorage.godPotionDuration.isPositive()

    @Subscription
    public fun onChat(event: ChatReceivedEvent.Pre) {
        if (cookieAteRegex.contains(event.text)) {
            updateBoosterCookieExpireTime(boosterCookieExpireTime.until() + 4.days)
        }
    }

    @Subscription
    public fun onInventoryFullyLoaded(event: ContainerInitializedEvent) {
        if (event.title == "SkyBlock Menu") {
            val cookieLore = event.itemStacks.find { it.cleanName == "Booster Cookie" }?.getRawLore() ?: return
            cookieInventoryRegex.anyMatch(cookieLore, "duration") { (duration) ->
                val parsedDuration = duration.parseDuration() ?: return@anyMatch
                updateBoosterCookieExpireTime(parsedDuration)
            }
            cookieNotActiveRegex.anyMatch(cookieLore) {
                EffectsStorage.boosterCookieExpireTime = Instant.DISTANT_PAST
            }
        }
    }

    @Subscription
    public fun onTabFooterUpdate(event: TabListHeaderFooterChangeEvent) {
        val cookieBuffChunk = event.newFooterChunked.find { "Cookie Buff" in it }
        cookieBuffChunk?.last()?.let {
            val parsedDuration = it.parseWordDuration() ?: return@let
            updateBoosterCookieExpireTime(parsedDuration)
        }

        godPotionFooterRegex.anyMatch(event.newFooterChunked.flatten(), "duration") { (duration) ->
            val parsedDuration = duration.parseDuration() ?: return@anyMatch
            EffectsStorage.godPotionDuration = parsedDuration
        }
    }

    @Subscription
    @OnlyWidget(TabWidget.ACTIVE_EFFECTS)
    public fun onTabWidgetUpdate(event: TabWidgetChangeEvent) {
        cookieTabWidgetRegex.anyMatch(event.new, "duration") { (duration) ->
            val parsedDuration = duration.parseDuration() ?: return@anyMatch
            updateBoosterCookieExpireTime(parsedDuration)
        }
        godPotionWidgetRegex.anyMatch(event.new, "duration") { (duration) ->
            val parsedDuration = duration.parseDuration() ?: return@anyMatch
            EffectsStorage.godPotionDuration = parsedDuration
        }
    }

    private fun updateBoosterCookieExpireTime(parsedDuration: Duration) {
        val expireTime = parsedDuration.fromNow()

        // Check if the new expiry time is greater (more accurate) than the current one
        if (expireTime > boosterCookieExpireTime) {
            EffectsStorage.boosterCookieExpireTime = expireTime
        }
    }

    @ApiDebug("Effects")
    internal fun debug(builder: DebugBuilder) = with(builder) {
        fields(::boosterCookieExpireTime, ::godPotionDuration)
    }

    @Subscription
    internal fun onCommandsRegistration(event: RegisterSkyblockApiCommandsEvent) {
        event.registerWithCallback("sbapi effects reset") {
            EffectsStorage.boosterCookieExpireTime = Instant.DISTANT_PAST
            EffectsStorage.godPotionDuration = Duration.ZERO
            Text.sendDebug("Reset Effects Data.")
        }
    }
}
