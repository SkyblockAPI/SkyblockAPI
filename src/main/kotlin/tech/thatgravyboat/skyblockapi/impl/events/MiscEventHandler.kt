package tech.thatgravyboat.skyblockapi.impl.events

import com.google.common.cache.CacheBuilder
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.player.*
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.area.mining.MiningBlockFamily
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ActionBarReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.level.*
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ItemDebugTooltipEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.impl.events.chat.ChatComponentExtension
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Module
object MiscEventHandler {

    private val phase = Identifiers.of("skyblockapi", "phase")

    private val blocksClicked = CacheBuilder.newBuilder()
        .maximumSize(50)
        .expireAfterWrite(5.seconds.toJavaDuration())
        .build<BlockPos, Unit>()
    private var lastBlockClicked: BlockPos = BlockPos.ZERO

    init {
        ClientTickEvents.END_CLIENT_TICK.register {
            TickEvent.post(SkyBlockAPI.eventBus)
        }
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            RegisterCommandsEvent(dispatcher).post()
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
            } else {
                InteractionResult.PASS
            }
        }
        UseBlockCallback.EVENT.register { player, _, hand, result ->
            val stack = player.getItemInHand(hand)
            if (RightClickBlockEvent(result.blockPos, stack).post(SkyBlockAPI.eventBus)) {
                InteractionResult.FAIL
            } else {
                InteractionResult.PASS
            }
        }
        UseEntityCallback.EVENT.register { player, _, hand, entity, _ ->
            val stack = player.getItemInHand(hand)
            if (RightClickEntityEvent(entity, stack).post(SkyBlockAPI.eventBus)) {
                InteractionResult.FAIL
            } else {
                InteractionResult.PASS
            }
        }
        AttackEntityCallback.EVENT.register { player, _, hand, entity, _ ->
            val stack = player.getItemInHand(hand)
            if (LeftClickEntityEvent(entity, stack).post(SkyBlockAPI.eventBus)) {
                InteractionResult.FAIL
            } else {
                InteractionResult.PASS
            }
        }
        AttackBlockCallback.EVENT.register { player, _, hand, pos, _ ->
            val stack = player.getItemInHand(hand)
            if (LeftClickBlockEvent(pos, stack).post(SkyBlockAPI.eventBus)) {
                return@register InteractionResult.FAIL
            }
            blocksClicked.put(pos, Unit)
            lastBlockClicked = pos
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
                    event.post()
                    (McClient.self.gui.chat as ChatComponentExtension).`skyblockapi$setIdForMessage`(event.id)
                    event.component
                }
            }
        }

        // Server Disconnect
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            ServerDisconnectEvent.post()
        }
    }

    private fun validMineChange(old: Block, new: Block): Boolean {
        if (old == new) return false
        if (old in listOf(Blocks.AIR, Blocks.BEDROCK)) return false
        if (new in listOf(Blocks.AIR, Blocks.BEDROCK)) return true

        if (new == Blocks.COBBLESTONE && old == Blocks.STONE) return true
        if (new == Blocks.STONE && old == Blocks.COBBLESTONE) return false
        if (new == Blocks.POLISHED_DIORITE && old in MiningBlockFamily.MITHRIL.blocks) return true
        if (new == Blocks.STONE && (old in MiningBlockFamily.VANILLA_ORES.blocks || old in MiningBlockFamily.VANILLA_BLOCKS.blocks)) return true
        if (new == Blocks.RED_SANDSTONE && old == Blocks.RED_SAND) return true
        if (new == Blocks.GRAY_TERRACOTTA && old == Blocks.MYCELIUM) return true

        return false
    }

    @Subscription
    fun onBlockChange(event: BlockChangeEvent) {
        if (
            (blocksClicked.getIfPresent(event.pos) != null || event.pos.distSqr(lastBlockClicked) < 25 /* maybe check if 5 block range is good enough */)
            && validMineChange(McLevel[event.pos].block, event.state.block)
        ) {
            blocksClicked.invalidate(event.pos)
            BlockMinedEvent(event.pos, McLevel[event.pos], event.pos != lastBlockClicked).post(SkyBlockAPI.eventBus)
        }
    }
}
