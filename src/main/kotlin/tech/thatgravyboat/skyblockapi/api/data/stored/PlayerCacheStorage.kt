package tech.thatgravyboat.skyblockapi.api.data.stored

import me.owdding.ktmodules.Module
import net.minecraft.client.player.AbstractClientPlayer
import tech.thatgravyboat.skyblockapi.api.data.CachedPlayer
import tech.thatgravyboat.skyblockapi.api.data.PlayerCacheData
import tech.thatgravyboat.skyblockapi.api.data.StoredData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.entity.EntityAddedEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extentions.isRealPlayer
import java.util.UUID

private const val MAX_REMOVE_TIME = 10 * 24 * 60 * 60 * 1000 // 10 days

@Module
internal object PlayerCacheStorage {

    private var shouldSave = false

    private val PLAYER_CACHE = StoredData<PlayerCacheData>(
        "player_cache",
        differentAlphaData = false,
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

    fun getPlayerUUID(name: String): UUID? {
        if (McPlayer.name == name) return McPlayer.uuid
        val currentServerUuid = McClient.players.find { it.profile.name == name }?.profile?.id
        if (currentServerUuid != null) {
            updatePlayer(currentServerUuid, name)
            return currentServerUuid
        }
        return players.entries.find { (_, cachedPlayer) -> cachedPlayer.name == name }?.key
    }

    fun getPlayerName(uuid: UUID): String? {
        if (uuid == McPlayer.uuid) return McPlayer.name
        val cachedPlayer = uuid.player
        if (cachedPlayer != null) return cachedPlayer.name
        val currentServerName = McClient.players.find { it.profile.id == uuid }?.profile?.name
        if (currentServerName != null) {
            updatePlayer(uuid, currentServerName)
        }
        return currentServerName
    }

    private fun scheduleSave() {
        shouldSave = true
    }

    /** Returns true if the player was added to the cache or the name was updated */
    internal fun updatePlayer(uuid: UUID, name: String): Boolean {
        if (!uuid.isRealPlayer()) return false // nicked and fake players shouldn't get added
        val player = uuid.player
        if (player == null) {
            players[uuid] = CachedPlayer(name)
            scheduleSave()
            return true
        }
        player.time = System.currentTimeMillis()
        if (player.name != name) {
            player.name = name
            return true
        }
        scheduleSave()
        return false
    }

    @Subscription
    private fun onEntity(event: EntityAddedEvent) {
        val player = event.entity as? AbstractClientPlayer ?: return
        if (!player.isRealPlayer()) return
        val profile = player.gameProfile
        updatePlayer(profile.id, profile.name)
    }

    @Subscription(ServerChangeEvent::class)
    private fun onServerChange() {
        if (shouldSave) save()
    }

    @Subscription(ServerDisconnectEvent::class)
    private fun onDisconnect() = cleanupAndSave()

    private fun save() {
        shouldSave = false
        PLAYER_CACHE.save()
    }

}
