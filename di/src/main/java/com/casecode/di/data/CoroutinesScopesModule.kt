package com.casecode.di.data

import com.casecode.data.utils.AppDispatchers
import com.casecode.data.utils.Dispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

@InstallIn(SingletonComponent::class)
@Module
internal object CoroutinesScopesModule
{
   @Singleton
   @Provides
   @ApplicationScope
   fun providesCoroutineScope(
        @Dispatcher(AppDispatchers.DEFAULT)
        defaultDispatcher: CoroutineDispatcher,
                             )
        : CoroutineScope = CoroutineScope(SupervisorJob() + defaultDispatcher)
   
}