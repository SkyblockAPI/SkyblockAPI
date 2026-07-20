package tech.thatgravyboat.skyblockapi.impl

import me.owdding.ktmodules.Module
import net.fabricmc.loader.impl.util.FileSystemUtil
import net.minecraft.util.ARGB.alpha
import org.apache.commons.io.IOUtils
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.sendWithPrefix
import java.io.InputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.util.zip.ZipOutputStream
import kotlin.io.path.createDirectory
import kotlin.io.path.deleteIfExists
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.io.path.outputStream
import kotlin.io.path.useDirectoryEntries

@Module
object HypixelPackLoader {

    private const val DOWNLOAD_URL = "https://github.com/meowdding/hypixel-pack/archive/refs/heads/"
    private const val PACK_FILE_NAME = "HypixelSkyBlockPack.zip"
    private const val PACK_FILE_NAME_ALPHA = "HypixelSkyBlockPack_Alpha.zip"

    @Subscription
    internal fun onCommand(event: RegisterSkyblockApiCommandsEvent) {
        event.register("pack") {
            thenCallback("stable") { downloadPack("stable") }
            thenCallback("alpha") { downloadPack("alpha") }
        }
    }

    private fun downloadPack(path: String) {
        if (LocationAPI.isOnSkyBlock) {
            Text.of("Pack Downloading doesn't work on SkyBlock").sendWithPrefix()
            return
        }

        val mcVersion = McClient.mcVersion.stringVersion
        val isAlpha = path == "alpha"
        val branch = if (isAlpha) "alpha/$mcVersion" else mcVersion
        val fullUrl = "$DOWNLOAD_URL$branch.zip"
        val packFileName = if (isAlpha) PACK_FILE_NAME_ALPHA else PACK_FILE_NAME

        Text.of("Downloading Hypixel resource pack ($path) for version $mcVersion...").sendWithPrefix()

        Thread {
            try {
                val targetPackPath = McClient.self.gameDirectory.toPath().resolve("resourcepacks").resolve(packFileName)

                targetPackPath.deleteIfExists()

                URI.create(fullUrl).toURL().openStream().use { inputStream ->
                    Files.copy(inputStream.removeFirstDirectoryLayer(), targetPackPath, StandardCopyOption.REPLACE_EXISTING)
                }

                McClient.self.execute {
                    val packRepository = McClient.self.resourcePackRepository
                    packRepository.reload()

                    val packId = "file/$packFileName"
                    val selectedPacks = packRepository.selectedIds.toMutableList()

                    if (!selectedPacks.contains(packId)) {
                        selectedPacks.add(packId)
                        packRepository.setSelected(selectedPacks)
                        McClient.options.updateResourcePacks(packRepository)
                    } else {
                        McClient.reloadResourcePacks()
                    }

                    Text.of("Hypixel pack successfully loaded!").sendWithPrefix()
                }
            } catch (e: Exception) {
                SkyBlockAPI.logger.error("Failed to download or load the Hypixel pack from $fullUrl", e)
                McClient.self.execute {
                    Text.of("Failed to download or load the pack.").sendWithPrefix()
                }
            }
        }.start()
    }

    // Idek what the fuck mona wrote here
    private fun InputStream.removeFirstDirectoryLayer(): InputStream {
        val input = Files.createTempFile("sbapi_", "_pack_in.zip")
        val output = Files.createTempFile("sbapi_", "_pack_out.zip")
        IOUtils.copy(this, input.outputStream(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))
        ZipOutputStream(output.outputStream(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)).close()

        val inputSystem = FileSystemUtil.getJarFileSystem(input, false)
        val outputSystem = FileSystemUtil.getJarFileSystem(output, true)
        try {
            val inputRoot = inputSystem.get().getPath("/")
            val outputRoot = outputSystem.get().getPath("/")

            inputRoot.useDirectoryEntries {
                it.forEach {
                    it.copyTo(outputRoot)
                }
            }
        } finally {
            inputSystem.close()
            outputSystem.close()
        }

        return output.inputStream()
    }

    private fun Path.copyTo(target: Path) {
        Files.list(this).forEach {
            if (it.isDirectory()) {
                val dir = target.resolve(it.name)
                dir.createDirectory()
                it.copyTo(dir)
            } else {
                Files.copy(it, target.resolve(it.name))
            }
        }
    }
}
