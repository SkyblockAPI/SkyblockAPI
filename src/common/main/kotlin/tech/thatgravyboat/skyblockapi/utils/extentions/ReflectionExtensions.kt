package tech.thatgravyboat.skyblockapi.utils.extentions

import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

inline fun <reified T : Annotation> Method.getAnnotation(): T? =
    getAnnotation(T::class.java)

fun <T : Any> KClass<T>.getEmptyConstructor(): KFunction<T>? =
    constructors.find { constructor ->
        constructor.parameters.all { it.isOptional }
    }

/**
 * Returns a [List] of all Objects that extend/implement the sealed class/interface [T].
 * Will throw an [IllegalArgumentException] if one of the classes that extends/implements [T] isn't an object,
 * or if [T] isn't a sealed class.
 */
fun <T : Any> getSealedObjects(kClass: KClass<T>): List<T> {
    require(kClass.isSealed) { "${kClass.simpleName} is not a sealed class/interface" }
    return kClass.sealedSubclasses.map {
        requireNotNull(it.objectInstance) { "${it.simpleName} is not an object" }
    }
}
/**
 * Returns a [List] of all Objects that extend/implement the sealed class/interface [T].
 * Will throw an [IllegalArgumentException] if one of the classes that extends/implements [T] isn't an object,
 * or if [T] isn't a sealed class.
 */
inline fun <reified T : Any> getSealedObjects(): List<T> = getSealedObjects(T::class)
