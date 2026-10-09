package tech.thatgravyboat.skyblockapi.api.events.misc

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

/**
 * Event that gets ran when the data of a specific profile gets deleted,
 * it being either an automatic deletion or triggered by the user
 */
public class ProfileDeleteDataEvent(val profileName: String) : SkyBlockEvent()
