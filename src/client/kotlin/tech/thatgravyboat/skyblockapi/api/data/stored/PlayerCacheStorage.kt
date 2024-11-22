package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.CachedPlayer
import tech.thatgravyboat.skyblockapi.api.data.PlayerCacheData
import tech.thatgravyboat.skyblockapi.api.data.StoredData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.modules.Module
import java.util.*

private const val MAX_REMOVE_TIME = 10 * 24 * 60 * 60 * 1000 // 10 days

@Module
internal object PlayerCacheStorage {

    private var shouldSave = false

    private val PLAYER_CACHE = StoredData(
        PlayerCacheData(),
        PlayerCacheData.CODEC,
        "player_cache.json"
    )

    private val players: MutableMap<UUID, CachedPlayer>
        get() = PLAYER_CACHE.get().players

    private inline val UUID.player get() = players[this]

    private fun cleanupAndSave() {
        val current = System.currentTimeMillis()
        players.entries.removeIf { (_, cachedPlayer) ->
            current - cachedPlayer.time > MAX_REMOVE_TIME
        }
        save()
    }

    fun getPlayerName(uuid: UUID) = when {
        uuid == McPlayer.uuid -> McPlayer.name
        uuid.player == null -> McClient.players.find { it.profile.id == uuid }?.profile?.also { updatePlayer(it.id, it.name) }?.name
        else -> uuid.player?.name
    }

    /** Returns true if the player was added to the cache or the name was updated */
    internal fun updatePlayer(uuid: UUID, name: String): Boolean {
        val player = uuid.player
        if (player == null) {
            players[uuid] = CachedPlayer(name)
            save()
            return true
        }
        player.time = System.currentTimeMillis()
        if (player.name != name) {
            player.name = name
            save()
            return true
        }
        shouldSave = true
        return false
    }

    @Subscription
    fun onServerChange(event: ServerChangeEvent) {
        if (shouldSave) save()
    }

    @Subscription
    fun onDisconnect(event: ServerDisconnectEvent) = cleanupAndSave()

    private fun save() {
        shouldSave = false
        PLAYER_CACHE.save()
    }

}
