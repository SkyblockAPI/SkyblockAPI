package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.profile.hotf.WhisperType
import kotlin.reflect.typeOf

internal object WhisperStorage : SkillTreeCurrencyStorage<WhisperType>("whisper.json", typeOf<WhisperType>())
