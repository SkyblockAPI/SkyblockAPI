package tech.thatgravyboat.skyblockapi.api.remote

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.RunesAPI.Rune
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockRunesRepo

@Deprecated("Use SkyBlockRunesRepo instead", ReplaceWith("tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockRunesRepo"))
object RepoRunesAPI {

    fun getRuneById(id: String): List<Rune>? = SkyBlockRunesRepo.get(id)
    fun getRune(id: String, tier: Int) = SkyBlockRunesRepo.getTier(id, tier)

    fun getRune(string: String): Rune? {
        val split = string.split(":")
        if (split.size != 3) return null
        if (split[0] != "rune") return null
        val id = split[1]
        val tier = split[2].toIntOrNull() ?: return null
        return SkyBlockRunesRepo.getTier(id, tier)
    }

    fun getRuneAsItemOrNull(id: String, tier: Int? = null): ItemStack? = SkyBlockRunesRepo.getItemStack {
        this.id = id
        this.tier = tier
    }
    fun getRuneAsItem(id: String, tier: Int): ItemStack = SkyBlockRunesRepo.getItemStackOrDefault {
        this.id = id
        this.tier = tier
    }

    fun Rune.getId() = buildString {
        append("rune:")
        append(this@getId.id())
        append(":")
        append(this@getId.tier())
    }

    fun Rune.getItem(): ItemStack? = getRuneAsItem(this.id(), this.tier())
}
