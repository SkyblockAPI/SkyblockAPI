package tech.thatgravyboat.skyblockapi.api.events.entity

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerInfo
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent


abstract class SlayerEvent(open val slayerInfo: SlayerInfo): SkyBlockEvent()
data class SlayerInfoLineAttachEvent(val component: Component, val infoLineEntity: Entity, override val slayerInfo: SlayerInfo) : SlayerEvent(slayerInfo)
data class SlayerInfoLineChangeEvent(val component: Component, val infoLineEntity: Entity, override val slayerInfo: SlayerInfo) : SlayerEvent(slayerInfo)
