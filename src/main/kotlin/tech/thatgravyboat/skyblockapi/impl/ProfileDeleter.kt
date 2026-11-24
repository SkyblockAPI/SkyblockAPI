package tech.thatgravyboat.skyblockapi.impl

import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.data.stored.ProfileStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.InventoryTitle
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.ProfileDeleteDataEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.utils.builders.TooltipBuilder
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.pluralize
import tech.thatgravyboat.skyblockapi.utils.regex.Destructured
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup
import tech.thatgravyboat.skyblockapi.utils.regex.matchWhen
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.onClick
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.underlined

@Module
internal object ProfileDeleter {

    private val group = RegexGroup.CHAT.group("profile_delete")
    private val deleteRegex = group.create("delete", "Done! Your (?<name>.*) profile was deleted!")
    private val wipeRegex = group.create("wipe", "Your SkyBlock Profile (?<name>.*) has been wiped .*")
    private val bingoStartRegex = group.create("bingo.start", "\\s+Welcome to SkyBlock Bingo!")

    @Subscription
    @InventoryTitle("Profile Management")
    context(event: ContainerInitializedEvent)
    private fun onContainerInitialized() {
        if (!ProfileAPI.profileManagementTitle.matches(event.title)) return

        val foundProfiles = event.containerItems.mapNotNullTo(mutableSetOf()) { item ->
            if (item.isEmpty) return@mapNotNullTo null
            ProfileAPI.profileManagementNameRegex.findGroup(item.cleanName, "profileName")
        }

        val allProfiles = ProfileStorage.getAllProfileNames()
        val missingProfiles: MutableList<String> = (allProfiles - foundProfiles).toMutableList()

        val reversedProfiles = missingProfiles.filter { profileName ->
            if (profileName.length < 2) return@filter false
            // we check if profile name is reversed by seeing if it starts by lowercase and ends with uppercase
            profileName.first().isLowerCase() && profileName.last().isUpperCase()
        }

        // if the profile name is reversed, we immediately delete it
        reversedProfiles.forEach { name ->
            handleDelete(name)
        }

        // we remove all the reversed profiles from the missing profiles
        missingProfiles.removeAll(reversedProfiles)

        if (missingProfiles.isEmpty()) return
        val size = missingProfiles.size
        Text.debug {
            append("Found ")
            append(size.toString(), TextColor.AQUA)
            append(" ${pluralize(size, "profile")} with data that does not exist anymore. ")

            append("Click to delete the stored data") {
                underlined = true
            }

            hover = TooltipBuilder.multiline {
                add("This won't delete your SkyBlock profiles,")
                add("it will only delete data stored.")
                space()
                add("Profiles are: ", TextColor.YELLOW)
                space()
                missingProfiles.forEach {
                    add {
                        color = TextColor.GRAY
                        append(" - ")
                        append("\"")
                        append(it, TextColor.RED)
                        append("\"")
                    }
                }
            }

            onClick {
                missingProfiles.forEach(::handleDelete)
                Text.sendDebug("Deleted data of $size ${pluralize(size, "profile")}.")
            }
        }.send("NON_EXISTING_PROFILES")
    }
    

    @Subscription(receiveCancelled = true)
    private fun onChat(event: ChatReceivedEvent.Pre) {
        matchWhen(event.text) {
            case(deleteRegex, "name", action = ::handleDelete)
            case(wipeRegex, "name", action = ::handleDelete)
            case(bingoStartRegex) {
                // when bingo starts, we remove all bingo profiles
                ProfileStorage.getProfileTypes()
                    .filterValues { it == BINGO }
                    .keys
                    .forEach(::handleDelete)
            }
        }
    }

    @Subscription
    private fun onCommand(event: RegisterSkyblockApiCommandsEvent) {
        event.register("profile delete") {
            val allProfiles = ProfileStorage.getAllProfileNames()

            thenCallback("profileName", StringArgumentType.string(), allProfiles) {
                val profileName = argument<String>("profileName")
                if (profileName !in allProfiles) {
                    Text.sendDebug("There is no profile named \"$profileName\"!")
                    return@thenCallback
                }
                handleDelete(profileName)
                Text.sendDebug("Deleted all profile data for \"") {
                    append(profileName, TextColor.GOLD)
                    append("\"!")
                }
            }
        }
    }

    private fun handleDelete(destructured: Destructured) {
        handleDelete(destructured.component1())
    }

    private fun handleDelete(profileName: String) {
        SkyBlockAPI.info("Deleting all profile data for profile \"$profileName\"")
        ProfileStorage.removeProfile(profileName)
        StoredProfileData.allProfileData.forEach { it.removeProfile(profileName) }
        ProfileDeleteDataEvent(profileName).post()
        // This is here so other mods can also delete their own data of specific profiles, if they want to
    }

}
