package com.casecode.pos.core.printer.utils

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections

object BluetoothUtils {
    private const val PERMISSION_BLUETOOTH_CONNECT = 3
    private const val PERMISSION_BLUETOOTH_SCAN = 4

    fun checkBluetoothPermissions(
        context: Context,
        onPermissionsGranted: () -> Unit,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT,
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN,
                    ),
                    PERMISSION_BLUETOOTH_CONNECT,
                )
            } else {
                onPermissionsGranted()
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH,
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_ADMIN,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN),
                    PERMISSION_BLUETOOTH_CONNECT,
                )
            } else {
                onPermissionsGranted()
            }
        }
    }

    fun getAvailableBluetoothPrinters(): List<BluetoothConnection> = BluetoothPrintersConnections().list?.toList() ?: emptyList()

    fun connectToBluetoothPrinter(device: BluetoothDevice): BluetoothConnection = BluetoothConnection(device)
}