package tech.thatgravyboat.skyblockapi

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer
import net.minecraft.client.gui.components.debug.DebugScreenEntry
import net.minecraft.resources.Identifier
import net.minecraft.world.level.Level
import net.minecraft.world.level.chunk.LevelChunk
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.helpers.SkyBlockEntity.getAttachedLines

object TagAttachmentDebugEntry : DebugScreenEntry {
    val GROUP: Identifier = Identifier.fromNamespaceAndPath(SkyBlockAPI.MOD_ID, "tag_attachments")
    override fun display(
        displayer: DebugScreenDisplayer,
        arg2: Level?,
        arg3: LevelChunk?,
        arg4: LevelChunk?,
    ) {
        val entity = Minecraft.getInstance().cameraEntity ?: return

        val list = mutableListOf<String>()
        list.add(getAttachedLines(entity).size.toString())
        for (attachedLine in getAttachedLines(entity)) {
            list.add(attachedLine.string)
        }

        displayer.addToGroup(GROUP, list)
    }
}
