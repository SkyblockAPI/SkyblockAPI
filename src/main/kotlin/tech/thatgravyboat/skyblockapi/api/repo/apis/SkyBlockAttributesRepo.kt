package tech.thatgravyboat.skyblockapi.api.repo.apis

import me.owdding.ktmodules.Module
import tech.thatgravyboat.repolib.api.AttributesAPI
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.RepoStatus
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnRepoStatus
import tech.thatgravyboat.skyblockapi.api.events.repo.RepoEvent
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack

@Module
object SkyBlockAttributesRepo : RepoItemCache<String>("Attributes") {

    private val attributes: MutableMap<String, AttributesAPI.Attribute> = mutableMapOf()
    private val repo get() = RepoAPI.attributes()

    override fun create(key: String): LazyItemStack? {
        val attribute = attributes[key.lowercase()] ?: this.repo.getAttribute(key)
        if (attribute == null) return null
        return attribute.itemStack.let(::LazyItemStack)
    }

    @Subscription(RepoEvent.Reload::class)
    @OnRepoStatus(RepoStatus.SUCCESS)
    fun onRepoReady() {
        this.attributes.putAll(this.repo.attributes().values.associateBy { it.attributeId().lowercase() })
    }

    fun get(id: String): AttributesAPI.Attribute? = ifInitialized {
        this.attributes[id.lowercase()] ?: this.repo.getAttribute(id)
    }
}
