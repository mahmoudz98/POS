package com.casecode.pos.core.testing.di

import com.casecode.pos.core.common.AppDispatchers.DEFAULT
import com.casecode.pos.core.common.AppDispatchers.IO
import com.casecode.pos.core.common.Dispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [com.casecode.pos.core.common.di.DispatchersModule::class],
)
object TestDispatchersModule {
    @Provides
    @Dispatcher(IO)
    fun providesIODispatcherTest(testDispatcher: TestDispatcher): CoroutineDispatcher = testDispatcher

    @Provides
    @Dispatcher(DEFAULT)
    fun providesDefaultDispatcherTest(testDispatcher: TestDispatcher): CoroutineDispatcher =
        testDispatcher
}