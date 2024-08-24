package com.casecode.pos.core.data.di

import android.content.Context
import com.casecode.pos.core.common.di.ApplicationScope
import com.casecode.pos.core.data.utils.ConnectivityManagerNetworkMonitor
import com.casecode.pos.core.data.utils.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideConnectivityManagerNetworkMonitor(
        @ApplicationContext context: Context,
        @ApplicationScope coroutineScope: CoroutineScope,
    ): NetworkMonitor = ConnectivityManagerNetworkMonitor(context, coroutineScope)
}