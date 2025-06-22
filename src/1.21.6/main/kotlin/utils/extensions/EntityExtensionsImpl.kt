package tech.thatgravyboat.skyblockapi.utils.extentions

import net.minecraft.nbt.CompoundTag
import net.minecraft.util.ProblemReporter
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.storage.TagValueOutput
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI

actual fun Entity.save(): CompoundTag {
    ProblemReporter.ScopedCollector(SkyBlockAPI).use {
        val valueOutput = TagValueOutput.createWithoutContext(it)
        valueOutput.putString("id", EntityType.getKey(this.type).toString());
        this.saveWithoutId(valueOutput)
        return valueOutput.buildResult()
    }
}
