package tech.thatgravyboat.skyblockapi.api.events.base.predicates

import tech.thatgravyboat.repolib.api.RepoStatus
import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicate
import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicateProvider
import tech.thatgravyboat.skyblockapi.api.events.misc.RepoStatusEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.getAnnotation
import java.lang.reflect.Method

annotation class OnRepoStatus(val repoStatus: RepoStatus)

class RepoStatusPredicateProvider : EventPredicateProvider {
    override fun getPredicate(method: Method): EventPredicate? {
        val status = method.getAnnotation<OnRepoStatus>() ?: return null
        return { event, _ ->
            if (event is RepoStatusEvent) {
                event.status == status.repoStatus
            } else {
                true
            }
        }
    }
}
