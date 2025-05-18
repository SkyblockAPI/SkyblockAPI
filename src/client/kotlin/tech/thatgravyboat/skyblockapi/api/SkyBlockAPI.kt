package tech.thatgravyboat.skyblockapi.api

import com.google.gson.JsonElement
import com.mojang.logging.LogUtils
import com.mojang.serialization.Codec
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.RepoVersion
import tech.thatgravyboat.skyblockapi.api.events.base.EventBus
import tech.thatgravyboat.skyblockapi.generated.Modules
import tech.thatgravyboat.skyblockapi.impl.DataTypesRegistry
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.nio.file.Files

object SkyBlockAPI {

    internal val mod = FabricLoader.getInstance().getModContainer("skyblock-api").orElseThrow()

    @JvmStatic
    val eventBus = EventBus()

    internal val logger = LogUtils.getLogger()

    internal val isDebug get() = System.getProperty("skyblockapi.debug")?.lowercase() == "true"

    @JvmStatic
    internal fun init() {
        Modules.load()
        RepoAPI.setup(RepoVersion.V1_21_5)
    }

    @JvmStatic
    internal fun postInit() {
        DataTypesRegistry.load()
    }

    internal fun id(path: String) = ResourceLocation.fromNamespaceAndPath("skyblockapi", path)
    internal fun <C : Any> getRepo(file: String, codec: Codec<C>) =
        mod.findPath("repo/$file.json").orElseThrow()?.let(Files::readString)?.readJson<JsonElement>().toDataOrThrow(codec)
}
