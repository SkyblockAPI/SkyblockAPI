package tech.thatgravyboat.skyblockapi.utils.extentions

import java.lang.reflect.Method

inline fun <reified T : Annotation> Method.getAnnotation(): T? =
    getAnnotation(T::class.java)
