package tech.thatgravyboat.skyblockapi.api.datatype

import net.minecraft.world.item.ItemStack
import org.jetbrains.annotations.ApiStatus
//? < 26.2
//import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.impl.DataTypesRegistry
import tech.thatgravyboat.skyblockapi.utils.extentions.getCompoundTagFunctionByType
import tech.thatgravyboat.skyblockapi.utils.extentions.unsafeTag
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class DataType<T> private constructor(
    val id: String,
    autoRegister: Boolean,
    private val resolver: (ResolutionContext, ItemStack) -> T?,
    val type: KType?,
) {

    //? < 26.2
    //@RemoveNextVersion constructor(id: String, autoRegister: Boolean, factory: (ItemStack) -> T?, type: KType?) : this(id, autoRegister, { _, stack -> factory(stack) }, type)

    //? < 26.2
    //@RemoveNextVersion constructor(id: String, autoRegister: Boolean, factory: (ItemStack) -> T?) : this(id, autoRegister, factory, null)

    //? < 26.2
    //@RemoveNextVersion constructor(id: String, factory: (ItemStack) -> T?) : this(id, true, factory)

    @get:Deprecated("Scheduled for removal in 26.3")
    @get:ApiStatus.ScheduledForRemoval
    val factory: (ItemStack) -> T? get() = ::resolve

    init {
        if (autoRegister) DataTypesRegistry.addDataType(this)
    }

    @Suppress("UNCHECKED_CAST")
    fun cast(value: Any?): T? = value as? T

    fun resolve(stack: ItemStack, context: ResolutionContext = ResolutionContext(stack)): T? {
        return resolver.invoke(context, stack)
    }

    companion object {

        fun <T> of(id: String, type: KType, autoRegister: Boolean = true, resolver: (ResolutionContext, ItemStack) -> T?): DataType<T> {
            return DataType(id, autoRegister, resolver, type)
        }

        inline fun <reified T> of(id: String, autoRegister: Boolean = true, noinline resolver: (ResolutionContext, ItemStack) -> T?): DataType<T> {
            return of(id, typeOf<T>(), autoRegister, resolver)
        }

        fun <T> of(id: String, type: KType, autoRegister: Boolean = true, factory: (ItemStack) -> T?): DataType<T> {
            return DataType(id, autoRegister, { _, stack -> factory(stack) }, type)
        }

        inline fun <reified T> of(id: String, autoRegister: Boolean = true, noinline factory: (ItemStack) -> T?): DataType<T> {
            return of(id, typeOf<T>(), autoRegister, factory)
        }

        fun <T : Any> simple(id: String, type: KType, tagName: String = id, autoRegister: Boolean = true): DataType<T> {
            val function = getCompoundTagFunctionByType<T>(type)
            return of(id, type, autoRegister) { item -> item.unsafeTag?.let { function(it, tagName) } }
        }

        /**
         * Creates a [DataType] that gets the tag named [tagName] from the item's Custom Data.
         *
         * See [getCompoundTagFunctionByType] for the allowed classes.
         */
        inline fun <reified T : Any> simple(id: String, tagName: String = id, autoRegister: Boolean = true): DataType<T> {
            return simple(id, typeOf<T>(), tagName, autoRegister)
        }
    }
}
