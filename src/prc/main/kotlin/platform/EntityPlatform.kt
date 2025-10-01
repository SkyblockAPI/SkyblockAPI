@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.nbt.CompoundTag
import net.minecraft.util.ProblemReporter
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.storage.TagValueOutput
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI

actual fun Entity.save(): CompoundTag {
    val collector = ProblemReporter.ScopedCollector(SkyBlockAPI)
    val valueOutput = TagValueOutput.createWithoutContext(collector)
    valueOutput.putString("id", EntityType.getKey(this.type).toString())
    this.saveWithoutId(valueOutput)
    collector.close()
    return valueOutput.buildResult()
}
