package tech.thatgravyboat.skyblockapi.modules

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.VALUE_PARAMETER)
annotation class FieldName(val value: String)
