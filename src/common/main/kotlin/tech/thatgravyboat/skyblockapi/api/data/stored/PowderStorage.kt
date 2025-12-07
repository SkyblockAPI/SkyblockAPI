package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.profile.hotm.PowderType

internal object PowderStorage : SkillTreeCurrencyStorage<PowderType>("powder.json", PowderType::class)
