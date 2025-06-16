package tech.thatgravyboat.skyblockapi.utils.extensions

import net.minecraft.nbt.CompoundTag
import net.minecraft.util.ProblemReporter
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.storage.TagValueOutput
import net.msrandom.stub.Stub
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI

@Stub
actual fun Entity.saveWithoutId(tag: CompoundTag): CompoundTag {
    ProblemReporter.ScopedCollector(SkyBlockAPI).use {
        val valueOutput = TagValueOutput.createWithoutContext(it)
        this.saveWithoutId(valueOutput)
        return valueOutput.buildResult()
    }
}
