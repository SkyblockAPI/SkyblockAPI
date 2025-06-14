package tech.thatgravyboat.skyblockapi.item

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import me.owdding.repo.DEFAULT_CACHE_DIRECTORY
import me.owdding.repo.FileCache
import me.owdding.repo.resources.CompactingResourcesExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Internal
import org.gradle.kotlin.dsl.getByType
import java.nio.file.StandardOpenOption
import kotlin.io.path.createDirectories
import kotlin.io.path.writeBytes
import kotlin.time.Duration.Companion.hours

const val HYPIXEL_ITEM_LIST = "https://api.hypixel.net/v2/resources/skyblock/items"
const val ITEM_DATA_CACHE_ENTRY = "item_data"

@CacheableTask
abstract class CreateItemDataTask : DefaultTask() {
    @Internal
    val downloadCache = FileCache(project.gradle.gradleUserHomeDir.toPath().resolve(DEFAULT_CACHE_DIRECTORY), 1.hours)

    @Internal
    val cacheKey = downloadCache.getKey(ITEM_DATA_CACHE_ENTRY)

    init {
        val configuration = project.extensions.getByType<CompactingResourcesExtension>()
        val file = project.layout.buildDirectory.file("generated/meowdding/item_data/${configuration.basePath!!}/item_data.json").get().asFile
        fun write(byteArray: ByteArray) {
            val filePath = file.toPath()
            filePath.parent.createDirectories()
            filePath.writeBytes(byteArray, options = arrayOf(StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE))
        }

        doFirst {
            if (downloadCache.isCached(cacheKey)) {
                write(downloadCache.read(cacheKey))
                return@doFirst
            }

            val itemList = JsonParser.parseString(downloadCache.getOrDownload(HYPIXEL_ITEM_LIST).toString(Charsets.UTF_8)).asJsonObject["items"].asJsonArray

            val outputArray = JsonArray()
            itemList.forEach { item ->
                val item = item.asJsonObject
                val output = JsonObject()

                output.add("id", item.get("id"))
                val objectsToKeep = listOf("upgrade_costs", "dungeon_item_conversion_cost", "gemstone_slots", "npc_sell_price")
                for (objectToKeep in objectsToKeep) {
                    val obj = item.get(objectToKeep) ?: continue
                    output.add(objectToKeep, obj)
                }
                if (item.has("museum_data")) {
                    val museumData = item.getAsJsonObject("museum_data")
                    val newData = JsonObject()

                    museumData.get("type")?.let { newData.add("type", it) }
                    museumData.getAsJsonObject("armor_set_donation_xp")?.let { armorSetDonationXp ->
                        val armorIds = JsonArray().apply {
                            armorSetDonationXp.keySet().forEach { add(it) }
                        }
                        when(armorIds.size()) {
                            0 -> {}
                            1 -> newData.add("armor_set", armorIds[0])
                            else -> newData.add("armor_set", armorIds)
                        }
                    }
                    museumData.getAsJsonObject("parent")?.takeIf { !it.isEmpty }?.let {
                        newData.add("parent", it)
                    }
                    if (!museumData.isEmpty) {
                        output.add("museum_data", newData)
                    }
                } else if (item.get("museum")?.asBoolean == true) {
                    val newData = JsonObject().apply {
                        addProperty("type", "SPECIAL_ITEMS")
                    }
                    output.add("museum_data", newData)
                }

                if (output.size() > 1) outputArray.add(output)
            }
            downloadCache.write(cacheKey, outputArray.toString().toByteArray())

            write(GsonBuilder().setPrettyPrinting().create().toJson(outputArray).toByteArray())
        }

        outputs.dir(project.layout.buildDirectory.file("generated/meowdding/item_data"))
    }


}
