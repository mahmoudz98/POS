package com.casecode.domain.utils

import java.lang.Exception

sealed class Resource<out T : Any?> {
   
    class Loading<out T>(): Resource<T>()
    data class Success<out T : Any>(val data: T) : Resource<T>()
    data class Error(val exception: Exception?) : Resource<Nothing>()
    data class Empty<T>(val data: T? = null): Resource<T>()
}