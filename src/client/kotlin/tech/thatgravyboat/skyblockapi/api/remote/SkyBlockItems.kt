package tech.thatgravyboat.skyblockapi.api.remote

import com.mojang.serialization.Codec
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils
import tech.thatgravyboat.skyblockapi.utils.http.RemoteData
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

/**
 * Contains simplified SkyBlock item data.
 *
 * This does not contain any lore and just the ItemStack with their name and items with optional glint.
 */
@Module
@UseRepoLib
object SkyBlockItems {

    private val repo by RemoteData(
        CodecUtils.map(Codec.STRING, ItemStack.CODEC).fieldOf("items").codec(),
        "https://raw.githubusercontent.com/SkyblockAPI/Data/refs/heads/main/items.json",
        "items.json"
    )

    val idToItem: Map<String, ItemStack>
        get() = repo ?: emptyMap()

    val nameToId = buildMap<String, String> {
        repo?.forEach { (id, item) ->
            val name = item.get(DataComponents.CUSTOM_NAME) ?: return@forEach
            this[name.stripped.lowercase()] = id
        }
    }

    fun getIdByDisplayName(name: String): String? = nameToId[name.lowercase()]

    fun getItemById(id: String): ItemStack? = repo?.get(id)

    fun getItemByDisplayName(name: String): ItemStack? = getIdByDisplayName(name)?.let(::getItemById)
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
