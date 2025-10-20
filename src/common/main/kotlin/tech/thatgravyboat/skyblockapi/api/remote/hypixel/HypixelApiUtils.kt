@file:OptIn(ExperimentalEncodingApi::class)

package tech.thatgravyboat.skyblockapi.api.remote.hypixel

import com.google.gson.JsonObject
import me.owdding.dfu.item.LegacyDataFixer
import net.minecraft.nbt.NbtAccounter
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.Tag
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.extentions.asInt
import tech.thatgravyboat.skyblockapi.utils.runCatchingWithPrint
import java.io.ByteArrayInputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.jvm.optionals.getOrNull

internal fun JsonObject.parseInvData(): List<ItemStack>? = runCatchingWithPrint {
    when (this.get("type").asInt(-1)) {
        0 -> parseV0InventoryData(this)
        else -> emptyList()
    }
}.getOrNull()

private fun parseV0InventoryData(json: JsonObject): List<ItemStack>? {
    val data = json.get("data").asString
    val tag = NbtIo.readCompressed(ByteArrayInputStream(Base64.decode(data)), NbtAccounter.unlimitedHeap())
    return tag.getList("i").getOrNull()?.map {
        it.legacyStack()
    }
}

fun Tag.legacyStack(): ItemStack = LegacyDataFixer.fromTag(this) ?: ItemStack.EMPTY
