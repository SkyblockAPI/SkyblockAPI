package tech.thatgravyboat.skyblockapi.api.profile.hotx

interface HotxData<Perk : HotxPerk> {
    var perks: MutableMap<String, Perk>
    val tokens: Int
}

interface HotxPerk {
    val level: Int
    val unlocked: Boolean
    val disabled: Boolean
}
