package tech.thatgravyboat.skyblockapi.api.remote

import org.jetbrains.annotations.ApiStatus

@ApiStatus.ScheduledForRemoval(inVersion = "1.21.6 or 1.22.0")
@Deprecated("Use the new API to get item IDs.")
object SkyBlockItems {

    val nameToId: Map<String, String> get() = RepoItemsAPI.nameCache

    fun getIdByDisplayName(name: String): String? = RepoItemsAPI.getItemIdByName(name)
}
