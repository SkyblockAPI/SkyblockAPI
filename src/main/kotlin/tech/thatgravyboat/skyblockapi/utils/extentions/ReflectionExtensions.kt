package tech.thatgravyboat.skyblockapi.utils.extentions

import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType

public inline fun <reified T : Annotation> Method.getAnnotation(): T? =
    getAnnotation(T::class.java)

public inline fun <reified T : Annotation> Method.hasAnnotation(): Boolean = getAnnotation<T>() != null

public val KType.kClass: KClass<*>? get() = classifier as? KClass<*>

public fun <T : Any> KClass<T>.getEmptyConstructor(): KFunction<T>? =
    constructors.find { constructor ->
        constructor.parameters.all { it.isOptional }
    }
