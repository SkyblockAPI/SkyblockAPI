package tech.thatgravyboat.skyblockapi.utils.extentions

import tech.thatgravyboat.repolib.api.ReforgeStonesAPI.ReforgeData
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity

fun ReforgeData.getApplyCosts() = SkyBlockRarity.entries.associateWith { this.applyCost[it.name] }.filterValuesNotNull()
fun ReforgeData.getApplyCost(rarity: SkyBlockRarity) = this.applyCost[rarity.name]
