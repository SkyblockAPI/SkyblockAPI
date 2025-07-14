package tech.thatgravyboat.skyblockapi.api.profile.mining.forge

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.profile.items.forge.ForgeAPI as NewForgeApi

@RemoveNextVersion
object ForgeAPI {
    fun getForgeSlots(): Map<Int, ForgeSlot> = NewForgeApi.getForgeSlots().map { (key, data) -> key to ForgeSlot.fromNewData(data) }.toMap()
}
