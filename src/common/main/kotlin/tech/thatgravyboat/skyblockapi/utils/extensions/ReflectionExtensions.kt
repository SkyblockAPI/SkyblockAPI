package tech.thatgravyboat.skyblockapi.utils.extensions

import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

inline fun <reified T : Annotation> Method.getAnnotation(): T? =
    getAnnotation(T::class.java)

fun <T : Any> KClass<T>.getEmptyConstructor(): KFunction<T>? =
    constructors.find { constructor ->
        constructor.parameters.all { it.isOptional }
    }
