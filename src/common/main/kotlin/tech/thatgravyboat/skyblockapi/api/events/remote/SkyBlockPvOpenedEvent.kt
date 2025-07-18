package tech.thatgravyboat.skyblockapi.api.events.remote

import com.google.gson.JsonObject
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extentions.toDashlessString
import tech.thatgravyboat.skyblockapi.utils.json.getPath

/**
 * This event is fired when the user opens the SkyBlockPv viewer on their own profile.
 *
 * It only contains the currently selected profile, so from the api response:
 * ```json
 * {
 *   "success": true,
 *   "profiles": [
 *      {
 *        "profile_id": "922f4e4d-f914-4453-8693-3dd9b90e7732",
 *        "selected": true,
 *        ...
 *      },
 *      {
 *        "profile_id": "842ace31-138b-4e17-9ef4-5065456069e4",
 *        "selected": false,
 *        ...
 *      }
 *   ]
 * }
 * ```
 * It will only return the JsonObject of the selected profile.
 */
@SkyBlockPvRequired
data class SkyBlockPvOpenedEvent(val profileData: JsonObject) : SkyBlockEvent() {
    /** member data of the McPlayer in the currently selected profile */
    val member: JsonObject = profileData.getPath("members.${McPlayer.uuid.toDashlessString()}")?.asJsonObject!!
}
@RequiresOptIn(
    message = """
        This event only gets fired when SkyBlockPv is actually installed.
        It should serve as a fallback for data fetching and not as a replacement.
    """,
    level = RequiresOptIn.Level.WARNING,
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class SkyBlockPvRequired
