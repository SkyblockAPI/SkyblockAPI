package tech.thatgravyboat.skyblockapi.api.remote

import tech.thatgravyboat.skyblockapi.RemoveNextVersion

@RemoveNextVersion(
    replaceWith = ReplaceWith("RepoItemsAPI", "tech.thatgravyboat.skyblockapi.api.remote.RepoItemsAPI"),
)
object SkyBlockItems {

    val nameToId: Map<String, String> get() = RepoItemsAPI.nameCache

    fun getIdByDisplayName(name: String): String? = RepoItemsAPI.getItemIdByName(name)
}
