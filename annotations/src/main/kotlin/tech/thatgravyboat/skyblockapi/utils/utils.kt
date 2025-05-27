package tech.thatgravyboat.skyblockapi.utils

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.ksp.toClassName

fun KSTypeReference.resolveClassName() = this.resolve().resolveClassName()
fun KSType.resolveClassName() = (this.starProjection().declaration as KSClassDeclaration).toClassName()
