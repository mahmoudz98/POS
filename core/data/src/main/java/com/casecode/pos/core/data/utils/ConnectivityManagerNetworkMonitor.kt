/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.casecode.pos.core.data.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.NetworkRequest.Builder
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import timber.log.Timber
import javax.inject.Inject

class ConnectivityManagerNetworkMonitor
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        val coroutineScope: CoroutineScope,
    ) : NetworkMonitor {
        override val isOnline: Flow<Boolean> =
            callbackFlow {
                val connectivityManager = context.getSystemService<ConnectivityManager>()
                if (connectivityManager == null) {
                    channel.trySend(false)
                    channel.close()
                    return@callbackFlow
                }

                /**
                 * The callback's methods are invoked on changes to *any* network matching the [NetworkRequest],
                 * not just the active network. So we can simply track the presence (or absence) of such [Network].
                 */
                val callback =
                    object : NetworkCallback() {
                        private val networks = mutableSetOf<Network>()

                        override fun onAvailable(network: Network) {
                            networks += network
                            channel.trySend(true)
                        }

                        override fun onLost(network: Network) {
                            networks -= network
                            channel.trySend(networks.isNotEmpty())
                        }
                    }

                val request =
                    Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                        .build()
                connectivityManager.registerNetworkCallback(request, callback)

                /**
                 * Sends the latest connectivity status to the underlying channel.
                 */
                channel.trySend(connectivityManager.isCurrentlyConnected())

                awaitClose {
                    connectivityManager.unregisterNetworkCallback(callback)
                    Timber.d("close connectivity manager")
                }
            }.conflate()

        private fun ConnectivityManager.isCurrentlyConnected(): Boolean {
            val capabilities =
                this.getNetworkCapabilities(
                    this.activeNetwork,
                )
            return capabilities != null &&
                (
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        capabilities.hasCapability(
                            NetworkCapabilities.NET_CAPABILITY_VALIDATED,
                        ) &&
                        (
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                                        )
                        )
    }
}