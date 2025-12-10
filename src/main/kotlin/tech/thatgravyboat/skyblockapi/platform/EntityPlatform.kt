package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.nbt.CompoundTag
import net.minecraft.util.ProblemReporter
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI

//? if > 1.21.5
import net.minecraft.world.level.storage.TagValueOutput

fun Entity.save(): CompoundTag {
    //? if > 1.21.5 {

    val collector = ProblemReporter.ScopedCollector(SkyBlockAPI)
    val valueOutput = TagValueOutput.createWithoutContext(collector)
    valueOutput.putString("id", EntityType.getKey(this.type).toString())
    this.saveWithoutId(valueOutput)
    collector.close()
    return valueOutput.buildResult()
    //?} else {

    /*val tag = CompoundTag()
    tag.putString("id", EntityType.getKey(this.type).toString())
    return this.saveWithoutId(tag)
    *///?}
}

