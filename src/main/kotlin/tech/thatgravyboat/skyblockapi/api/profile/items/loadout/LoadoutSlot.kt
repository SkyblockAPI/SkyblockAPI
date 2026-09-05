package tech.thatgravyboat.skyblockapi.api.profile.items.loadout

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.ExperimentalExtendedContracts
import kotlin.contracts.contract
import kotlin.time.Instant

@GenerateCodec
data class LoadoutSlot(
    val id: Int,
    var name: String?,
    var armor: NumberMatch?,
    var equipment: NumberMatch?,
    var pet: StringMatch?,
    var powerstone: StringMatch?,
    var tunings: NumberMatch?,
    var hotm: StringMatch?,
    var hotf: StringMatch?,
    var locked: Boolean = true,
) {
    constructor(id: Int) : this(id, null,null,null,null,null,null,null,null)

    context(source: DataSource)
    fun value(value: Int?) = NumberMatch(value, source, currentInstant())
    context(source: DataSource)
    fun value(value: String?) = StringMatch(value, source, currentInstant())
}

enum class DataSource(val multiplier: Double) {
    API(0.5),
    OVERVIEW(0.5),
    EDIT(1.0),
    ;
}

@OptIn(ExperimentalContracts::class, ExperimentalExtendedContracts::class)
fun NumberMatch?.value(default: Int? = null): Int? {
    contract {
        (default != null) implies returnsNotNull()
    }
    return this?.value ?: default
}


@OptIn(ExperimentalContracts::class, ExperimentalExtendedContracts::class)
operator fun NumberMatch?.invoke(default: Int? = null): Int? {
    contract {
        (default != null) implies returnsNotNull()
    }
    return this?.value ?: default
}

@OptIn(ExperimentalContracts::class, ExperimentalExtendedContracts::class)
fun StringMatch?.value(default: String? = null): String? {
    contract {
        (default != null) implies returnsNotNull()
    }
    return this?.value ?: default
}


@OptIn(ExperimentalContracts::class, ExperimentalExtendedContracts::class)
operator fun StringMatch?.invoke(default: String? = null): String? {
    contract {
        (default != null) implies returnsNotNull()
    }
    return this?.value ?: default
}

@GenerateCodec
data class NumberMatch(
    val value: Int?,
    val source: DataSource,
    val time: Instant,
)

@GenerateCodec
data class StringMatch(
    val value: String?,
    val source: DataSource,
    val time: Instant,
)
