package tech.thatgravyboat.skyblockapi.api.repo.apis

import com.google.gson.JsonObject
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.utils.extentions.asString
import tech.thatgravyboat.skyblockapi.utils.extentions.removeTrailingChar
import tech.thatgravyboat.skyblockapi.utils.json.getPath
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

object SkyBlockItemsRepo : RepoItemCache<String>("Items") {

    private val repo get() = RepoAPI.items().items()
    private val names by lazy {
        this.repo.mapNotNull { entry ->
            val json = entry.value.getPath("['components'].['minecraft:custom_name'].['text']") ?: return@mapNotNull null
            val text = Text.of(json.asString("")).stripped.takeIf { it.isNotEmpty() } ?: return@mapNotNull null
            // neu doesn't store them with : like hypixel does, we however use the hypixel format for eas of use
            text.lowercase() to entry.key.uppercase().replace("-", ":")
        }.toMap()
    }

    override fun create(key: String): LazyItemStack? {
        val id = key.uppercase().replace(":", "-").takeUnless { it == "MUSHROOM_COLLECTION" } ?: "RED_MUSHROOM"
        return RepoAPI.items().getItem(id)?.let(::LazyItemStack)
    }

    fun get(id: String): JsonObject? = ifInitialized { this.repo[id] }

    fun getIdByName(name: String): String? = ifInitialized {
        val lowercase = name.lowercase()
        names[lowercase]?.let { return it }
        val noStars = lowercase.removeTrailingChar('✪').trim()
        names[noStars]?.let { return it }
        val firstWhitespace = noStars.indexOf(' ')
        if (firstWhitespace == -1) return null
        val withoutFirstWord = noStars.substring(firstWhitespace).trim() // In case the item has a reforge
        return names[withoutFirstWord]
    }
}
