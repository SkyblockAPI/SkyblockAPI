package tech.thatgravyboat.skyblockapi.api.profile.effects

import kotlinx.datetime.Instant
import tech.thatgravyboat.skyblockapi.api.data.stored.EffectsStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabListHeaderFooterChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.profile.community.CommunityCenterAPI.cookieAteRegex
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.parseDuration
import tech.thatgravyboat.skyblockapi.utils.extentions.parseWordDuration
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.contains
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.time.fromNow
import tech.thatgravyboat.skyblockapi.utils.time.until
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

@Module
object EffectsAPI {

    private val cookieTabWidgetRegex = RegexGroup.TABLIST_WIDGET.create(
        "effects.cookie",
        "\\s*Cookie Buff: (?<duration>.*)",
    )
    private val cookieInventoryRegex = RegexGroup.INVENTORY.create(
        "effects.cookie",
        "Duration: (?<duration>.*)",
    )
    private val godPotionWidgetRegex = RegexGroup.TABLIST_WIDGET.create(
        "effects.god_potion",
        "\\s*God Potion: (?<duration>.*)",
    )
    private val godPotionFooterRegex = RegexGroup.TABLIST.create(
        "effects.god_potion.footer",
        "You have a God Potion active! (?<duration>.*)",
    )


    val boosterCookieExpireTime get() = EffectsStorage.boosterCookieExpireTime
    val godPotionDuration get() = EffectsStorage.godPotionDuration


    @Subscription
    fun onChat(event: ChatReceivedEvent.Pre) {
        if (cookieAteRegex.contains(event.text)) {
            updateBoosterCookieExpireTime(boosterCookieExpireTime.until() + 4.days)
        }
    }

    @Subscription
    fun onInventoryFullyLoaded(event: ContainerInitializedEvent) {
        if (event.title == "SkyBlock Menu") {
            val cookieLore = event.itemStacks.find { it.cleanName == "Booster Cookie" }?.getRawLore() ?: return
            cookieInventoryRegex.anyMatch(cookieLore, "duration") { (duration) ->
                val parsedDuration = duration.parseDuration() ?: return@anyMatch
                updateBoosterCookieExpireTime(parsedDuration)
            }
        }
    }

    @Subscription
    fun onTabFooterUpdate(event: TabListHeaderFooterChangeEvent) {
        val cookieBuffChunk = event.newFooterChunked.find { "Cookie Buff" in it }
        cookieBuffChunk?.last()?.let {
            val parsedDuration = it.parseWordDuration() ?: return@let
            updateBoosterCookieExpireTime(parsedDuration)
        }

        godPotionFooterRegex.anyMatch(event.newFooterChunked.flatten(), "duration") { (duration) ->
            val parsedDuration = duration.parseWordDuration() ?: return@anyMatch
            EffectsStorage.godPotionDuration = parsedDuration
        }
    }

    @Subscription
    @OnlyWidget(TabWidget.ACTIVE_EFFECTS)
    fun onTabWidgetUpdate(event: TabWidgetChangeEvent) {
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


    @Subscription
    fun onCommandsRegistration(event: RegisterCommandsEvent) {
        event.register("sbapi") {
            then("effects") {
                then("copy") {
                    callback {
                        val effects = listOf(
                            "Booster Cookie Expire Time: $boosterCookieExpireTime",
                            "God Potion Duration: $godPotionDuration",
                        )

                        Text.of("[SkyBlockAPI] Copied Effects Data to clipboard.") {
                            this.color = TextColor.YELLOW
                        }.send()

                        McClient.clipboard = effects.joinToString("\n")
                    }
                }
                then("reset") {
                    callback {
                        EffectsStorage.boosterCookieExpireTime = Instant.DISTANT_PAST
                        EffectsStorage.godPotionDuration = Duration.ZERO
                        Text.of("[SkyBlockAPI] Reset Effects Data.") {
                            this.color = TextColor.YELLOW
                        }.send()
                    }
                }
            }
        }
    }
}
