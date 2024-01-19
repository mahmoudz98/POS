package com.casecode.data.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

class NetworkConnection @Inject constructor(
   @ApplicationContext  private val context: Context,
    val coroutineScope: CoroutineScope,
                                           ) : NetworkMonitor
{
   
   override val isOnline: Flow<Boolean> = callbackFlow {
      
      val connectivityManager = context.getSystemService<ConnectivityManager>()
      if (connectivityManager == null)
      {
         channel.trySend(false)
         channel.close()
         return@callbackFlow
      }
      
      /**
       * The callback's methods are invoked on changes to *any* network matching the [NetworkRequest],
       * not just the active network. So we can simply track the presence (or absence) of such [Network].
       */
      val callback = object : NetworkCallback()
      {
         
         private val networks = mutableSetOf<Network>()
         
         override fun onAvailable(network: Network)
         {
            networks += network
            channel.trySend(true)
         }
         
         override fun onLost(network: Network)
         {
            networks -= network
            channel.trySend(networks.isNotEmpty())
         }
      }
      
      val request = NetworkRequest.Builder()
         .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
         .build()
      connectivityManager.registerNetworkCallback(request, callback)
      
      /**
       * Sends the latest connectivity status to the underlying channel.
       */
      channel.trySend(connectivityManager.isCurrentlyConnected())
      
      awaitClose {
         connectivityManager.unregisterNetworkCallback(callback)
      }
   }
      .conflate()
   
   private fun ConnectivityManager.isCurrentlyConnected() = when
   {
      true -> //Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
          activeNetwork
            ?.let(::getNetworkCapabilities)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            
      
      else -> true//activeNetworkInfo?.isConnected
   } ?: false
   
   
   
   
   // TODO: tests every status with network and improve handle callback to use pub-sub pattern.
   // sometimes not work and if not subscribe first time not subscribe latter.
   private val connectivityManager: ConnectivityManager by lazy(LazyThreadSafetyMode.NONE) {
      context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
   }
   
   private val _isAvailable = MutableStateFlow(isActive())
   val isAvailable: StateFlow<Boolean> = _isAvailable
      .stateIn(scope = coroutineScope, started = SharingStarted.Lazily,
         isActive())
   
   
   private val networkCallback: NetworkCallback = object : NetworkCallback()
   {
      override fun onAvailable(network: Network)
      {
         
         super.onAvailable(network)
         Timber.tag(TAG).d("onAvailable:network = $network")
         updateNetworkStatus(true)
      }
      
      override fun onLost(network: Network)
      {
         super.onLost(network)
         updateNetworkStatus(false)
         Timber.tag(TAG).d("onLost = $network")
         
      }
      
      override fun onCapabilitiesChanged(
           network: Network,
           networkCapabilities: NetworkCapabilities,
                                        )
      {
         super.onCapabilitiesChanged(network, networkCapabilities)
         val unmetered =
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
         Timber.tag(TAG).d("onCapabilitiesChanged = $unmetered")
         
         // updateNetworkStatus(unmetered)
         
      }
   }
   
   init
   {
      registerNetworkCallback()
      updateNetworkStatus(isActive())
   }
   
   fun onDestroy()
   {
      unregisterNetworkCallback()
   }
   
   private fun updateNetworkStatus(isAvailable: Boolean)
   {
      _isAvailable.value = isAvailable
      
   }
   
   
   private fun registerNetworkCallback()
   {
      
      val request = NetworkRequest.Builder()
         .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
         .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
         .build()
      
      coroutineScope.launch {
         try
         {
            Timber.d("Thread : ${currentCoroutineContext()}")
            connectivityManager.registerNetworkCallback(request, networkCallback)
            
         } catch (e: Exception)
         {
            Timber.tag(TAG).e(e)
         }
      }
   }
   
   private fun unregisterNetworkCallback()
   {
      coroutineScope.launch {
         try
         {
            connectivityManager.unregisterNetworkCallback(networkCallback)
         } catch (e: Exception)
         {
            Timber.tag(TAG).e(e)
         }
      }
      
   }
   
   private fun isActive(): Boolean
   {
      val capabilities = connectivityManager.getNetworkCapabilities(
         connectivityManager.activeNetwork
                                                                   )
      return capabilities != null &&
           (
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                     capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                     (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                          capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)))
      
   }
   
   
   companion object
   {
      const val TAG = "NetworkConnection"
   }
}