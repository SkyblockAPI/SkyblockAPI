package tech.thatgravyboat.skyblockapi.impl.events

import com.google.common.cache.CacheBuilder
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.player.*
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionResult
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.area.mining.MiningBlockFamily
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
        AttackBlockCallback.EVENT.register { player, _, hand, pos, _ ->
            val stack = player.getItemInHand(hand)
            if (LeftClickBlockEvent(pos, stack).post(SkyBlockAPI.eventBus)) {
                InteractionResult.FAIL
            }
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

    private fun validMineChange(old: Block, new: Block): Boolean {
        if (old == new) return false
        if (old in listOf(Blocks.AIR, Blocks.BEDROCK)) return false
        if (new in listOf(Blocks.AIR, Blocks.BEDROCK)) return true

        if (new == Blocks.COBBLESTONE && old == Blocks.STONE) return true
        if (new == Blocks.POLISHED_DIORITE && old in MiningBlockFamily.MITHRIL.getBlocks()) return true
        if (new == Blocks.STONE && (old in MiningBlockFamily.VANILLA_ORES.getBlocks() || old in MiningBlockFamily.VANILLA_BLOCKS.getBlocks())) return true
        if (new == Blocks.RED_SANDSTONE && old == Blocks.RED_SAND) return true
        if (new == Blocks.GRAY_TERRACOTTA && old == Blocks.MYCELIUM) return true

        return false
    }

    @Subscription
    fun onBlockChange(event: BlockChangeEvent) {
        // TODO: check around mines block for efficient miner broken blocks
        if (blocksClicked.getIfPresent(event.pos) != null && validMineChange(McLevel[event.pos].block, event.state.block)) {
            blocksClicked.invalidate(event.pos)
            BlockMinedEvent(event.pos, McLevel[event.pos]).post(SkyBlockAPI.eventBus)
        }
    }
}
