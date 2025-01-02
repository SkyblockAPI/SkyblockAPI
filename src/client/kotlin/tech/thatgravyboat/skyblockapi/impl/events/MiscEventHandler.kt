package tech.thatgravyboat.skyblockapi.impl.events

import com.google.common.cache.CacheBuilder
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionResult
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ActionBarReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.level.*
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ItemDebugTooltipEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.impl.events.chat.ChatComponentExtension
import tech.thatgravyboat.skyblockapi.modules.Module

@Module
object MiscEventHandler {

    private val phase = ResourceLocation.fromNamespaceAndPath("skyblockapi", "phase")

    private val blocksClicked = CacheBuilder.newBuilder()
        .maximumSize(50)
        .build<BlockPos, Unit>()

    init {
        ClientTickEvents.END_CLIENT_TICK.register {
            TickEvent.post(SkyBlockAPI.eventBus)
        }
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            RegisterCommandsEvent(dispatcher).post(SkyBlockAPI.eventBus)
        }
        ItemTooltipCallback.EVENT.register { stack, _, flags, list ->
            if (flags.isAdvanced) {
                ItemDebugTooltipEvent(stack, list).post(SkyBlockAPI.eventBus)
            }
        }
        UseItemCallback.EVENT.register { player, _, hand ->
            val stack = player.getItemInHand(hand)
            if (RightClickItemEvent(stack).post(SkyBlockAPI.eventBus)) {
                InteractionResult.FAIL
            }
            InteractionResult.PASS
        }
        UseBlockCallback.EVENT.register { player, _, hand, result ->
            val stack = player.getItemInHand(hand)
            if (RightClickBlockEvent(result.blockPos, stack).post(SkyBlockAPI.eventBus)) {
                InteractionResult.FAIL
            }
            InteractionResult.PASS
        }
        UseEntityCallback.EVENT.register { player, _, hand, entity, _ ->
            val stack = player.getItemInHand(hand)
            if (RightClickEntityEvent(entity, stack).post(SkyBlockAPI.eventBus)) {
                InteractionResult.FAIL
            }
            InteractionResult.PASS
        }
        AttackEntityCallback.EVENT.register { player, _, hand, entity, _ ->
            val stack = player.getItemInHand(hand)
            if (LeftClickEntityEvent(entity, stack).post(SkyBlockAPI.eventBus)) {
                InteractionResult.FAIL
            }
            InteractionResult.PASS
        }
        AttackBlockCallback.EVENT.register { _, _, _, pos, _ ->
            blocksClicked.put(pos, Unit)
            InteractionResult.PASS
        }

        // Chat Events
        ClientReceiveMessageEvents.ALLOW_GAME.addPhaseOrdering(phase, Event.DEFAULT_PHASE)
        ClientReceiveMessageEvents.MODIFY_GAME.addPhaseOrdering(phase, Event.DEFAULT_PHASE)

        ClientReceiveMessageEvents.ALLOW_GAME.register(phase) { message, overlay ->
            if (overlay) {
                !ActionBarReceivedEvent.Pre(message).post()
            } else if (ChatReceivedEvent.Pre(message).post()) {
                SkyBlockAPI.logger.info("[Cancelled] [CHAT] {}", message.string)
                false
            } else {
                true
            }
        }
        ClientReceiveMessageEvents.MODIFY_GAME.register(phase) { message, overlay ->
            if (overlay) {
                ActionBarReceivedEvent.Post(message).let { event ->
                    event.post()
                    event.component
                }
            } else {
                ChatReceivedEvent.Post(message).let { event ->
                    (McClient.self.gui.chat as ChatComponentExtension).`skyblockapi$setIdForMessage`(event.id)
                    event.component
                }
            }
        }
    }

    @Subscription
    fun onBlockChange(event: BlockChangeEvent) {
        if (blocksClicked.getIfPresent(event.pos) != null && event.state.isAir) {
            blocksClicked.invalidate(event.pos)
            BlockMinedEvent(event.pos, McLevel[event.pos]).post(SkyBlockAPI.eventBus)
        }
    }
}
