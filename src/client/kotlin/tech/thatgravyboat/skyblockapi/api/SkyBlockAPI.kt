package tech.thatgravyboat.skyblockapi.api

import com.mojang.logging.LogUtils
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.RepoVersion
import tech.thatgravyboat.skyblockapi.api.events.base.EventBus
import tech.thatgravyboat.skyblockapi.generated.Modules
import tech.thatgravyboat.skyblockapi.impl.DataTypesRegistry

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
}
