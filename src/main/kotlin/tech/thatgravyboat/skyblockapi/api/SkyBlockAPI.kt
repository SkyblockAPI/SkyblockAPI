package tech.thatgravyboat.skyblockapi.api

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import me.owdding.dfu.item.MeowddingItemDfu
import me.owdding.ktmodules.Module
import net.fabricmc.loader.api.FabricLoader
import org.jetbrains.annotations.ApiStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.RepoLibLogger
import tech.thatgravyboat.repolib.api.RepoVersion
import tech.thatgravyboat.skyblockapi.api.events.base.EventBus
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiDebugEvent
//? < 26.3
//import tech.thatgravyboat.skyblockapi.api.events.misc.RepoStatusEvent
import tech.thatgravyboat.skyblockapi.api.events.repo.RepoEvent
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPIApiDebug
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPIDevModules
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPIModules
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.impl.DataTypesRegistry
import tech.thatgravyboat.skyblockapi.impl.RepoLibLogging
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import tech.thatgravyboat.skyblockapi.utils.ApiDebug
import tech.thatgravyboat.skyblockapi.utils.SkyBlockApiDevUtils
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.nio.file.Files

@Module
object SkyBlockAPI : Logger by LoggerFactory.getLogger("SkyBlockAPI") {

    internal val mod = FabricLoader.getInstance().getModContainer("skyblock-api").orElseThrow()
    val MOD_ID: String get() = mod.metadata.id
    const val NAMESPACE: String = "skyblockapi"

    @JvmStatic
    val eventBus = EventBus()

    internal val logger = this

    internal val isDebug get() = System.getProperty("skyblockapi.debug")?.lowercase() == "true"

    @JvmStatic
    @ApiStatus.Internal
    fun init() {
        debug("Starting sbapi!")
        RepoLibLogger.setInstance(RepoLibLogging)
        SkyblockAPIModules.init { eventBus.register(it) }
        SkyBlockApiDevUtils.init()
        if (McClient.isDev) SkyblockAPIDevModules.init(eventBus::register)
        RepoAPI.setup(RepoVersion.fromName(McClient.version) ?: RepoVersion.V1_21_7) { status ->
            //? < 26.3
            //RepoStatusEvent(status).post()
            RepoEvent.Status(status).post()
            RepoEvent.Reload(status).post()
        }
        MeowddingItemDfu.load()
    }

    @JvmStatic
    @ApiStatus.Internal
    fun postInit() {
        DataTypesRegistry.load()
    }

    internal fun id(path: String) = Identifiers.of(NAMESPACE, path)
    internal fun <C : Any> getRepo(file: String, codec: Codec<C>) =
        mod.findPath("repo/$file.json").orElseThrow()?.let(Files::readString)?.readJson<JsonElement>().toDataOrThrow(codec)

    @Subscription
    private fun RegisterCommandsEvent.registerSkyblockApiCommands() {
        val event = RegisterSkyblockApiCommandsEvent(this)
        event.post()
        RegisterSkyblockApiDebugEvent(event).post()
    }

    @Subscription
    private fun registerDebugs(event: RegisterSkyblockApiDebugEvent) {
        SkyblockAPIApiDebug.collected.forEach {
            val debug = it.annotations.filterIsInstance<ApiDebug>().first()
            val name = debug.name
            val commandName = debug.commandName.takeUnless(String::isEmpty) ?: name.lowercase().replace(" ", "_")

            event.register(name, commandName) {
                it.invoke(this)
            }
        }
    }
}
