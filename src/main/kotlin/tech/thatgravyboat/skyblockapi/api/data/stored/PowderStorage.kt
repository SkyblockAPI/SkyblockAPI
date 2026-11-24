package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.profile.hotm.PowderType
import kotlin.reflect.typeOf

internal object PowderStorage : SkillTreeCurrencyStorage<PowderType>("powder.json", typeOf<PowderType>())
