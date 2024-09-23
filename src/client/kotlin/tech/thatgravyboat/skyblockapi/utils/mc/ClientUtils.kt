package tech.thatgravyboat.skyblockapi.utils.mc

import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.network.chat.Component
import net.minecraft.world.scores.PlayerTeam
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent

internal val PlayerInfo.displayName: Component
    get() = tabListDisplayName ?: PlayerTeam.formatNameForTeam(team, profile.name.asComponent())
