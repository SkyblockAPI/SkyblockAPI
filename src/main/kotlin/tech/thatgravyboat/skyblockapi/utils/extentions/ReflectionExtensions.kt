package tech.thatgravyboat.skyblockapi.utils.extentions

import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

inline fun <reified T : Annotation> Method.getAnnotation(): T? =
    getAnnotation(T::class.java)

inline fun <reified T : Annotation> Method.hasAnnotation(): Boolean = getAnnotation<T>() != null

fun <T : Any> KClass<T>.getEmptyConstructor(): KFunction<T>? =
    constructors.find { constructor ->
        constructor.parameters.all { it.isOptional }
    }
