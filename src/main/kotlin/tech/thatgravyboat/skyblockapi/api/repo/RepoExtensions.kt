package tech.thatgravyboat.skyblockapi.api.repo

import tech.thatgravyboat.repolib.api.ReforgeStonesAPI.ReforgeData
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.utils.extentions.filterValuesNotNull

fun ReforgeData.getApplyCosts() = SkyBlockRarity.entries.associateWith { this.applyCost[it.name] }.filterValuesNotNull()
fun ReforgeData.getApplyCost(rarity: SkyBlockRarity) = this.applyCost[rarity.name]
