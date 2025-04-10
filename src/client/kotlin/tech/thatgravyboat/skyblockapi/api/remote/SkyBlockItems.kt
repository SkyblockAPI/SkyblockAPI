package tech.thatgravyboat.skyblockapi.api.remote

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils
import tech.thatgravyboat.skyblockapi.utils.http.RemoteData

/**
 * Contains simplified SkyBlock item data.
 *
 * This does not contain any lore and just the ItemStack with their name and items with optional glint.
 */
@Module
@UseRepoLib
@Deprecated("Use ItemAPI instead")
object SkyBlockItems {

    private val repo by RemoteData(
        CodecUtils.map(Codec.STRING, Codec.STRING).fieldOf("items").codec(),
        "https://raw.githubusercontent.com/SkyblockAPI/Data/refs/heads/main/namesToId.json",
        "namesToId.json",
    )

    val idToName: Map<String, String>
        get() = repo ?: emptyMap()

    fun getIdByDisplayName(name: String): String? = idToName.entries.find { it.value.equals(name, true) }?.key
    fun getDisplayNameById(id: String): String? = idToName.entries.find { it.key.equals(id, true) }?.value
}

@RequiresOptIn(
    message = """
        This API is not as exhaustive as the RepoLib API and may not 
        contain all items and does not contain all information. 
        It is recommended to use the RepoLib API for more accurate data.
        If you are sure you want to use this API, you can suppress this warning.
        
        For information on how to use the RepoLib API, see https://github.com/SkyblockAPI/Repo-Lib
    """,
    level = RequiresOptIn.Level.WARNING,
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class UseRepoLib
