package tech.thatgravyboat.skyblockapi.api.repo.v2

import com.google.common.hash.Hashing
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.mojang.authlib.properties.Property
import com.mojang.datafixers.util.Either
import com.mojang.serialization.DataResult
import me.owdding.ktmodules.Module
import net.minecraft.client.Minecraft
import net.minecraft.commands.arguments.NbtTagArgument
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.ByteArrayTag
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.EndTag
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.IntArrayTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.LongArrayTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.ShortTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.util.Util
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.component.DyedItemColor
import net.minecraft.world.item.component.ItemLore
import org.apache.logging.log4j.core.tools.picocli.CommandLine
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tech.thatgravyboat.repolib.v2.RepoInstance
import tech.thatgravyboat.repolib.v2.RepoLoader
import tech.thatgravyboat.repolib.v2.expl.ContentInfo
import tech.thatgravyboat.repolib.v2.expl.value.ArrayValue
import tech.thatgravyboat.repolib.v2.expl.value.BoolValue
import tech.thatgravyboat.repolib.v2.expl.value.MutableArrayValue
import tech.thatgravyboat.repolib.v2.expl.value.MutableStructValue
import tech.thatgravyboat.repolib.v2.expl.value.NumValue
import tech.thatgravyboat.repolib.v2.expl.value.StrValue
import tech.thatgravyboat.repolib.v2.expl.value.StructValue
import tech.thatgravyboat.repolib.v2.expl.value.Value
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.impl.commands.GiveCommands
import tech.thatgravyboat.skyblockapi.platform.GameProfile
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import tech.thatgravyboat.skyblockapi.platform.toResolvableProfile
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.extentions.since
import tech.thatgravyboat.skyblockapi.utils.extentions.toReadableTime
import tech.thatgravyboat.skyblockapi.utils.http.Http
import tech.thatgravyboat.skyblockapi.utils.http.Http.connect
import tech.thatgravyboat.skyblockapi.utils.http.Http.get
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent
import tech.thatgravyboat.skyblockapi.utils.text.Text.sendWithPrefix
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.bold
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.font
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.obfuscated
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.shadowColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.strikethrough
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.underlined
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.InvalidPathException
import java.nio.file.LinkOption
import java.nio.file.Path
import java.util.Optional
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.notExists
import kotlin.io.path.readBytes
import kotlin.io.path.readSymbolicLink
import kotlin.io.path.readText
import kotlin.io.path.relativeToOrNull
import kotlin.io.path.writeText
import kotlin.jvm.optionals.getOrNull

@RequiresOptIn
annotation class ExperimentalRepo

@ExperimentalRepo
object RepoV2 : Logger by SkyBlockAPI {

    val storageDir: Path = when (Util.getPlatform()) {
        Util.OS.WINDOWS -> Path(System.getenv("APPDATA"))
        Util.OS.OSX -> Path("/Library/Application Support/")
        else -> Path(System.getProperty("user.home")).resolve(".local/share")
    }.resolve("skyblock-api/repo").createDirectories().toRealPath()

    val loader = RepoLoader(storageDir)
    val instance: RepoInstance = loader.create()

    fun load() {
        loader.load().forEach {
            error("Failed to load '{}' due to", it.file, it.reason)
        }
    }

    fun fileHash(path: String) : String? {
        val file = file(path)
        if (file.notExists()) {
            return null
        }
        return Hashing.sha256().hashBytes(file.readBytes()).asBytes().toHexString()
    }
    fun fileContent(path: String) : String? {
        val file = file(path)
        if (file.notExists()) {
            return null
        }
        return file.readText()
    }

    fun file(path: String): Path = storageDir.resolve(path.removePrefix("./")).toAbsolutePath().also {
        if (!it.startsWith(storageDir)) {
            throw InvalidPathException(it.toString(), "Path escapes repo directory!")
        }
    }

    fun parseHashes(content: String?): Map<String, String> {
        if (content == null) return emptyMap()

        return content.lines().mapNotNull {
            if (it.isEmpty()) return@mapNotNull null
            it.substring(66) to it.substring(0, 65)
        }.toMap()
    }

    fun getFromRemote(path: String): String? {
        val result = connect("https://raw.githubusercontent.com/SkyblockAPI/Repo-Data/refs/heads/master/${URLEncoder.encode(path.removePrefix("./"), StandardCharsets.UTF_8)}") {
            GET()
        }

        if (result.statusCode() !in 200..<300) {
            return null
        }

        return result.body().use {
            it.readAllBytes().decodeToString()
        }
    }

    fun checkForUpdates() {
        val indexSha = getFromRemote("index.sha256")?.substringBefore(' ')
        if (indexSha == null) {
            error("Failed to load index sha, using current version!")
            return
        }

        val localIndexSha = fileHash("index")

        if (indexSha == localIndexSha) {
            info("Remote and local are the same, skipping update!")
            return
        }

        info("Hash missmatch, updating repo!")

        val hashFile = getFromRemote("index") ?: return error("Failed to fetch index from remote!")
        val remoteIndex = parseHashes(hashFile)
        val currentIndex = parseHashes(fileContent("index"))

        val remoteFiles = remoteIndex.keys
        val localFiles = currentIndex.keys
        val orphanFiles = localFiles - remoteFiles

        val start = currentInstant()
        val pool = Executors.newFixedThreadPool(5)

        remoteIndex.forEach { (path, hash) ->
            if (currentIndex[path] == hash) return@forEach info("Skipping $path")
            val file = file(path)

            pool.submit {
                val content = getFromRemote(path) ?: return@submit error("Failed to get $path from remote!")
                debug("Writing $path")
                file.createParentDirectories().writeText(content)
            }
        }

        pool.shutdown()
        pool.awaitTermination(2, TimeUnit.MINUTES)
        info("Took ${start.since().toReadableTime(allowMs = true)}")

        orphanFiles.map(::file).forEach {
            it.deleteIfExists()
        }

        file("index").writeText(hashFile)
    }

    init {
        checkForUpdates()
        load()
        SkyBlockAPI.eventBus.register(this)
    }

    internal fun command(event: RegisterSkyblockApiCommandsEvent) {
        event.register("repo_v2_item") {
            thenCallback("give data", NbtTagArgument.nbtTag()) {
                val tag = argument<CompoundTag>("data")
                val item = createItem(tag)

                if (item.isError) {
                    val error = item.error().get().message()
                    Text.of("Failed to create item") {
                        this.hover = error.asComponent()
                    }.sendWithPrefix()

                    error(error)
                    return@thenCallback
                }

                val stack = item.orThrow
                GiveCommands.tryGive(stack.create())
            }
            thenCallback("reload") {
                load()
            }
        }
    }

    fun createItem(data: JsonObject): DataResult<LazyItemStack> = createItem(data.toRepoStruct())
    fun createItem(data: CompoundTag): DataResult<LazyItemStack> = createItem(data.toRepoStruct(), data)

    fun createItem(data: StructValue, nbtData: CompoundTag = data.toNbt()): DataResult<LazyItemStack> {
        val stack = instance.createStack(data) ?: return DataResult.error { "Result is null." }

        val errors = stack.error
        if (errors.isNotEmpty()) {
            return DataResult.error { (errors.joinToString(";") { it.render() }) }
        }
        stack.debug.forEach {
            debug(it.render())
        }

        return toStack(stack.stack, nbtData)
    }

    private fun toStack(data: StructValue, nbtData: CompoundTag = data.toNbt()): DataResult<LazyItemStack> {
        val item = data.get("item").asString() ?: return DataResult.error { "Stack doesn't set any base item." }
        val baseItem = BuiltInRegistries.ITEM.get(Identifiers.parse(item))?.getOrNull() ?: return DataResult.error { "Invalid item id $item!" }

        val lore = data.get("lore")?.asArray()?.mapNotNull { it.asComponent() } ?: return DataResult.error { "No lore on stack." }
        val itemName = data.get("name")?.asComponent() ?: return DataResult.error { "Item name can't be empty!" }

        return DataResult.success(
            LazyItemStack(baseItem.value(), 1) {
                this[DataComponents.CUSTOM_NAME] = itemName
                this[DataComponents.LORE] = ItemLore(lore, lore)

                data.get("minecraft:item_model")?.asString()?.let(Identifiers::parse)?.let {
                    this[DataComponents.ITEM_MODEL] = it
                }
                this[DataComponents.CUSTOM_DATA] = CustomData.of(nbtData)
                data.get("enchanted")?.asBool()?.let {
                    this[DataComponents.ENCHANTMENT_GLINT_OVERRIDE] = it
                }
                data.get("color")?.asNum()?.toInt()?.let {
                    this[DataComponents.DYED_COLOR] = DyedItemColor(it)
                }
                data.get("skin")?.asString()?.let {
                    this[DataComponents.PROFILE] = GameProfile {
                        put("textures", Property("textures", it))
                    }.toResolvableProfile()
                }
            },
        )
    }

    private fun Value?.asComponent(): Component? {
        this.asString()?.let {
            return Text.of(it)
        }

        this.asArray()?.let {
            return Text.join(it.map { it.asComponent() })
        }

        val struct = this.asStruct() ?: return null
        if (!struct.iterator().hasNext()) return CommonComponents.EMPTY

        val text = struct.get("text").asString() ?: ""
        return Text.of(text) {

            struct.get("color").asTextColor()?.let { this.color = it }
            struct.get("shadow_color").asTextColor()?.let { this.shadowColor = it }
            struct.get("bold")?.asBool()?.let { this.bold = it }
            this.italic = false
            struct.get("italic")?.asBool()?.let { this.italic = it }
            struct.get("obfuscated")?.asBool()?.let { this.obfuscated = it }
            struct.get("strikethrough")?.asBool()?.let { this.strikethrough = it }
            struct.get("underlined")?.asBool()?.let { this.underlined = it }
            struct.get("font")?.asString()?.let(Identifiers::parse)?.let { this.font = it }

            struct.get("extra")?.asArray()?.forEach {
                append(it.asComponent() ?: return@forEach)
            }
        }
    }

    private fun Value?.asTextColor(): Int? {
        val color = this.asString() ?: return null
        return when (color.lowercase()) {
            "black" -> TextColor.BLACK
            "dark_blue" -> TextColor.DARK_BLUE
            "dark_green" -> TextColor.DARK_GREEN
            "cyan" -> TextColor.DARK_AQUA
            "lime" -> TextColor.GREEN
            "dark_aqua" -> TextColor.DARK_AQUA
            "dark_red" -> TextColor.DARK_RED
            "dark_purple" -> TextColor.DARK_PURPLE
            "magenta" -> TextColor.MAGENTA
            "gold" -> TextColor.GOLD
            "orange" -> TextColor.ORANGE
            "gray" -> TextColor.GRAY
            "dark_gray" -> TextColor.DARK_GRAY
            "blue" -> TextColor.BLUE
            "green" -> TextColor.GREEN
            "aqua" -> TextColor.AQUA
            "red" -> TextColor.RED
            "light_purple" -> TextColor.LIGHT_PURPLE
            "pink" -> TextColor.PINK
            "yellow" -> TextColor.YELLOW
            "white" -> TextColor.WHITE
            else if color.startsWith("#") -> color.substring(1).toInt(16)
            else -> null
        }
    }

    private fun Value?.asString() = (this as? StrValue)?.value
    private fun Value?.asNum() = (this as? NumValue)?.value
    private fun Value?.asBool() = (this as? BoolValue)?.value()
    private fun Value?.asStruct() = (this as? StructValue)
    private fun Value?.asArray() = (this as? ArrayValue)?.toList()
}

private fun ContentInfo.render() = "{Stack: <${this.stack}>, Message: '${this.message}'}"

fun StructValue.toNbt(): CompoundTag {
    val tag = CompoundTag()
    for ((key, value) in this) {
        tag.put(key, value.toTag() ?: continue)
    }

    return tag
}

fun Value.toTag(): Tag? = when (this) {
    is ArrayValue -> ListTag().also {
        it.addAll(this.map { tag -> tag.toTag() })
    }

    is StructValue -> this.toNbt()
    is BoolValue -> ByteTag.valueOf(value())
    is NumValue -> DoubleTag.valueOf(value)
    is StrValue -> StringTag.valueOf(value)
    else -> null
}

private fun JsonObject.toRepoStruct(): StructValue {
    val struct = MutableStructValue()

    for ((key, value) in this.entrySet()) {
        struct.set(key, value.toRepo())
    }

    return struct
}

private fun JsonElement.toRepo(): Value = when (this) {
    is JsonObject -> this.toRepoStruct()
    is JsonArray -> MutableArrayValue.create(this.map { it.toRepo() })
    is JsonPrimitive if this.isNumber -> NumValue(this.asNumber.toDouble())
    is JsonPrimitive if this.isBoolean -> BoolValue.wrap(this.asBoolean)
    is JsonPrimitive if this.isString -> StrValue(this.asString)
    else -> Value.NIL
}

private fun CompoundTag.toRepoStruct(): StructValue {
    val struct = MutableStructValue()

    for ((key, value) in this.entrySet()) {
        struct.set(key, value.toRepo())
    }

    return struct
}

private fun Tag.toRepo(): Value = when (this) {
    is ByteArrayTag -> MutableArrayValue.create(this.asByteArray.toList().map { NumValue(it.toDouble()) })
    is IntArrayTag -> MutableArrayValue.create(this.asIntArray.toList().map { NumValue(it.toDouble()) })
    is LongArrayTag -> MutableArrayValue.create(this.asLongArray.toList().map { NumValue(it.toDouble()) })
    is ListTag -> MutableArrayValue.create(this.map { it.toRepo() })
    is CompoundTag -> this.toRepoStruct()
    is EndTag -> Value.NIL
    is ByteTag -> NumValue(this.byteValue().toDouble())
    is DoubleTag -> NumValue(this.doubleValue())
    is FloatTag -> NumValue(this.floatValue().toDouble())
    is IntTag -> NumValue(this.intValue().toDouble())
    is LongTag -> NumValue(this.longValue().toDouble())
    is ShortTag -> NumValue(this.shortValue().toDouble())
    is StringTag -> StrValue(this.value())
}
