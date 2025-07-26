package tech.thatgravyboat.skyblockapi.api

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import me.owdding.dfu.item.MeowddingItemDfu
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.ResourceLocation
import org.jetbrains.annotations.ApiStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.RepoVersion
import tech.thatgravyboat.skyblockapi.api.events.base.EventBus
import tech.thatgravyboat.skyblockapi.api.events.misc.RepoStatusEvent
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPIModules
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.impl.DataTypesRegistry
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.nio.file.Files

object SkyBlockAPI : Logger by LoggerFactory.getLogger("SkyBlockAPI") {

    internal val mod = FabricLoader.getInstance().getModContainer("skyblock-api").orElseThrow()

    @JvmStatic
    val eventBus = EventBus()

    internal val logger = this

    internal val isDebug get() = System.getProperty("skyblockapi.debug")?.lowercase() == "true"

    @JvmStatic
    @ApiStatus.Internal
    fun init() {
        SkyblockAPIModules.init { eventBus.register(it) }
        RepoAPI.setup(RepoVersion.fromName(McClient.version) ?: RepoVersion.V1_21_7) { status ->
            RepoStatusEvent(status).post()
        }
        MeowddingItemDfu.load()
    }

    @JvmStatic
    @ApiStatus.Internal
    fun postInit() {
        DataTypesRegistry.load()
    }

    internal fun id(path: String) = ResourceLocation.fromNamespaceAndPath("skyblockapi", path)
    internal fun <C : Any> getRepo(file: String, codec: Codec<C>) =
        mod.findPath("repo/$file.json").orElseThrow()?.let(Files::readString)?.readJson<JsonElement>().toDataOrThrow(codec)
}
