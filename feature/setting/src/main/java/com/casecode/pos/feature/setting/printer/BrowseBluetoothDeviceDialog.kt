package com.casecode.pos.feature.setting.printer

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.casecode.pos.feature.setting.R
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnections
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.google.accompanist.permissions.ExperimentalPermissionsApi

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
    // TODO:remove this object used only for test
    val bluetoothConnections = BluetoothConnections()
    val bluetoothPrintersConnections = BluetoothPrintersConnections()
    val bluetoothDevicesList = remember { mutableStateListOf<BluetoothConnection>() }

    LaunchedEffect(
        Unit,
        bluetoothConnections.filterForPrintersTest(),
        bluetoothPrintersConnections.list,
    ) {
        bluetoothDevicesList.clear()
        bluetoothDevicesList.addAll(bluetoothConnections.filterForPrintersTest()) // TODO:remove this invoke
        //bluetoothDevicesList.addAll(bluetoothPrintersConnections.list ?: emptyArray())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.feature_setting_printer_dialog_bluetooth_devices_title)) },
        text = {
            Column {
                if (bluetoothDevicesList.isEmpty()) {
                    Text(stringResource(R.string.feature_setting_printer_dialog_bluetooth_devices_message_empty))
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        contentPadding = PaddingValues(vertical = 4.dp),
                    ) {
                        bluetoothDevicesList.forEach {
                            item(key = it.device.address) {
                                ListItem(
                                    headlineContent = { Text(it.device.name) },
                                    modifier =
                                        Modifier.clickable {
                                            onSelectPrinterDevice(it)
                                        },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                )
                            }
                        }
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