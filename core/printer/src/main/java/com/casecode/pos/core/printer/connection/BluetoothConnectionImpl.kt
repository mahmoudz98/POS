/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casecode.pos.core.printer.connection

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.ParcelUuid
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class BluetoothConnectionImpl(
    internal var device: BluetoothDevice? = null,
    private val context: Context,
) : DeviceConnection() {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        device?.createRfcommSocketToServiceRecord(getDeviceUUID())
    }
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    /**
     * Check if OutputStream is open.
     *
     * @return true if connected
     */
    override fun isConnected(): Boolean = this.mmSocket != null && this.mmSocket!!.isConnected && super.isConnected

    @Throws(EscPosConnectionException::class)
    override fun connect(): DeviceConnection? {
        return return runBlocking {
            try {
                performConnect()
            } catch (e: EscPosConnectionException) {
                Timber.e("Failed to connect: ${e.message}")
                throw e
            }
        }
    }

    @Throws(EscPosConnectionException::class)
    private suspend fun performConnect(): DeviceConnection? {
        if (this.isConnected) {
            return this
        }
        if (this.device == null) {
            throw EscPosConnectionException("Bluetooth device is not connected.")
        }
        Timber.d("Attempting to connect to device: ${device?.name} ${device?.address}")
        try {
            withContext(scope.coroutineContext) {
                var retries = 3
                while (retries > 0) {
                    try {
                        bluetoothAdapter.cancelDiscovery()

                        mmSocket?.connect()
                        this@BluetoothConnectionImpl.outputStream = mmSocket?.outputStream
                        this@BluetoothConnectionImpl.data = byteArrayOf(0)
                    } catch (e: IOException) {
                        Timber.e(e)
                        retries--
                        delay(1000)
                        disconnect()
                        if (retries == 0) {
                            throw EscPosConnectionException(
                                "Unable to connect to bluetooth device, ${e.message}",
                            )
                        }
                    }
                }
            }
        } catch (e: IOException) {
            disconnect()
            throw EscPosConnectionException("Unable to connect to bluetooth device, ${e.message}")
        }

        return this
    }

    /**
     * Get Bluetooth device UUID
     */
    private fun getDeviceUUID(): UUID {
        val uuids = device?.uuids
        return if (uuids != null && uuids.isNotEmpty()) {
            if (uuids.contains(ParcelUuid(SPP_UUID))) {
                SPP_UUID
            } else {
                uuids[0].uuid
            }
        } else {
            SPP_UUID
        }
    }

    override fun disconnect(): DeviceConnection? = runBlocking {
        try {
            performDisconnect()
        } catch (e: IOException) {
            Timber.e(e)
            throw e
        }
    }

    private suspend fun performDisconnect(): DeviceConnection? {
        this.data = byteArrayOf()
        try {
            withContext(scope.coroutineContext) {
                this@BluetoothConnectionImpl.outputStream?.close()
                this@BluetoothConnectionImpl.outputStream = null
                this@BluetoothConnectionImpl.mmSocket?.close()
            }
        } catch (e: IOException) {
            Timber.e(e)
            throw e
        }
        return this
    }

    companion object {
        private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
}