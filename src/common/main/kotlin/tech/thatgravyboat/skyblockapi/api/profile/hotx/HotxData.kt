package tech.thatgravyboat.skyblockapi.api.profile.hotx

interface HotxData<Perk : HotxPerk> {
    var perks: MutableMap<String, Perk>
    var tokens: Int
}

interface HotxPerk {
    val level: Int
    val unlocked: Boolean
    val disabled: Boolean
}
