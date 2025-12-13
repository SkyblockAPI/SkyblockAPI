package tech.thatgravyboat.skyblockapi.impl.imc

import com.google.gson.JsonObject
import me.owdding.ktmodules.Module
import net.fabricmc.loader.api.FabricLoader
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvOpenedEvent
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvRequired
import java.util.function.Consumer

@Module
@OptIn(SkyBlockPvRequired::class)
object ImcHandler {

    private val PV_PROFILE = registerChannel<JsonObject>("pv-profile")

    @Subscription
    private fun SkyBlockPvOpenedEvent.onPv() = PV_PROFILE.invoke(this.profileData)

    @Suppress("UNCHECKED_CAST")
    private fun <T> registerChannel(channel: String): ((T) -> Unit) {
        val invokers = runCatching { FabricLoader.getInstance()
            .getEntrypoints("skyblockapi:imc/$channel", Consumer::class.java) }
            .onFailure(Throwable::printStackTrace)
            .getOrDefault(listOf())

        return { message: T ->
            for (invoker in invokers) {
                runCatching { (invoker as Consumer<T>).accept(message) }.onFailure(Throwable::printStackTrace)
            }
        }
    }
}
