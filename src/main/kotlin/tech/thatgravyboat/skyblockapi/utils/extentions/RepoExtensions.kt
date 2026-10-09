package tech.thatgravyboat.skyblockapi.utils.extentions

import tech.thatgravyboat.repolib.api.ReforgeStonesAPI.ReforgeData
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity

public fun ReforgeData.getApplyCosts() = SkyBlockRarity.entries.associateWith { this.applyCost[it.name] }.filterValuesNotNull()
public fun ReforgeData.getApplyCost(rarity: SkyBlockRarity) = this.applyCost[rarity.name]
