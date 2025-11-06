package tech.thatgravyboat.skyblockapi.impl

import com.google.common.hash.Hashing
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.*
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.data.FolderStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.screen.*
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.impl.suggestion.IterableSuggestionProvider
import tech.thatgravyboat.skyblockapi.utils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.filterContainerItems
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.sendWithPrefix
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@Suppress("UnstableApiUsage")
@Module
object ChestDump {

    val enabled by debugToggle("chest_dumps", "Allows you to save inventories by pressing 'S'")

    private val storage = FolderStorage("chest_dumps", ChestDumpStorage.DEFAULT, SkyblockAPICodecs.ChestDumpStorageCodec.codec())

    @Subscription
    fun onKey(event: ScreenKeyPressedEvent.Pre) {
        if (!enabled) return
        if (event.key != InputConstants.KEY_S) return

        val chest = (event.screen as? AbstractContainerScreen<*>)?.menu as? ChestMenu ?: return
        val title = event.screen.title
        val items = chest.slots.filterContainerItems().toMutableList().map { itemStack -> itemStack.copy() }

        val hash = Hashing.sha256().newHasher()
        hash.putUnencodedChars(title.string)
        items.forEach { hash.putUnencodedChars(it.displayName.string) }

        val type = BuiltInRegistries.MENU.getKey(chest.type) ?: run {
            SkyBlockAPI.warn("Unknown menu type: ${chest.type}")
            return
        }

        storage.set(hash.hash().asBytes().toHexString(), ChestDumpStorage(title, items, type))

        Text.of("Dumping ${items.size} items in ${title.stripped}").sendWithPrefix()
    }


    fun <T : AbstractContainerMenu> createScreen(type: MenuType<T>, dump: ChestDumpStorage): Screen = when (type) {
        MenuType.GENERIC_9x1, MenuType.GENERIC_9x2, MenuType.GENERIC_9x3, MenuType.GENERIC_9x4, MenuType.GENERIC_9x5, MenuType.GENERIC_9x6 -> {
            val menu = type.create(-1, McPlayer.self!!.inventory)
            object : ContainerScreen(menu as ChestMenu, McPlayer.self!!.inventory, dump.title) {
                override fun init() {
                    super.init()
                    dump.items.forEachIndexed { index, item -> menu.slots[index].set(item) }

                    ContainerInitializedEvent(menu.slots.map { it.item }, this).post()
                    ScreenInitializedEvent(this).post()
                    dump.items.forEachIndexed { index, item ->
                        InventoryChangeEvent(item, menu.slots[index], dump.title, menu.slots, this).post()
                    }
                }

                override fun onClose() {
                    ContainerCloseEvent.post()
                    super.onClose()
                }

                @Suppress("WRONG_NULLABILITY_FOR_JAVA_OVERRIDE") // it literally crashes if you don't do this so yeah
                override fun slotClicked(slot: Slot?, slotId: Int, mouseButton: Int, type: ClickType?) {
                    SkyBlockAPI.info("<ChestDump> Clicked slot $slotId with button $mouseButton and click type $type")
                }

                override fun handleSlotStateChanged(slotId: Int, containerId: Int, newState: Boolean) {
                    SkyBlockAPI.info("<ChestDump> Slot state changed $slotId with container $containerId and new state $newState")
                }
            }
        }

        else -> throw UnsupportedOperationException("Unsupported menu type: $type")
    }

    fun openDump(dump: ChestDumpStorage) {
        val type = BuiltInRegistries.MENU.getValue(dump.type)!!
        val screen = createScreen(type, dump)
        McClient.setScreenAsync { screen }
    }

    @Subscription
    fun command(event: RegisterCommandsEvent) {
        event.register("sbapi chestdump") {
            val suggestions = IterableSuggestionProvider(storage.getStorages().entries) {
                val dump = it.value.get()
                "${dump.title.stripped}-${it.key.take(3)}"
            }

            fun getDump(id: String) = storage.getAll().entries.find { "${it.value.title.stripped}-${it.key.take(3)}" == id }
            then("open id", StringArgumentType.greedyString(), suggestions) {
                callback {
                    val id = argument<String>("id")

                    val dump = getDump(id!!)?.value ?: run {
                        Text.of("No dump with id $id found.").sendWithPrefix()
                        return@callback
                    }

                    openDump(dump)
                }
            }
            then("delete id", StringArgumentType.greedyString(), suggestions) {
                callback {
                    val id = argument<String>("id")

                    val dump = getDump(id!!)?.key ?: run {
                        Text.of("No dump with id $id found.").sendWithPrefix()
                        return@callback
                    }

                    Text.of("Deleted dump $id").sendWithPrefix()
                    storage.remove(dump)
                }
            }
            thenCallback("refresh") {
                storage.refresh()
                Text.of("Refreshed chest dumps.").sendWithPrefix()
            }
        }
    }

    @GenerateCodec
    data class ChestDumpStorage(
        val title: Component,
        val items: List<ItemStack>,
        val type: ResourceLocation,
    ) {
        companion object {
            val DEFAULT = ChestDumpStorage(CommonText.EMPTY, emptyList(), BuiltInRegistries.MENU.getKey(MenuType.GENERIC_9x6)!!)
        }
    }

}

