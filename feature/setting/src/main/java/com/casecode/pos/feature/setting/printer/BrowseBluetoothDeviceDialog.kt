package com.casecode.pos.feature.setting.printer

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.casecode.pos.feature.setting.R
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnections
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
// TODO: remove from here and support only devices printer, not all devices
@SuppressLint("MissingPermission")
fun BluetoothConnections.filterForPrintersTest(): Array<BluetoothConnection> {
    val bluetoothDevicesList: Array<BluetoothConnection> = this.getList() ?: emptyArray()

    val printers = mutableListOf<BluetoothConnection>()
    for (bluetoothConnection in bluetoothDevicesList) {
        printers.add(bluetoothConnection)
    }
    return printers.toTypedArray()
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BrowseBluetoothDeviceDialog(
    onDismiss: () -> Unit,
    onSelectPrinterDevice: (BluetoothConnection) -> Unit,
) {
    //TODO:remove this object used only for test
    val bluetoothConnections = BluetoothConnections()
    val bluetoothPrintersConnections = BluetoothPrintersConnections()
    val bluetoothDevicesList = remember { mutableStateListOf<BluetoothConnection>() }

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
        )
    } else {
        listOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
        )
    }

    val bluetoothPermissions = rememberMultiplePermissionsState(permissions)
    LaunchedEffect(Unit, bluetoothConnections.filterForPrintersTest(), bluetoothPrintersConnections.list) {
        if (bluetoothPermissions.allPermissionsGranted) {

            bluetoothDevicesList.clear()
            bluetoothDevicesList.addAll(bluetoothConnections.filterForPrintersTest()) //TODO:remove this invoke
            bluetoothDevicesList.addAll(bluetoothPrintersConnections.list?: emptyArray())
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.feature_setting_printer_dialog_bluetooth_devices_title)) },
        text = {
            Column {
                if (bluetoothDevicesList.isEmpty()) {
                    Text(stringResource(R.string.feature_setting_printer_dialog_bluetooth_devices_message_empty))
                } else {
                    bluetoothDevicesList.forEachIndexed { _, device ->
                        Text(
                            text = device.device.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    onSelectPrinterDevice(device)

                                },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(stringResource(com.casecode.pos.core.ui.R.string.core_ui_dialog_cancel_button_text))
            }
        },
    )

}