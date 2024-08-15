package com.casecode.pos.feature.setting.printer

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosOutlinedButton
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.PrinterConnectionType
import com.casecode.pos.core.model.data.toConnectionType
import com.casecode.pos.core.printer.base.PrinterState
import com.casecode.pos.feature.setting.R
import com.casecode.pos.core.printer.base.UsbEscPosPrint.Companion.ACTION_USB_PERMISSION
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.usb.UsbConnection
import com.dantsu.escposprinter.connection.usb.UsbConnections
import com.dantsu.escposprinter.connection.usb.UsbDeviceHelper
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import timber.log.Timber
import com.casecode.pos.core.printer.R as resourcePrinter

@Composable
internal fun PrinterInfoRoute(
    printerViewModel: PrinterViewModel = hiltViewModel(),
    isUpdate: Boolean = false,
    onBackClick: () -> Unit,
) {
    val printerState by printerViewModel.printerState.collectAsStateWithLifecycle()
    PrinterInfoScreen(
        isUpdate = isUpdate,
        onBackClick = onBackClick,
        onSavedClick = {},
     /*   onTestClick = {typePrinterConnection, namePrinter, ipAddress, port, bluetoothConnection, usbDeviceName, isCurrentSelected, paperWidth, context ->
            printerViewModel.testPrinter(
                typePrinterConnection,
                namePrinter,
                ipAddress,
                port,
                bluetoothConnection,
                usbDeviceName,
                isCurrentSelected,
                paperWidth,
                context,
            )},*/


        onTestClick = printerViewModel::testPrinter,
        printerState = printerState
    )
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PrinterInfoScreen(
    modifier: Modifier = Modifier,
    isUpdate: Boolean,
    onBackClick: () -> Unit,
    onTestClick: (
        typePrinterConnection: String,
        namePrinter: String,
        ipAddress: String,
        port: String,
        bluetoothConnection: BluetoothConnection?,
        usbDeviceName: String,
        isCurrentSelected: Boolean,
        paperWidth: String,
        context: Context,
    ) -> Unit,
    printerState: PrinterState,
    onSavedClick: () -> Unit,
) {
    val context = LocalContext.current

    var printerName by remember { mutableStateOf("") }
    val printerEthernetDefault =
        stringResource(resourcePrinter.string.core_printer_info_connection_ethernet)

    val paperWidthDefault = stringResource(resourcePrinter.string.core_printer_info_paper_width_72)
    var selectedTypeConnection by remember { mutableStateOf(printerEthernetDefault) }
    var selectedPaperWidth by remember { mutableStateOf(paperWidthDefault) }

    var ipAddress by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }
    var nameUsb by remember { mutableStateOf("") }

    var typeConnectionExpanded by remember { mutableStateOf(false) }
    var paperWidthExpanded by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf(false) }
    val typeConnectionError by remember { mutableStateOf(false) }
    var ipAddressError by remember { mutableStateOf<Int?>(null) }
    var portError by remember { mutableStateOf<Int?>(null) }
    var nameBluetoothError by remember { mutableStateOf(false) }
    var nameUsbError by remember { mutableStateOf(false) }
    val paperWidthError by remember { mutableStateOf(false) }

    var showBrowseBluetoothDialog by remember { mutableStateOf(false) }
    var showStatePrinterDialog by remember { mutableStateOf(false) }
    var selectedDevice by remember { mutableStateOf<BluetoothConnection?>(null) }


    if (showBrowseBluetoothDialog) {
        BrowseBluetoothDeviceDialog(
            onDismiss = { showBrowseBluetoothDialog = false },
            onSelectPrinterDevice = {
                selectedDevice = it
                showBrowseBluetoothDialog = false
            },
        )
    }
    if (showStatePrinterDialog) {
        StatePrinterDialog(
            printerState = printerState,
            onDismiss = { showStatePrinterDialog = false },
        )
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column {
            PosTopAppBar(
                titleRes = R.string.feature_settings_printer_info_title,
                navigationIcon = PosIcons.ArrowBack,
                navigationIconContentDescription = null,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                ),
                action = {
                    PosTextButton(onClick = onSavedClick) {
                        Text(text = stringResource(R.string.feature_setting_printer_info_save_button_text))
                    }
                },
                onNavigationClick = onBackClick,
            )
            Column(Modifier.padding(16.dp)) {

                PosOutlinedTextField(
                    value = printerName,
                    onValueChange = { printerName = it },
                    label = stringResource(R.string.feature_setting_printer_info_name_label),
                    isError = nameError,
                    supportingText = if (nameError) stringResource(R.string.feature_setting_printer_info_error_name_empty) else null,
                    modifier = Modifier.fillMaxWidth(),
                )

                ExposedDropdownMenuBox(
                    expanded = typeConnectionExpanded,
                    onExpandedChange = { typeConnectionExpanded = !typeConnectionExpanded },
                ) {
                    PosOutlinedTextField(
                        value = selectedTypeConnection,
                        onValueChange = {},
                        readOnly = true,
                        isError = typeConnectionError,
                        supportingText = if (typeConnectionError) stringResource(R.string.feature_setting_printer_info_error_interface_connection_empty) else null,

                        label = stringResource(R.string.feature_setting_printer_info_interface_connection_label),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeConnectionExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                    )
                    val connectionType =
                        stringArrayResource(resourcePrinter.array.core_printer_info_connection)

                    ExposedDropdownMenu(
                        expanded = typeConnectionExpanded,
                        onDismissRequest = { typeConnectionExpanded = false },
                    ) {
                        connectionType.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedTypeConnection = type
                                    typeConnectionExpanded = false
                                },
                            )
                        }
                    }
                }
                when(selectedTypeConnection.toConnectionType()){
                    PrinterConnectionType.BLUETOOTH -> {
                        BluetoothFields(
                            selectedDevice,
                            nameBluetoothError,
                            onSearchBluetoothClick = {showBrowseBluetoothDialog = true},
                        )
                    }
                    PrinterConnectionType.USB -> {
                        DisposableEffect(Unit) {
                            val usbReceiver = object : BroadcastReceiver() {
                                override fun onReceive(context: Context, intent: Intent) {
                                    if (ACTION_USB_PERMISSION == intent.action) {
                                        synchronized(this) {
                                            val usbDevice: UsbDevice? = intent.getParcelableExtra(
                                                UsbManager.EXTRA_DEVICE,
                                            )
                                            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {

                                            }
                                        }
                                    }
                                }
                            }

                            val filter = IntentFilter(ACTION_USB_PERMISSION)
                            context.registerReceiver(usbReceiver, filter)

                            onDispose {
                                context.unregisterReceiver(usbReceiver)
                            }
                        }
                        val usbConnection = UsbPrintersConnections.selectFirstConnected(context)
                        nameUsb = usbConnection?.device?.deviceName?:""
                        PosOutlinedTextField(
                            value = nameUsb,
                            enabled = false,
                            onValueChange = { nameUsb = it },
                            label = stringResource(R.string.feature_setting_printer_info_interface_connection_usb_name_label),
                            isError = nameUsbError,
                            supportingText = if (nameUsbError) stringResource(R.string.feature_setting_printer_info_error_usb_name_empty) else null,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    PrinterConnectionType.ETHERNET -> {
                        EthernetFields(
                            ipAddress,
                            onIpAddressChange = { ipAddress = it },
                            ipAddressError,
                            port,
                            onPortChange = { port = it },
                            portError,
                        )
                    }
                    null -> {
                        EthernetFields(
                            ipAddress,
                            onIpAddressChange = { ipAddress = it },
                            ipAddressError,
                            port,
                            onPortChange = { port = it },
                            portError,)
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = paperWidthExpanded,
                    onExpandedChange = { paperWidthExpanded = !paperWidthExpanded },
                ) {
                    PosOutlinedTextField(
                        value = selectedPaperWidth,
                        onValueChange = {},
                        readOnly = true,
                        isError = paperWidthError,
                        supportingText = if (paperWidthError) stringResource(R.string.feature_setting_printer_info_error_papper_width_empty) else null,

                        label = stringResource(R.string.feature_setting_printer_info_papper_width_label),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paperWidthExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                    )
                    val paperWidthTypes =
                        stringArrayResource(resourcePrinter.array.core_printer_info_paper_width)

                    ExposedDropdownMenu(
                        expanded = paperWidthExpanded,
                        onDismissRequest = { paperWidthExpanded = false },
                    ) {
                        paperWidthTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedPaperWidth = type
                                    paperWidthExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        }
        PosOutlinedButton(
            onClick = {
                onTestClick(
                    selectedTypeConnection,
                    printerName,
                    ipAddress,
                    port,
                    selectedDevice,
                    nameUsb,
                    false,
                    selectedPaperWidth.replace(" mm", ""),
                    context,
                )
                showStatePrinterDialog = true


            },
            text = { Text(stringResource(R.string.feature_setting_printer_info_test_button_text)) },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
        )
    }
}
 fun UsbConnections.getList(): Array<UsbConnection?>? {
    val usbConnections: Array<UsbConnection> = this.getList() ?: return null

    var i = 0
    val printersTmp = arrayOfNulls<UsbConnection>(usbConnections.size)
    for (usbConnection in usbConnections) {
        val device = usbConnection.device
        var usbClass = device.deviceClass
        if ((usbClass == UsbConstants.USB_CLASS_PER_INTERFACE || usbClass == UsbConstants.USB_CLASS_MISC) && UsbDeviceHelper.findPrinterInterface(
                device,
            ) != null
        ) {
            usbClass = UsbConstants.USB_CLASS_PRINTER
        }
        if (usbClass == UsbConstants.USB_CLASS_PRINTER) {
          //  printersTmp[i++] = UsbConnection(this.usbManager, device)
        }
    }

    val usbPrinters = arrayOfNulls<UsbConnection>(i)
    System.arraycopy(printersTmp, 0, usbPrinters, 0, i)
    return usbPrinters
}
@SuppressLint("MissingPermission")
@Composable
private fun BluetoothFields(
    selectedDevice: BluetoothConnection?,
    isBluetoothNameError: Boolean,
    onSearchBluetoothClick: () -> Unit,
) {
    val enableBluetoothIntent = remember { Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE) }
    val requestEnableBluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Timber.i("Request enable Bluetooth: Granted")
            onSearchBluetoothClick() // Start discovery after enabling Bluetooth
        } else {
            Timber.i("Request enable Bluetooth: Denied")
            // Handle the case where the user denies Bluetooth permission
        }
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        PosOutlinedTextField(
            value = selectedDevice?.device?.name.orEmpty(),
            onValueChange = { },
            enabled = false,
            label = stringResource(R.string.feature_setting_printer_info_interface_connection_bluetooth_name_label),
            isError = isBluetoothNameError,
            supportingText = if (isBluetoothNameError) {
                stringResource(R.string.feature_setting_printer_info_error_bluetooth_name_empty)
            } else null,
            modifier = Modifier.weight(0.6f),
        )
        PosOutlinedButton(
            onClick = { requestEnableBluetoothLauncher.launch(enableBluetoothIntent) },
            modifier = Modifier
                .weight(0.4f)
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically),
            text = { Text(stringResource(R.string.feature_setting_printer_info_interface_connection_bluetooth_search_button_text)) },
        )
    }
}

@Composable
private fun EthernetFields(
    ipAddress: String,
    onIpAddressChange: (String) -> Unit,
    ipAddressError: Int?,
    port: String,
    onPortChange: (String) -> Unit,
    portError: Int?,
) {
        PosOutlinedTextField(
            value = ipAddress,
            onValueChange = { onIpAddressChange(formatIPAddress(it)) },
            label = stringResource(R.string.feature_setting_printer_info_interface_connection_ethernet_ip_address),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
            isError = ipAddressError != null,
            supportingText = ipAddressError?.let { stringResource(it) },
            modifier = Modifier.fillMaxWidth(),
        )
        PosOutlinedTextField(
            value = port,
            onValueChange = onPortChange,
            label = stringResource(R.string.feature_setting_printer_info_interface_connection_ethernet_port),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done, // Use Done for the last field
            ),
            isError = portError != null,
            supportingText = portError?.let { stringResource(it) },
            modifier = Modifier.fillMaxWidth(),
        )

}



@Preview
@Composable
fun PrinterInfoScreenPreview() {
    POSTheme {
        PosBackground {
            PrinterInfoScreen(
                isUpdate = false,
                onBackClick = {},
                onSavedClick = {},
                onTestClick = { _, _, _, _, _, _, _, _, _ -> },
                printerState = PrinterState.Connecting(12)
            )
        }
    }
}