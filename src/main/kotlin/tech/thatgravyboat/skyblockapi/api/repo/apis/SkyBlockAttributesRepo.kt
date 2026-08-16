package tech.thatgravyboat.skyblockapi.api.repo.apis

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.ktmodules.Module
import tech.thatgravyboat.repolib.api.AttributesAPI
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.RepoStatus
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnRepoStatus
import tech.thatgravyboat.skyblockapi.api.events.misc.RepoStatusEvent
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockEnchantmentsRepo.Query
@Module
object SkyBlockAttributesRepo : RepoItemCache<String>("Attributes") {

    private val attributes: MutableMap<String, AttributesAPI.Attribute> = mutableMapOf()
    private val repo get() = RepoAPI.attributes()

    override fun create(key: String): LazyItemStack? {
        val attribute = attributes[key.lowercase()] ?: this.repo.getAttribute(key)
        if (attribute == null) return null
        return attribute.itemStack.let(::LazyItemStack)
    }

    @Subscription(RepoStatusEvent::class)
    @OnRepoStatus(RepoStatus.SUCCESS)
    fun onRepoReady() {
        this.attributes.putAll(this.repo.attributes().values.associateBy { it.attributeId().lowercase() })
    }

    fun get(id: String): AttributesAPI.Attribute? = ifInitialized {
        this.attributes[id.lowercase()] ?: this.repo.getAttribute(id)
    }
}
