package com.casecode.di.data

import com.casecode.data.utils.AppDispatchers.DEFAULT
import com.casecode.data.utils.AppDispatchers.IO
import com.casecode.data.utils.AppDispatchers.MAIN
import com.casecode.data.utils.Dispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
 object DispatchersModule
{
   
   
   @Dispatcher(DEFAULT)
   @Provides
   fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
   
   @Dispatcher(IO)
   @Provides
   fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
   
   @Dispatcher(MAIN)
   @Provides
   fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
   
   
}
