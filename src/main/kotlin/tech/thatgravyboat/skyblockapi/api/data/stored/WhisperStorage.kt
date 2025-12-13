package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.profile.hotf.WhisperType

internal object WhisperStorage : SkillTreeCurrencyStorage<WhisperType>("whisper.json", WhisperType::class)
