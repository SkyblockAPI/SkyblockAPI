package tech.thatgravyboat.skyblockapi.api.remote.hypixel

import com.google.gson.JsonObject
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.HypixelSkillAPI.SkillData.Companion.toSkillData
import tech.thatgravyboat.skyblockapi.utils.extentions.asInt
import tech.thatgravyboat.skyblockapi.utils.extentions.asLong
import tech.thatgravyboat.skyblockapi.utils.extentions.asString
import tech.thatgravyboat.skyblockapi.utils.extentions.valueOfOrNull
import tech.thatgravyboat.skyblockapi.utils.http.Http
import tech.thatgravyboat.skyblockapi.utils.runCatchBlocking

private const val API_URL = "https://api.hypixel.net/v2/resources/skyblock/skills"

public object HypixelSkillAPI {
    public enum class Skill(private val floatingCap: Boolean = false) : SkillType {
        COMBAT,
        FORAGING(true),
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
        override fun hasFloatingLevelCap(): Boolean = floatingCap

        override val id: String = name

        @Module
        public companion object {
            init {
                runCatchBlocking {
                    val skillsObject = Http.getResult<JsonObject>(url = API_URL).getOrNull()?.getAsJsonObject("skills") ?: return@runCatchBlocking
                    skillsObject.entrySet().mapNotNull { (key, value) ->
                        val skillData = value.asJsonObject.toSkillData()

                        valueOfOrNull<Skill>(key)?.also { skill -> skill.internalSkillData = skillData }
                    }
                }
            }

            public fun getByName(name: String): Skill? = Skill.entries.find {
                it.name.equals(name, true) || it.skillApiId.equals(name, true) || it.data.name.equals(name, true)
            }
        }
    }

    public data class SkillData(
        val name: String,
        val maxLevel: Int,
        val skillLevels: Map<Int, Long>,
    ) {
        public fun getTotalExpForLevel(level: Int): Long = skillLevels[level] ?: skillLevels.entries.lastOrNull()?.value ?: 0L
        public fun getXpForLevel(level: Int): Long = skillLevels[level]?.let { xpAtLevel -> xpAtLevel - (skillLevels[level - 1] ?: 0L) } ?: 0L
        public fun getLevelForExp(exp: Long): Int = skillLevels.entries.lastOrNull { exp >= it.value }?.key ?: 0

        public companion object {
            internal val EMPTY = SkillData("", 0, emptyMap())

            internal fun JsonObject.toSkillData() = SkillData(
                this["name"].asString(""),
                this["maxLevel"].asInt(0),
                this.getAsJsonArray("levels").associate { it.asJsonObject.let { it["level"].asInt(0) to it["totalExpRequired"].asLong(0) } },
            )
        }
    }

    public interface SkillType {
        public val data: SkillData
        public val id: String
        public fun hasFloatingLevelCap(): Boolean = false
        public val skillApiId: String get() = "SKILL_$id"
    }
}
