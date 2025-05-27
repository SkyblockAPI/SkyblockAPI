package tech.thatgravyboat.skyblockapi.api.remote.hypixel

import com.google.gson.JsonObject
import kotlinx.coroutines.runBlocking
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.HypixelSkillAPI.SkillData.Companion.toSkillData
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.asInt
import tech.thatgravyboat.skyblockapi.utils.extentions.asLong
import tech.thatgravyboat.skyblockapi.utils.extentions.asString
import tech.thatgravyboat.skyblockapi.utils.http.Http

// §63,538/3,163❤     §3+87.8 Mining (127,630,594/0)     §236,127/50k Drill Fuel

private const val API_URL = "https://api.hypixel.net/v2/resources/skyblock/skills"

object HypixelSkillAPI {
    enum class OtherSkill : SkillType {
        COMBAT,
        FORAGING,
        MINING,
        FARMING {
            override fun hasFloatingLevelCap(): Boolean = true
        },
        FISHING,
        ENCHANTING,
        ALCHEMY,
        TAMING {
            override fun hasFloatingLevelCap(): Boolean = true
        },
        CARPENTRY,
        RUNECRAFTING,
        SOCIAL,
        ;

        private var internalSkillData: SkillData? = null
        override val data: SkillData get() = internalSkillData ?: throw UnsupportedOperationException("Internal skill data is not yet supported")
        override val id: String = name

        @Module
        companion object {
            var skills = emptyList<OtherSkill>()
                private set

            init {
                runBlocking {
                    val skills = Http.getResult<JsonObject>(url = API_URL).getOrNull()?.getAsJsonObject("skills") ?: return@runBlocking
                    this@Companion.skills = skills.entrySet().mapNotNull { (key, value) ->
                        val skillData = value.asJsonObject.toSkillData()

                        runCatching {
                            valueOf(key).also { skill -> skill.internalSkillData = skillData }
                        }.getOrNull()
                    }
                }
            }
        }
    }

    data class SkillData(
        val name: String,
        val maxLevel: Int,
        val skillLevels: Map<Int, Long>,
    ) {
        companion object {
            internal fun JsonObject.toSkillData() = SkillData(
                this["name"].asString(""),
                this["maxLevel"].asInt(0),
                this.getAsJsonArray("levels").associate { it.asJsonObject.let { it["level"].asInt(0) to it["totalExpRequired"].asLong(0) } },
            )
        }
    }

    interface SkillType {
        val data: SkillData
        val id: String
        fun hasFloatingLevelCap(): Boolean = false
        val skillApiId get() = "SKILL_$id"
    }
}
