package tech.thatgravyboat.skyblockapi.item

import com.google.gson.*
import me.owdding.repo.DEFAULT_CACHE_DIRECTORY
import me.owdding.repo.FileCache
import me.owdding.repo.resources.CompactingResourcesExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Internal
import org.gradle.kotlin.dsl.getByType
import java.io.File
import java.nio.file.StandardOpenOption
import kotlin.io.path.createDirectories
import kotlin.io.path.writeBytes
import kotlin.time.Duration.Companion.hours

const val HYPIXEL_ITEM_LIST = "https://api.hypixel.net/v2/resources/skyblock/items"
const val ITEM_DATA_CACHE_ENTRY = "item_data"
const val MUSEUM_DATA_CACHE_ENTRY = "museum_data"

@CacheableTask
abstract class CreateItemDataTask : DefaultTask() {
    @Internal
    val downloadCache = FileCache(project.gradle.gradleUserHomeDir.toPath().resolve(DEFAULT_CACHE_DIRECTORY), 1.hours)

    @Internal
    val itemDataCacheKey = downloadCache.getKey(ITEM_DATA_CACHE_ENTRY)

    @Internal
    val museumDataCacheKey = downloadCache.getKey(MUSEUM_DATA_CACHE_ENTRY)

    init {
        val configuration = project.extensions.getByType<CompactingResourcesExtension>()
        fun file(name: String) = project.layout.buildDirectory.file("generated/meowdding/item_data/${configuration.basePath!!}/$name.json").get().asFile
        val itemDataFile = file("item_data")
        val museumDataFile = file("museum_data")
        fun write(byteArray: ByteArray, file: File) {
            val filePath = file.toPath()
            filePath.parent.createDirectories()
            filePath.writeBytes(byteArray, options = arrayOf(StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE))
        }

        fun write(element: JsonElement, file: File) = write(GsonBuilder().setPrettyPrinting().create().toJson(element).toByteArray(), file)

        doFirst {
            if (/*downloadCache.isCached(itemDataCacheKey)*/false) {
                write(downloadCache.read(itemDataCacheKey), itemDataFile)
                return@doFirst
            }

            val itemList = JsonParser.parseString(downloadCache.getOrDownload(HYPIXEL_ITEM_LIST).toString(Charsets.UTF_8)).asJsonObject["items"].asJsonArray

            val itemData = JsonArray()
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
                        when (armorIds.size()) {
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

                if (output.size() > 1) itemData.add(output)
            }
            downloadCache.write(itemDataCacheKey, itemData.toString().toByteArray())

            write(itemData, itemDataFile)
        }
        doFirst {
            if (downloadCache.isCached(museumDataCacheKey)) {
                write(downloadCache.read(museumDataCacheKey), museumDataFile)
                return@doFirst
            }

            val itemList = JsonParser.parseString(downloadCache.getOrDownload(HYPIXEL_ITEM_LIST).toString(Charsets.UTF_8)).asJsonObject["items"].asJsonArray
            val museumData = JsonObject()
            val museumArmorData = JsonObject()
            itemList.forEach { item ->
                val item = item.asJsonObject

                if (item.has("museum_data")) {
                    val itemMuseumData = item.getAsJsonObject("museum_data")
                    itemMuseumData.getAsJsonObject("armor_set_donation_xp")?.let { armorSetDonationXp ->
                        val armorsets = armorSetDonationXp.keySet()
                        for (set in armorsets) {
                            val setArray: JsonArray
                            if (!museumArmorData.has(set)) {
                                setArray = JsonArray()
                                museumArmorData.add(set, setArray)
                            } else {
                                setArray = museumArmorData.getAsJsonArray(set)
                            }
                            setArray.add(item.get("id"))
                        }
                    }
                }

                if (!museumArmorData.isEmpty) {
                    museumData.add("armor_sets", museumArmorData)
                }
                downloadCache.write(itemDataCacheKey, museumData.toString().toByteArray())

                write(museumData, museumDataFile)
            }
        }

        outputs.dir(project.layout.buildDirectory.file("generated/meowdding/item_data"))
    }
}
