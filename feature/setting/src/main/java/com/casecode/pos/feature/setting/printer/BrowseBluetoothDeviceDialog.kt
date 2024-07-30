package com.casecode.pos.feature.setting.printer

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import timber.log.Timber


@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun BrowseBluetoothDeviceDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val context = LocalContext.current

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
        )
    } else {
        listOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN,
        )
    }

    val bluetoothPermissions = rememberMultiplePermissionsState(permissions)
    LaunchedEffect(Unit) {
        if (!bluetoothPermissions.allPermissionsGranted) {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

    var selectedDevice by remember { mutableStateOf<BluetoothConnection?>(null) }
    val bluetoothDevicesList by remember { mutableStateOf<MutableList<BluetoothConnection>?>(null) }
    Timber.e("bluetoothDevicesList: $bluetoothDevicesList")
    val items = bluetoothDevicesList?.let { list ->
        arrayOfNulls<String>(list.size + 1).apply {
            list.forEachIndexed { index, device ->
                Timber.e("device: $device")
                this[index + 1] = device.device.name
            }
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bluetooth printer selection") },
        text = {
            Column {
                items?.forEachIndexed { i, item ->
                    item?.let {
                        Button(
                            onClick = {
                                val index = i - 1
                                selectedDevice =
                                    if (index == 0) null else bluetoothDevicesList?.get(index - 1)
                            },
                        ) {
                            Text(text = item)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                },
            ) {
                Text("Ok")
            }
        },
    )

}