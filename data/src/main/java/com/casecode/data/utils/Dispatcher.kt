package com.casecode.data.utils

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME


/*  @Target(AnnotationTarget.PROPERTY,
   AnnotationTarget.VALUE_PARAMETER,
   AnnotationTarget.FUNCTION) */
@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val appDispatcher: AppDispatchers)
enum class AppDispatchers {
   IO,
   DEFAULT,
   MAIN}