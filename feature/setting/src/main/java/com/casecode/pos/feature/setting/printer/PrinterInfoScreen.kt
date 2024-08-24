package com.casecode.pos.feature.setting.printer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import com.casecode.pos.core.printer.base.UsbEscPosPrint.Companion.ACTION_USB_PERMISSION
import com.casecode.pos.core.printer.model.PrinterState
import com.casecode.pos.feature.setting.R
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import timber.log.Timber
import com.casecode.pos.core.printer.R as resourcePrinter

@Composable
internal fun PrinterInfoRoute(
    printerViewModel: PrinterViewModel = hiltViewModel(),
    isEditMode: Boolean = false,
    onNavigateBack: () -> Unit,
) {
    val printerStatus by printerViewModel.printerState.collectAsStateWithLifecycle()
    PrinterInfoScreen(
        isEditMode = isEditMode,
        onNavigateBack = onNavigateBack,
        onSave = {},
        onTestEthernetPrinter = printerViewModel::testPrinterEthernet,
        onTestBluetoothPrinter = printerViewModel::testPrinterBluetooth,
        onTestUsbPrinter = printerViewModel::testPrinterUsb,
        printerStatus = printerStatus,
    )
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PrinterInfoScreen(
    rootModifier: Modifier = Modifier,
    isEditMode: Boolean,
    onNavigateBack: () -> Unit,
    onTestEthernetPrinter: (
        namePrinter: String,
        ethernetIpAddress: String,
        ethernetPort: String,
        paperWidth: String,
        applicationContext: Context,
    ) -> Unit? = { _, _, _, _, _ -> },
    onTestBluetoothPrinter: (
        namePrinter: String,
        bluetoothConnection: BluetoothConnection,
        paperWidth: String,
        applicationContext: Context,
    ) -> Unit? = { _, _, _, _ -> },
    onTestUsbPrinter: (
        namePrinter: String,
        usbConnection: String,
        paperWidth: String,
        applicationContext: Context,
    ) -> Unit? = { _, _, _, _ -> },
    printerStatus: PrinterState,
    onSave: () -> Unit,
) {
    val applicationContext = LocalContext.current

    var printerDisplayName by remember { mutableStateOf("") }
    val defaultConnectionType =
        stringResource(resourcePrinter.string.core_printer_info_connection_ethernet)

    val defaultPaperWidth = stringResource(resourcePrinter.string.core_printer_info_paper_width_72)
    var selectedConnectionType by remember { mutableStateOf(defaultConnectionType) }
    var selectedPaperWidthValue by remember { mutableStateOf(defaultPaperWidth) }

    var ethernetIpAddress by remember { mutableStateOf("") }
    var ethernetPort by remember { mutableStateOf("") }
    var usbDeviceName by remember { mutableStateOf("") }

    var isConnectionTypeDropdownExpanded by remember { mutableStateOf(false) }
    var isPaperWidthDropdownExpanded by remember { mutableStateOf(false) }

    var isPrinterNameInvalid by remember { mutableStateOf(false) }
    val isConnectionTypeInvalid by remember { mutableStateOf(false) }
    var ipAddressErrorIndex by remember { mutableStateOf<Int?>(null) }
    var portErrorIndex by remember { mutableStateOf<Int?>(null) }
    var isBluetoothDeviceInvalid by remember { mutableStateOf(false) }
    var isUsbDeviceInvalid by remember { mutableStateOf(false) }
    val isPaperWidthInvalid by remember { mutableStateOf(false) }

    var isPrinterStatusDialogVisible by remember { mutableStateOf(false) }
    var selectedBluetoothConnection by remember { mutableStateOf<BluetoothConnection?>(null) }

    fun hasValidBluetoothInput(): Boolean {
        isPrinterNameInvalid = printerDisplayName.isBlank()
        isBluetoothDeviceInvalid = selectedBluetoothConnection == null
        return isPrinterNameInvalid || isBluetoothDeviceInvalid
    }

    fun hasValidEthernetInput(): Boolean {
        isPrinterNameInvalid = printerDisplayName.isBlank()
        ipAddressErrorIndex = validateIPAddress(ethernetIpAddress)
        portErrorIndex = validatePort(ethernetPort)
        return isPrinterNameInvalid || ipAddressErrorIndex != null || portErrorIndex != null
    }

    if (isPrinterStatusDialogVisible) {
        StatePrinterDialog(
            printerState = printerStatus,
            onDismiss = { isPrinterStatusDialogVisible = false },
        )
    }

    Column(
        modifier = rootModifier.fillMaxSize(),
    ) {
        PosTopAppBar(
            titleRes = R.string.feature_settings_printer_info_title,
            navigationIcon = PosIcons.ArrowBack,
            navigationIconContentDescription = null,
            colors =
                TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                ),
            action = {
                PosTextButton(onClick = onSave) {
                    Text(text = stringResource(R.string.feature_setting_printer_info_save_button_text))
                }
            },
            onNavigationClick = onNavigateBack,
        )
        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            PosOutlinedTextField(
                value = printerDisplayName,
                onValueChange = { printerDisplayName = it },
                label = stringResource(R.string.feature_setting_printer_info_name_label),
                isError = isPrinterNameInvalid,
                supportingText = if (isPrinterNameInvalid) stringResource(R.string.feature_setting_printer_info_error_name_empty) else null,
                modifier = Modifier.fillMaxWidth(),
            )

            ExposedDropdownMenuBox(
                expanded = isConnectionTypeDropdownExpanded,
                onExpandedChange = {
                    isConnectionTypeDropdownExpanded = !isConnectionTypeDropdownExpanded
                },
            ) {
                PosOutlinedTextField(
                    value = selectedConnectionType,
                    onValueChange = {},
                    readOnly = true,
                    isError = isConnectionTypeInvalid,
                    supportingText =
                        if (isConnectionTypeInvalid) {
                            stringResource(
                                R.string.feature_setting_printer_info_error_interface_connection_empty,
                            )
                        } else {
                            null
                        },
                    label = stringResource(R.string.feature_setting_printer_info_interface_connection_label),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isConnectionTypeDropdownExpanded) },
                    modifier =
                        Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                )
                val connectionTypes =
                    stringArrayResource(resourcePrinter.array.core_printer_info_connection)

                ExposedDropdownMenu(
                    expanded = isConnectionTypeDropdownExpanded,
                    onDismissRequest = { isConnectionTypeDropdownExpanded = false },
                ) {
                    connectionTypes.forEach { paperWidthItem ->
                        DropdownMenuItem(
                            text = { Text(paperWidthItem) },
                            onClick = {
                                selectedConnectionType = paperWidthItem
                                isConnectionTypeDropdownExpanded = false
                            },
                        )
                    }
                }
            }
            when (selectedConnectionType.toConnectionType()) {
                PrinterConnectionType.BLUETOOTH -> {
                    BluetoothFields(
                        selectedBluetoothConnection,
                        isBluetoothDeviceInvalid,
                        onSelectPrinterDevice = {
                            selectedBluetoothConnection = it
                        },
                    )
                }

                PrinterConnectionType.USB -> {
                    DisposableEffect(Unit) {
                        val usbPermissionReceiver =
                            object : BroadcastReceiver() {
                                override fun onReceive(
                                    applicationContext: Context,
                                    intent: Intent,
                                ) {
                                    if (ACTION_USB_PERMISSION == intent.action) {
                                        synchronized(this) {
                                            val connectedUsbDevice: UsbDevice? =
                                                intent.getParcelableExtra(
                                                    UsbManager.EXTRA_DEVICE, // UsbDevice::class.java
                                                )
                                            if (intent.getBooleanExtra(
                                                    UsbManager.EXTRA_PERMISSION_GRANTED,
                                                    false,
                                                )
                                            ) {
                                            }
                                        }
                                    }
                                }
                            }

                        val filter = IntentFilter(ACTION_USB_PERMISSION)
                        applicationContext.registerReceiver(usbPermissionReceiver, filter)

                        onDispose {
                            applicationContext.unregisterReceiver(usbPermissionReceiver)
                        }
                    }
                    val usbConnection =
                        UsbPrintersConnections.selectFirstConnected(applicationContext)
                    usbDeviceName = usbConnection?.device?.deviceName ?: ""
                    PosOutlinedTextField(
                        value = usbDeviceName,
                        enabled = false,
                        onValueChange = { usbDeviceName = it },
                        label = stringResource(R.string.feature_setting_printer_info_interface_connection_usb_name_label),
                        isError = isUsbDeviceInvalid,
                        supportingText =
                        if (isUsbDeviceInvalid) {
                            stringResource(
                                R.string.feature_setting_printer_info_error_usb_name_empty,
                            )
                        } else {
                            null
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                PrinterConnectionType.ETHERNET -> {
                    EthernetFields(
                        ethernetIpAddress,
                        onIpAddressChange = {
                            ethernetIpAddress = it
                            ipAddressErrorIndex = validateIPAddress(it)
                        },
                        ipAddressErrorIndex,
                        ethernetPort,
                        onPortChange = {
                            ethernetPort = it
                            portErrorIndex = validatePort(it)
                        },
                        portErrorIndex,
                    )
                }

                null -> {
                }
            }

            ExposedDropdownMenuBox(
                expanded = isPaperWidthDropdownExpanded,
                onExpandedChange = { isPaperWidthDropdownExpanded = !isPaperWidthDropdownExpanded },
            ) {
                PosOutlinedTextField(
                    value = selectedPaperWidthValue,
                    onValueChange = {},
                    readOnly = true,
                    isError = isPaperWidthInvalid,
                    supportingText =
                    if (isPaperWidthInvalid) {
                        stringResource(
                            R.string.feature_setting_printer_info_error_papper_width_empty,
                        )
                    } else {
                        null
                    },
                    label = stringResource(R.string.feature_setting_printer_info_papper_width_label),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPaperWidthDropdownExpanded) },
                    modifier =
                    Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                )
                val paperWidthOptions =
                    stringArrayResource(resourcePrinter.array.core_printer_info_paper_width)

                ExposedDropdownMenu(
                    expanded = isPaperWidthDropdownExpanded,
                    onDismissRequest = { isPaperWidthDropdownExpanded = false },
                ) {
                    paperWidthOptions.forEach { paperWidthItem ->
                        DropdownMenuItem(
                            text = { Text(paperWidthItem) },
                            onClick = {
                                selectedPaperWidthValue = paperWidthItem
                                isPaperWidthDropdownExpanded = false
                            },
                        )
                    }
                }
            }

            PosOutlinedButton(
                onClick = {
                    when (selectedConnectionType.toConnectionType()) {
                        PrinterConnectionType.BLUETOOTH -> {
                            if (!hasValidBluetoothInput()) {
                                onTestBluetoothPrinter(
                                    printerDisplayName,
                                    selectedBluetoothConnection!!,
                                    selectedPaperWidthValue.replace(" mm", ""),
                                    applicationContext,
                                )
                                isPrinterStatusDialogVisible = true
                            }
                        }

                        PrinterConnectionType.USB -> {
                            isPrinterStatusDialogVisible = true

                            //  onTestUsbPrinter(printerName, usbConnection!!, selectedPaperWidth.replace(" mm", ""), context)
                        }

                        PrinterConnectionType.ETHERNET -> {
                            if (!hasValidEthernetInput()) {
                                onTestEthernetPrinter(
                                    printerDisplayName,
                                    ethernetIpAddress,
                                    ethernetPort,
                                    selectedPaperWidthValue.replace(" mm", ""),
                                    applicationContext,
                                )
                                isPrinterStatusDialogVisible = true
                            }
                        }

                        null -> {}
                    }
                },
                text = { Text(stringResource(R.string.feature_setting_printer_info_test_button_text)) },
                modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            )
        }
    }
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun BluetoothFields(
    selectedDevice: BluetoothConnection?,
    isBluetoothNameError: Boolean,
    onSelectPrinterDevice: (BluetoothConnection) -> Unit,
) {
    var showBluetoothDevicesDialog by remember { mutableStateOf(false) }
    if (showBluetoothDevicesDialog) {
        BrowseBluetoothDeviceDialog(
            onDismiss = { showBluetoothDevicesDialog = false },
            onSelectPrinterDevice = {
                onSelectPrinterDevice(it)
                showBluetoothDevicesDialog = false
            },
        )
    }
    val permissions =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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

    val enableBluetoothIntent = remember { Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE) }
    val requestEnableBluetoothLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                Timber.i("Request enable Bluetooth: Granted")
                showBluetoothDevicesDialog = true
            } else {
                showBluetoothDevicesDialog = false
            }
        }
    Timber.e("bluetoothPermissions.allPermissionsGranted: ${bluetoothPermissions.shouldShowRationale}")
    LaunchedEffect(
        key1 = bluetoothPermissions.allPermissionsGranted,
    ) {
        if (showBluetoothDevicesDialog && bluetoothPermissions.allPermissionsGranted) {
            requestEnableBluetoothLauncher.launch(enableBluetoothIntent)
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
            supportingText =
            if (isBluetoothNameError) {
                stringResource(R.string.feature_setting_printer_info_error_bluetooth_name_empty)
            } else {
                null
            },
            modifier = Modifier.weight(0.6f),
        )
        PosOutlinedButton(
            onClick = {
                if (bluetoothPermissions.allPermissionsGranted) {
                    requestEnableBluetoothLauncher.launch(enableBluetoothIntent)
                } else {
                    bluetoothPermissions.launchMultiplePermissionRequest()
                }
            },
            modifier =
            Modifier
                .weight(0.4f)
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically),
            text = { Text(stringResource(R.string.feature_setting_printer_info_interface_connection_bluetooth_search_button_text)) },
        )
    }
}

@Composable
private fun EthernetFields(
    ethernetIpAddress: String,
    onIpAddressChange: (String) -> Unit,
    ipAddressErrorIndex: Int?,
    ethernetPort: String,
    onPortChange: (String) -> Unit,
    portErrorIndex: Int?,
) {
    PosOutlinedTextField(
        value = ethernetIpAddress,
        onValueChange = {
            onIpAddressChange(formatIPAddress(it))
        },
        label = stringResource(R.string.feature_setting_printer_info_interface_connection_ethernet_ip_address),
        keyboardOptions =
        KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
        isError = ipAddressErrorIndex != null,
        supportingText = ipAddressErrorIndex?.let { stringResource(it) },
        modifier = Modifier.fillMaxWidth(),
    )
    PosOutlinedTextField(
        value = ethernetPort,
        onValueChange = onPortChange,
        label = stringResource(R.string.feature_setting_printer_info_interface_connection_ethernet_port),
        keyboardOptions =
        KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done, // Use Done for the last field
            ),
        isError = portErrorIndex != null,
        supportingText = portErrorIndex?.let { stringResource(it) },
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview
@Composable
fun PrinterInfoScreenPreview() {
    POSTheme {
        PosBackground {
            PrinterInfoScreen(
                isEditMode = false,
                onNavigateBack = {},
                onSave = {},
                printerStatus = PrinterState.Connecting(12),
            )
        }
    }
}