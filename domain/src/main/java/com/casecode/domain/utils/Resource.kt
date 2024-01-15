package com.casecode.domain.utils

sealed class Resource<out T>()
{
   abstract val data: T?
   
    data class Success<T>(override val data: T ) : Resource<T>()
    data class Error<T>(val message: Any?, override val data: T? = null) : Resource<T>()
    class Loading<T>(override val data: T? = null) : Resource<T>()
   data class Empty<T>(val emptyType : EmptyType? = null,val message: Any? = null) :
      Resource<T>()
   {
      override val data: T? = null
   }
   
   companion object
   {
      fun <T> success(data: T): Resource<T> = Success(data)
      fun <T> error(message: Any?): Resource<T> = Error(message, null)
      fun <T> loading(data: T? = null): Resource<T> = Loading(data)
      fun <T> empty( emptyType: EmptyType? = null, message: Any? = null): Resource<T> {
         return Empty(emptyType,message)
         
      }
      
   }
   
   override fun toString(): String
   {
      return when (this)
      {
         is Success<*> -> "Success[data=$data]"
         is Error -> "Error[exception=$message]"
         is Empty -> "Canceled[exception=$message]"
         is Loading -> "Canceled[exception=$data]"
      }
   }
}
