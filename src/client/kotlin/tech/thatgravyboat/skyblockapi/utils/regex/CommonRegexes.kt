package tech.thatgravyboat.skyblockapi.utils.regex

import tech.thatgravyboat.skyblockapi.modules.Module

@Module
internal object CommonRegexes {

    private val chatGroup = RegexGroup.CHAT

    val viewProfileRegex = chatGroup.create(
        "viewprofile",
        "^/viewprofile (?<uuid>.+)"
    )

}
