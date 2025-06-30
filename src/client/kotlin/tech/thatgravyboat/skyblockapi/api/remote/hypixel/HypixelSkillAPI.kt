package tech.thatgravyboat.skyblockapi.api.remote.hypixel

import com.google.gson.JsonObject
import kotlinx.coroutines.runBlocking
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.HypixelSkillAPI.SkillData.Companion.toSkillData
import tech.thatgravyboat.skyblockapi.utils.extentions.asInt
import tech.thatgravyboat.skyblockapi.utils.extentions.asLong
import tech.thatgravyboat.skyblockapi.utils.extentions.asString
import tech.thatgravyboat.skyblockapi.utils.extentions.valueOfOrNull
import tech.thatgravyboat.skyblockapi.utils.http.Http
import tech.thatgravyboat.skyblockapi.utils.runCatchBlocking

private const val API_URL = "https://api.hypixel.net/v2/resources/skyblock/skills"

object HypixelSkillAPI {
    enum class Skill(private val floatingCap: Boolean = false) : SkillType {
        COMBAT,
        FORAGING,
        MINING,
        FARMING(true),
        FISHING,
        ENCHANTING,
        ALCHEMY,
        HUNTING,
        TAMING(true),
        CARPENTRY,
        RUNECRAFTING,
        SOCIAL,
        ;

        private var internalSkillData: SkillData? = null
        override val data: SkillData get() = internalSkillData ?: SkillData.EMPTY
        override fun hasFloatingLevelCap() = floatingCap

        override val id: String = name

        @Module
        companion object {
            init {
                runCatchBlocking {
                    val skillsObject = Http.getResult<JsonObject>(url = API_URL).getOrNull()?.getAsJsonObject("skills") ?: return@runCatchBlocking
                    skillsObject.entrySet().mapNotNull { (key, value) ->
                        val skillData = value.asJsonObject.toSkillData()

                        valueOfOrNull<Skill>(key)?.also { skill -> skill.internalSkillData = skillData }
                    }
                }
            }

            fun getByName(name: String) = Skill.entries.find {
                it.name.equals(name, true) || it.skillApiId.equals(name, true) || it.data.name.equals(name, true)
            }
        }
    }

    data class SkillData(
        val name: String,
        val maxLevel: Int,
        val skillLevels: Map<Int, Long>,
    ) {
        fun getTotalExpForLevel(level: Int) = skillLevels[level] ?: skillLevels.entries.lastOrNull()?.value ?: 0L

        companion object {
            internal val EMPTY = SkillData("", 0, emptyMap())

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
