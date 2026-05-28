package tech.thatgravyboat.skyblockapi.impl.commands

import com.google.gson.JsonElement
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.commands.arguments.NbtTagArgument
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.component.ItemContainerContents
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.api.remote.api.SimpleItemAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.impl.ColoredItems
import tech.thatgravyboat.skyblockapi.impl.suggestion.IterableSuggestionProvider
import tech.thatgravyboat.skyblockapi.utils.builders.ItemBuilder
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getLore
import tech.thatgravyboat.skyblockapi.utils.extentions.getSkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.sanitizeForCommandInput
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.Text.sendWithPrefix
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.onClick
import java.util.concurrent.CompletableFuture
import kotlin.math.max
import kotlin.math.min

@Module
object GiveCommands {

    const val red = 0xf38ba8
    const val darkRed = 0xe78284
    const val peach = 0xfab387
    const val yellow = 0xe5c890
    const val green = 0xa6e3a1
    const val text = 0xc6d0f5
    const val pink = 0xf5bde6

    @Subscription
    private fun RegisterSkyblockApiCommandsEvent.onRegister() {
        register("dev give item") {
            callback {
                val item = McClient.clipboard.readJson<JsonElement>().toData(ItemStack.CODEC)
                if (item == null) {
                    Text.of("Failed to read item from clipboard!", red).sendWithPrefix("sbapi-dev-give-failed-decode")
                    return@callback
                }
                tryGive(item)
            }

            val allIds = SimpleItemAPI.getAllIds()
            thenCallback("id id", SkyBlockIdArgument(allIds)) {
                val id = argument<SkyBlockId>("id")
                if (!allIds.contains(id)) {
                    Text.of("Unable to find item with id ") {
                        color = red
                        append(id.cleanId, peach)
                        append("!")
                    }.sendWithPrefix("sbapi-dev-give-not-found")
                    return@thenCallback
                }
                tryGive(id.toItem())
            }
            thenCallback("name name", StringArgumentType.greedyString(), IterableSuggestionProvider(SimpleItemAPI.getAllNames())) {
                val name = argument<String>("name")
                val id = SimpleItemAPI.findIdByName(name)
                if (id == null) {
                    Text.of("Unable to find item for name ") {
                        color = red
                        append(name, peach)
                        append("!")
                    }.sendWithPrefix("sbapi-dev-give-not-found")
                    return@thenCallback
                }
                tryGive(id.toItem())
            }
        }
        register("sbapi dev") {
            then("find names") {
                val nameCallback: CommandContext<FabricClientCommandSource>.() -> Unit = {
                    val flags = runCatching { argument<Map<FindFlag, Any>>("flags") }.getOrDefault(emptyMap())
                    val search = argument<String>("filter")
                    findBy(flags, search) { it.toItem().cleanName }
                }
                then("flags", FlagArgument.enum<FindFlag>()) {
                    thenCallback("filter", StringArgumentType.greedyString(), block = nameCallback)
                }
                thenCallback("filter", StringArgumentType.greedyString(), block = nameCallback)
            }

            then("find ids") {
                val idCallback: CommandContext<FabricClientCommandSource>.() -> Unit = {
                    val flags = runCatching { argument<Map<FindFlag, Any>>("flags") }.getOrDefault(emptyMap())
                    val search = argument<String>("filter")
                    findBy(flags, search) { it.id }
                }
                then("flags", FlagArgument.enum<FindFlag>()) {
                    thenCallback("filter", StringArgumentType.greedyString(), block = idCallback)
                }
                thenCallback("filter", StringArgumentType.greedyString(), block = idCallback)
            }
        }
    }

    fun findBy(flags: Map<FindFlag, Any>, search: String, converter: (SkyBlockId) -> String) {
        val caseInsensitive = !flags.containsKey(FindFlag.MATCH_CASE)
        val give = flags.containsKey(FindFlag.GIVE)
        val tag = flags[FindFlag.CUSTOM_DATA] as? Tag
        val searchType: (filter: String, element: String) -> Boolean = when {
            flags.containsKey(FindFlag.REGEX) -> { filter: String, element: String ->
                Regex(
                    filter,
                    buildSet {
                        if (caseInsensitive) add(RegexOption.IGNORE_CASE)
                    },
                ).matches(element)
            }

            flags.containsKey(FindFlag.STARTS_WITH) -> { filter: String, element: String -> element.startsWith(filter, ignoreCase = caseInsensitive) }
            flags.containsKey(FindFlag.ENDS_WITH) -> { filter: String, element: String -> element.endsWith(filter, ignoreCase = caseInsensitive) }
            else -> { filter: String, element: String -> element.contains(filter, ignoreCase = caseInsensitive) }
        }

        val limit = flags.getOrDefault(FindFlag.LIMIT, if (flags.containsKey(FindFlag.ALL)) Int.MAX_VALUE else 100) as Int

        CompletableFuture.runAsync {
            val items = SimpleItemAPI.getAllIds().filter {
                searchType(search, converter(it))
            }
            McClient.runNextTick {
                Text.of("Found ") {
                    color = green
                    append(items.size) {
                        color = peach
                    }
                    append(" items matching the search!")
                }.sendWithPrefix("sbapi-dev-find")
                if (tag !is CompoundTag) {
                    Text.of("Custom data isn't a compound tag, ignoring!", darkRed).sendWithPrefix()
                }

                val limitedItems = items.take(limit).map {
                    ItemBuilder().apply {
                        val original = it.toItem()
                        copyFrom(original)
                        val data = original.get(DataComponents.CUSTOM_DATA)
                        val originTag = data?.copyTag() ?: CompoundTag()
                        set(
                            DataComponents.CUSTOM_DATA,
                            CustomData.of(
                                originTag.apply {
                                    (tag as? CompoundTag)?.forEach { key, value -> this.put(key, value) }
                                },
                            ),
                        )
                    }.build()
                }
                if (!give) {
                    limitedItems.forEachIndexed { index, stack ->
                        Text.of((index + 1).toFormattedString()) {
                            append(". ")
                            color = text
                            append(stack.hoverName) {
                                hover = Text.multiline(stack.getLore())
                                onClick { tryGive(stack) }
                            }
                            append(" [id]") {
                                color = pink
                                onClick {
                                    val location = stack.getSkyBlockId()
                                    if (location == null) {
                                        Text.of("No model id for item!", darkRed).sendWithPrefix("sbapi-dev-find-location-not-found")
                                        return@onClick
                                    }
                                    Text.of("Copied model id to clipboard!", yellow).sendWithPrefix("sbapi-dev-find-copied-location")
                                    McClient.clipboard = location
                                }
                            }
                        }.send("sbapi-find-result-$index")
                    }
                } else {
                    if (limitedItems.size > 20) {
                        fillAndGiveShulkers(limitedItems)
                    } else {
                        limitedItems.forEach { tryGive(it) }
                    }
                }
            }
        }
    }

    fun fillAndGiveShulkers(items: List<ItemStack>) {
        val maxAmount = items.size
        items.chunked(28).mapIndexed { index, items ->
            when ((index + 10) % 16) {
                0 -> ColoredItems.WHITE_SHULKER_BOX
                1 -> ColoredItems.ORANGE_SHULKER_BOX
                2 -> ColoredItems.MAGENTA_SHULKER_BOX
                3 -> ColoredItems.LIGHT_BLUE_SHULKER_BOX
                4 -> ColoredItems.YELLOW_SHULKER_BOX
                5 -> ColoredItems.LIME_SHULKER_BOX
                6 -> ColoredItems.PINK_SHULKER_BOX
                7 -> ColoredItems.GRAY_SHULKER_BOX
                8 -> ColoredItems.LIGHT_GRAY_SHULKER_BOX
                9 -> ColoredItems.CYAN_SHULKER_BOX
                10 -> ColoredItems.PURPLE_SHULKER_BOX
                11 -> ColoredItems.BLUE_SHULKER_BOX
                12 -> ColoredItems.BROWN_SHULKER_BOX
                13 -> ColoredItems.GREEN_SHULKER_BOX
                14 -> ColoredItems.RED_SHULKER_BOX
                15 -> ColoredItems.BLACK_SHULKER_BOX
                else -> TODO("no.")
            }.defaultInstance.apply {
                set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items))
                set(
                    DataComponents.CUSTOM_NAME,
                    Text.of("Items ${index * 27}-${min((index + 1) * 27, maxAmount)}") {
                        italic = false
                    },
                )
            }
        }.forEach(::tryGive)
    }

    private const val OPERATOR_GROUP = "operator"
    private const val LIMIT_GROUP = "limits"

    enum class FindFlag(
        override val shortName: Char,
        longName: String?,
        override val flagType: ArgumentType<*>?,
        override val group: String?,
    ) : CommandFlag {
        REGEX('r'),
        CONTAINS('c'),
        STARTS_WITH('s'),
        ENDS_WITH('e'),
        MATCH_CASE('m', group = null),
        LIMIT('l', IntegerArgumentType.integer(0), LIMIT_GROUP),
        ALL('a', group = LIMIT_GROUP),
        GIVE('g', group = null),
        CUSTOM_DATA('d', NbtTagArgument.nbtTag(), group = null)
        ;

        override val longName = (longName ?: name).lowercase()

        constructor(shortName: Char, argumentType: ArgumentType<*>? = null, group: String? = OPERATOR_GROUP) : this(shortName, null, argumentType, group)
    }

    fun tryGive(itemStack: ItemStack) {
        val item = itemStack.copyWithCount(1)
        // TODO: apparently .isSingleplayer got removed so fuck me ig?
        if (McPlayer.self?.gameMode()?.isCreative != true || /*!McClient.self.isSingleplayer ||*/ McPlayer.self?.hasInfiniteMaterials() != true) {
            Text.of("Not in singleplayer and creative!", red).sendWithPrefix("sbapi-dev-give-singleplayer")
            return
        }
        Text.of("Added ") {
            append(item.hoverName) {
                color = peach
            }
            append(" to your inventory!")
            color = green
        }.sendWithPrefix("sbapi-dev-give-added-${item.getSkyBlockId() ?: item.cleanName}")

        val freeSlot = McClient.self.player?.inventory?.freeSlot ?: -1
        McClient.self.player?.inventory?.setItem(freeSlot, item)
        McClient.connection?.send(ServerboundSetCreativeModeSlotPacket(36 + freeSlot, item))
        McClient.self.player?.containerMenu?.broadcastChanges()
    }

}

private data class SkyBlockIdArgument(val skyblockIds: Iterable<SkyBlockId> = SimpleItemAPI.getAllIds(), val filter: (SkyBlockId) -> Boolean = { true }) :
    ArgumentType<SkyBlockId>, SuggestionProvider<FabricClientCommandSource> {

    override fun parse(reader: StringReader): SkyBlockId {
        val cursor = reader.cursor
        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip()
        }
        val string = reader.string.substring(cursor, reader.cursor)


        return skyblockIds.find { it.id == string.lowercase() } ?: SkyBlockId.unsafe(string.lowercase())
    }

    override fun <S : Any> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        skyblockIds.forEach {
            suggest(builder, it.id.lowercase())
        }
        return builder.buildFuture()
    }

    fun suggest(builder: SuggestionsBuilder, name: String) {
        val filtered = name.sanitizeForCommandInput()
        if (SharedSuggestionProvider.matchesSubStr(builder.remaining.lowercase(), filtered.lowercase())) {
            builder.suggest(filtered)
        }
    }

    override fun getSuggestions(
        p0: CommandContext<FabricClientCommandSource>,
        p1: SuggestionsBuilder,
    ) = listSuggestions(p0, p1)
}

private interface CommandFlag {
    val shortName: Char
    val longName: String
    val flagType: ArgumentType<*>?
    val group: String? get() = null
}

private class FlagArgument<T : CommandFlag>(val flags: Iterable<T>) : ArgumentType<Map<T, Any>> {
    companion object {
        inline fun <reified T> enum(): FlagArgument<T> where T : Enum<T>, T : CommandFlag = FlagArgument(T::class.java.enumConstants.toList())
    }

    override fun parse(reader: StringReader): Map<T, Any> {
        val map = mutableMapOf<T, Any>()
        val minCursor = reader.cursor

        val consumedGroups: MutableSet<String> = mutableSetOf()
        while (reader.canRead() && reader.peek() == '-') {
            val cursor = reader.cursor
            reader.skip()
            if (!reader.canRead()) break
            val filteredFlags = flags.filterUnused(map.keys, consumedGroups)

            val flag = if (reader.peek() == '-') {
                reader.skip()
                val content = reader.readStringUntil(' ')
                reader.cursor -= 1
                filteredFlags.find { it.longName == content }
            } else {
                val content = reader.read()
                filteredFlags.find { it.shortName == content }
            }

            if (flag == null) {
                reader.cursor = max(minCursor, cursor - 1)
                return map
            }
            val flagType = flag.flagType
            flag.group?.let(consumedGroups::add)
            if (flagType == null) {
                map[flag] = Unit
            } else {
                reader.skipWhitespace()
                if (reader.canRead())
                    map[flag] = flagType.parse(reader) as Any
            }
            if (reader.remainingLength >= 2 && reader.peek(1) == '-') reader.skipWhitespace()
        }

        return map
    }

    private fun Iterable<T>.filterUnused(used: Iterable<T>, usedGroups: Set<String>) = this.filterNot { usedGroups.contains(it.group) || used.contains(it) }

    override fun <S> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val reader = StringReader(builder.input)
        reader.cursor = builder.start

        val consumedTypes: MutableSet<T> = mutableSetOf()
        val consumedGroups: MutableSet<String> = mutableSetOf()
        while (reader.canRead() && reader.peek() == '-') {
            reader.skip()
            val cursor = reader.cursor
            val filteredFlags = flags.filterUnused(consumedTypes, consumedGroups)
            if (!reader.canRead()) {
                val offset = builder.createOffset(reader.cursor)
                filteredFlags.forEach {
                    offset.suggest(it.shortName.toString())
                    offset.suggest("-" + it.longName)
                }
                return offset.buildFuture()
            }

            val flag = runCatching {
                if (reader.peek() == '-') {
                    reader.skip()
                    val content = reader.readStringUntil(' ')
                    reader.cursor -= 1
                    filteredFlags.find { it.longName == content }
                } else {
                    val content = reader.read()
                    filteredFlags.find { it.shortName == content }
                }
            }.getOrNull()

            if (flag == null) {
                reader.cursor = cursor
                if (reader.canRead() && reader.peek() == '-') {
                    reader.read()
                    val offset = builder.createOffset(reader.cursor)
                    SharedSuggestionProvider.suggest(filteredFlags.map { it.longName }, offset)
                    return offset.buildFuture()
                }
                return builder.buildFuture()
            }

            consumedTypes.add(flag)
            flag.group?.let(consumedGroups::add)
            val flagType = flag.flagType
            if (flagType != null) {
                reader.skipWhitespace()
                val offset = builder.createOffset(reader.cursor)
                if (reader.canRead()) {
                    flagType.parse(reader)
                }
                if (reader.remainingLength >= 2 && reader.peek(1) == '-') {
                    reader.skipWhitespace()
                    continue
                }
                return flagType.listSuggestions(context, offset)
            }
            if (reader.remainingLength >= 2 && reader.peek(1) == '-') reader.skipWhitespace()
        }

        return super.listSuggestions(context, builder)
    }
}
