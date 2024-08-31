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
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class MyBluetoothConnection(
    internal var device: BluetoothDevice? = null,
    private val context: Context,
) : DeviceConnection() {
    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private var socket: BluetoothSocket? = null
    /*  private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
          device?.createRfcommSocketToServiceRecord(SPP_UUID)
      }*/


    /**
     * Check if OutputStream is open.
     *
     * @return true if connected
     */
    override fun isConnected(): Boolean {
        return this.socket != null && this.socket!!.isConnected && super.isConnected()
    }

    @SuppressLint("ServiceCast")
    @Throws(EscPosConnectionException::class)
    override fun connect(): DeviceConnection? {
        if (this.isConnected) {
            return this
        }
        if (this.device == null) {
            throw EscPosConnectionException("Bluetooth device is not connected.")

        }
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
        val uuid = this.getDeviceUUID()
        try {
            this.socket = this.device?.createInsecureRfcommSocketToServiceRecord(uuid)
            bluetoothAdapter.cancelDiscovery()
            this.socket?.connect()
            this.outputStream = this.socket?.outputStream
            this.data = byteArrayOf(0)
        } catch (e: IOException) {
            e.printStackTrace()
            this.disconnect()
            throw EscPosConnectionException("Unable to connect to bluetooth device.");
        }
        return this
    }

    /**
     * Get Bluetooth device UUID
     */
    protected fun getDeviceUUID(): UUID {
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

    override fun disconnect(): DeviceConnection? {
        this.data = byteArrayOf()
        if (this.outputStream != null) {
            try {
                this.outputStream.flush() // I added this line
                this.outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace();
            }
            this.outputStream = null
        }
        if (this.socket != null) {
            try {
                this.socket?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            this.socket = null
        }
        return this
    }
}