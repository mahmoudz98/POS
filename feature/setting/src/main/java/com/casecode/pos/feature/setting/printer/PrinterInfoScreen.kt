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
package com.casecode.pos.feature.setting.printer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.PendingIntent
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PermissionDialog
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosOutlinedButton
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.PrinterConnectionType
import com.casecode.pos.core.model.data.toConnectionType
import com.casecode.pos.core.printer.base.UsbEscPosPrint.Companion.ACTION_USB_PERMISSION
import com.casecode.pos.core.printer.model.PrinterState
import com.casecode.pos.feature.setting.R
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import timber.log.Timber
import com.casecode.pos.core.printer.R as resourcePrinter

@Composable
internal fun PrinterInfoScreen(
    printerViewModel: PrinterViewModel = hiltViewModel(),
    isEditMode: Boolean = false,
    onNavigateBack: () -> Unit,
) {
    val printerStatus by printerViewModel.printerState.collectAsStateWithLifecycle()
    PrinterInfoScreen(
        isEditMode = isEditMode,
        onNavigateBack = onNavigateBack,
        onAddPrinterInfoUsb = printerViewModel::addPrinterInfoUsb,
        onAddPrinterInfoEthernet = printerViewModel::addPrinterInfoEthernet,
        onAddPrinterInfoBluetooth = printerViewModel::addPrinterInfoBluetooth,

        onTestEthernetPrinter = printerViewModel::testPrinterEthernet,
        onTestBluetoothPrinter = printerViewModel::testPrinterBluetooth,
        onTestUsbPrinter = printerViewModel::testPrinterUsb,
        printerStatus = printerStatus,
    )
    val addPrinterResult by printerViewModel.addPrinterResult.collectAsStateWithLifecycle()
    when (addPrinterResult) {
        is Resource.Error -> {
        }

        is Resource.Empty -> {
            Timber.e("Empty")
        }

        Resource.Loading -> {
            Timber.e("Loading")
        }

        is Resource.Success -> {
            onNavigateBack()
        }

        null -> {}
    }
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterInfoScreen(
    rootModifier: Modifier = Modifier,
    isEditMode: Boolean,
    onNavigateBack: () -> Unit,
    onAddPrinterInfoBluetooth: (namePrinter: String, nameDevice: String, address: String, paperWidth: String) -> Unit = { _, _, _, _ -> },
    onAddPrinterInfoUsb: (namePrinter: String, nameDevice: String, paperWidth: String) -> Unit = { _, _, _ -> },
    onAddPrinterInfoEthernet: (namePrinter: String, ipAddress: String, port: String, paperWidth: String) -> Unit = { _, _, _, _ -> },
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

    var showPrinterStatusDialog by remember { mutableStateOf(false) }
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

    if (showPrinterStatusDialog) {
        StatePrinterDialog(
            printerState = printerStatus,
            onDismiss = { showPrinterStatusDialog = false },
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
                PosTextButton(
                    onClick = {
                        when (selectedConnectionType.toConnectionType()) {
                            PrinterConnectionType.BLUETOOTH -> {
                                if (!hasValidBluetoothInput()) {
                                    onAddPrinterInfoBluetooth(
                                        printerDisplayName,
                                        selectedBluetoothConnection!!.device.name,
                                        selectedBluetoothConnection!!.device.address,
                                        selectedPaperWidthValue.replace(" mm", ""),
                                    )
                                }
                            }

                            PrinterConnectionType.USB -> {
                                onAddPrinterInfoUsb(
                                    printerDisplayName,
                                    usbDeviceName,
                                    selectedPaperWidthValue.replace(" mm", ""),
                                )

                                //  onTestUsbPrinter(printerName, usbConnection!!, selectedPaperWidth.replace(" mm", ""), context)
                            }

                            else -> {
                                if (!hasValidEthernetInput()) {
                                    onAddPrinterInfoEthernet(
                                        printerDisplayName,
                                        ethernetIpAddress,
                                        ethernetPort,
                                        selectedPaperWidthValue.replace(" mm", ""),
                                    )
                                }
                            }
                        }
                    },
                ) {
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
                    BluetoothContent(
                        selectedBluetoothConnection,
                        isBluetoothDeviceInvalid,
                        onSelectPrinterDevice = {
                            selectedBluetoothConnection = it
                        },
                    )
                }

                PrinterConnectionType.USB -> {
                    UsbContent(applicationContext, isUsbDeviceInvalid)
                }

                else -> {
                    EthernetContent(
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
                                showPrinterStatusDialog = true
                            }
                        }

                        PrinterConnectionType.USB -> {
                            showPrinterStatusDialog = true

                            //  onTestUsbPrinter(printerName, usbConnection!!, selectedPaperWidth.replace(" mm", ""), context)
                        }

                        else -> {
                            if (!hasValidEthernetInput()) {
                                onTestEthernetPrinter(
                                    printerDisplayName,
                                    ethernetIpAddress,
                                    ethernetPort,
                                    selectedPaperWidthValue.replace(" mm", ""),
                                    applicationContext,
                                )
                                showPrinterStatusDialog = true
                            }
                        }
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun UsbContent(
    applicationContext: Context,
    isUsbDeviceInvalid: Boolean,
) {
    var usbDeviceName by remember { mutableStateOf("") }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    remember {
        UsbPrintersConnections.selectFirstConnected(context)
    }
    context.getSystemService(Context.USB_SERVICE) as UsbManager?
    val permissionUsb = rememberPermissionState("${context.packageName}.USB_PERMISSION")
    LaunchedEffect(permissionUsb.status) {
        if (!permissionUsb.status.isGranted) {
            permissionUsb.launchPermissionRequest()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val usbPermissionReceiver =
            object : BroadcastReceiver() {
                override fun onReceive(
                    applicationContext: Context,
                    intent: Intent,
                ) {
                    Timber.e("onReceive: ${intent.action}")
                    if (ACTION_USB_PERMISSION == intent.action) {
                        Timber.e("ACTION_USB_PERMISSION")
                        synchronized(this) {
                            val usbDevice: UsbDevice? =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    intent.getParcelableExtra(
                                        UsbManager.EXTRA_DEVICE,
                                        UsbDevice::class.java,
                                    )
                                } else {
                                    @Suppress("DEPRECATION")
                                    intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                                }
                            Timber.e("usbDevice: $usbDevice")
                            Timber.e("usbDeviceName: ${usbDevice?.deviceName}")
                            if (intent.getBooleanExtra(
                                    UsbManager.EXTRA_PERMISSION_GRANTED,
                                    false,
                                )
                            ) {
                                usbDeviceName = usbDevice?.deviceName.toString()
                            }
                        }
                    }
                }
            }

        val intentFilter = IntentFilter(ACTION_USB_PERMISSION)
        context.registerReceiver(
            usbPermissionReceiver,
            intentFilter,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Context.RECEIVER_EXPORTED else 0,
        )

        /*  ContextCompat.registerReceiver(
              context,
              usbPermissionReceiver,
              IntentFilter(ACTION_USB_PERMISSION),
              ContextCompat.RECEIVER_EXPORTED,
          )*/
        onDispose {
            context.unregisterReceiver(usbPermissionReceiver)
        }
    }

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

private fun requestUsbPermission(context: Context, usbManager: UsbManager?, usbDevice: UsbDevice?) {
    if (usbManager != null && usbDevice != null) {
        val permissionIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent("${context.packageName}.USB_PERMISSION"),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0,
        )
        usbManager.requestPermission(usbDevice, permissionIntent)
    }
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun BluetoothContent(
    selectedDevice: BluetoothConnection?,
    isBluetoothNameError: Boolean,
    onSelectPrinterDevice: (BluetoothConnection) -> Unit,
) {
    var showBluetoothDevicesDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    val bluetoothPermissions = rememberBluetoothPermissions()

    val enableBluetoothIntent = remember { Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE) }
    val launchEnableBluetoothRequest =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            showBluetoothDevicesDialog = result.resultCode == RESULT_OK
        }

    if (showBluetoothDevicesDialog && bluetoothPermissions.allPermissionsGranted) {
        BrowseBluetoothDeviceDialog(
            onDismiss = { showBluetoothDevicesDialog = false },
            onSelectPrinterDevice = {
                onSelectPrinterDevice(it)
                showBluetoothDevicesDialog = false
            },
        )
    }
    if (showPermissionDialog) {
        PermissionDialog(
            onDismiss = { showPermissionDialog = false },
            messagePermission = R.string.feature_setting_need_permission_for_bluetooth,
        )
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
                    launchEnableBluetoothRequest.launch(enableBluetoothIntent)
                } else {
                    val hasDenied: Boolean = bluetoothPermissions.permissions.map {
                        it.status.shouldShowRationale
                    }.any { it }
                    if (hasDenied) {
                        showPermissionDialog = true
                    } else {
                        bluetoothPermissions.launchMultiplePermissionRequest()
                    }
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
@OptIn(ExperimentalPermissionsApi::class)
private fun rememberBluetoothPermissions(): MultiplePermissionsState {
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
    return rememberMultiplePermissionsState(permissions)
}

@Composable
private fun EthernetContent(
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
                printerStatus = PrinterState.Connecting(12),
            )
        }
    }
}