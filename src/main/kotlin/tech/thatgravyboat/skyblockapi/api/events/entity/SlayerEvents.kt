package tech.thatgravyboat.skyblockapi.api.events.entity

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerInfo
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent


public abstract class SlayerEvent(public open val slayerInfo: SlayerInfo): SkyBlockEvent()
public data class SlayerInfoLineAttachEvent(val component: Component, val infoLineEntity: Entity, override val slayerInfo: SlayerInfo) : SlayerEvent(slayerInfo)
public data class SlayerInfoLineChangeEvent(val component: Component, val infoLineEntity: Entity, override val slayerInfo: SlayerInfo) : SlayerEvent(slayerInfo)
