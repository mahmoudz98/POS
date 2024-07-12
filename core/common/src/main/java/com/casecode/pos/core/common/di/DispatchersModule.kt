package com.casecode.pos.core.common.di

import com.casecode.pos.core.common.AppDispatchers.DEFAULT
import com.casecode.pos.core.common.AppDispatchers.IO
import com.casecode.pos.core.common.Dispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    @Dispatcher(DEFAULT)
    @Provides
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Dispatcher(IO)
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO


}