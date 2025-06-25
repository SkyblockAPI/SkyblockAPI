package tech.thatgravyboat.skyblockapi.api.events.minecraft.sounds

import net.minecraft.sounds.SoundEvent
import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.events.base.OffThreadEvent

@OffThreadEvent
data class SoundPlayedEvent(val sound: SoundEvent, val pos: Vec3, val volume: Float, val pitch: Float) : CancellableSkyBlockEvent()
