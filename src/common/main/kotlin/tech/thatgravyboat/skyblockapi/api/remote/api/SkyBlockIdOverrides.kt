package tech.thatgravyboat.skyblockapi.api.remote.api

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.generated.CodecUtils

internal object SkyBlockIdOverrides {

    private val overrides: Map<SkyBlockId, String> = SkyBlockAPI.getRepo("skyblock_id_overrides", CodecUtils.map(SkyBlockId.CODEC, Codec.STRING))

    fun SkyBlockId.fixHypixelId(): String? = overrides[this]

}
