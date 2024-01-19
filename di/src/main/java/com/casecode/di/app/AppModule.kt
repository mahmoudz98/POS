package com.casecode.di.app

import android.content.Context
import com.casecode.data.utils.ConnectivityManagerNetworkMonitor
import com.casecode.data.utils.NetworkConnection
import com.casecode.data.utils.NetworkMonitor
import com.casecode.di.data.ApplicationScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule
{
   @Provides
   @Singleton
   fun provideNetworkConnection(
        @ApplicationContext context: Context,
        @ApplicationScope coroutineScope: CoroutineScope,
                               ): NetworkConnection
   {
      return NetworkConnection(context, coroutineScope)
   }
   @Provides
   @Singleton
   fun provideConnectivityManagerNetworkMonitor(
        @ApplicationContext context: Context,
        @ApplicationScope coroutineScope: CoroutineScope,
                               ): NetworkMonitor
   {
      return ConnectivityManagerNetworkMonitor(context, coroutineScope)
   }
}