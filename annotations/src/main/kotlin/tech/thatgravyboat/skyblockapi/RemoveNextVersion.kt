package tech.thatgravyboat.skyblockapi

import kotlin.annotation.AnnotationTarget.*

@Target(CLASS, FUNCTION, PROPERTY, ANNOTATION_CLASS, CONSTRUCTOR, PROPERTY_SETTER, PROPERTY_GETTER, TYPEALIAS)
annotation class RemoveNextVersion(
    val replaceWith: ReplaceWith = ReplaceWith(""),
    val level: DeprecationLevel = DeprecationLevel.WARNING,
)
