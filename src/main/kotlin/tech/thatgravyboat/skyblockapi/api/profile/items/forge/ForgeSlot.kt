package tech.thatgravyboat.skyblockapi.api.profile.items.forge

import me.owdding.ktcodecs.GenerateCodec
//? < 26.1 {
// import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import kotlin.time.Instant

@GenerateCodec
data class ForgeSlot(
    val skyBlockId: SkyBlockId,
    val expiryTime: Instant,
) {
    //? < 26.1 {
    /*
    @Deprecated("Use skyBlockId property instead")
    @RemoveNextVersion
    val id get() = skyBlockId.skyblockId
     *///? }
}
