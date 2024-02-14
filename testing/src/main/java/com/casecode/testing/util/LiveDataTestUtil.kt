package com.casecode.testing.util

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.casecode.testing.BaseTest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
     time: Long = 2,
     timeUnit: TimeUnit = TimeUnit.SECONDS,
     afterObserve: () -> Unit = {}
                                   ): T {
   var data: T? = null
   val latch = CountDownLatch(1)
   val observer = object : Observer<T?>
   {
      override fun onChanged(value: T?) {
         data = value
         latch.countDown()
         this@getOrAwaitValue.removeObserver(this)
      }
   }
   this.observeForever(observer)
   
   try {
      afterObserve.invoke()
      
      // Don't wait indefinitely if the LiveData is not set.
      if (!latch.await(time, timeUnit)) {
         throw TimeoutException("LiveData value was never set.")
      }
      
   } finally {
      this.removeObserver(observer)
   }
   
   @Suppress("UNCHECKED_CAST")
   return data as T
}

 suspend fun <T> getValueOrThrow(
     liveData: LiveData<T>,
     postFunction: (() -> Any)? = null,
     timeout: Long = 3000,
                                         ): T
{
   var result: T? = null
   liveData.observeForever {
      result = it
   }
   
   if (postFunction != null)
   {
      val postJob = postFunction()
      if (postJob is Job) postJob.join()
   }
   delay(timeout)
   
   if (result == null) throw TimeoutException(ERROR_MESSAGE_LIVEDATA_NULL)
   else return result !!
}

    const val ERROR_MESSAGE_LIVEDATA_NULL = "LiveData has null value"
