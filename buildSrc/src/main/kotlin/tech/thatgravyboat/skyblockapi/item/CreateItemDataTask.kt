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
            itemList.forEach {
                val item = it.asJsonObject
                val output = JsonObject()
                var keep = false

                output.add("id", item.get("id"))
                val objectsToKeep = listOf("upgrade_costs", "dungeon_item_conversion_cost", "gemstone_slots", "npc_sell_price")
                for (objectToKeep in objectsToKeep) {
                    if (item.has(objectToKeep)) {
                        output.add(objectToKeep, item.get(objectToKeep))
                        keep = true
                    }
                }

                if (keep) outputArray.add(output)
            }
            downloadCache.write(cacheKey, outputArray.toString().toByteArray())

            write(GsonBuilder().setPrettyPrinting().create().toJson(outputArray).toByteArray())
        }

        outputs.dir(project.layout.buildDirectory.file("generated/meowdding/item_data"))
    }


}
