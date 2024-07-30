package com.casecode.pos.feature.setting.printer

import android.Manifest
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
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
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosOutlinedButton
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.PrinterConnectionInfo
import com.casecode.pos.core.model.data.PrinterInfo
import com.casecode.pos.feature.setting.R
import com.cassecode.pos.core.printer.BluetoothPrinterConnection
import com.cassecode.pos.core.printer.PrintContent
import com.cassecode.pos.core.printer.PrinterConnection
import com.cassecode.pos.core.printer.TcpPrinterConnection
import com.cassecode.pos.core.printer.UsbPrinterConnection
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import timber.log.Timber
import com.casecode.pos.core.printer.R as resourcePrinter

@Composable
internal fun PrinterInfoRoute(
    printerVIewModel: PrinterVIewModel = hiltViewModel(),
    isUpdate: Boolean = false,
    onBackClick: () -> Unit,
) {
    PrinterInfoScreen(
        isUpdate = isUpdate,
        onBackClick = onBackClick,
        onSavedClick = {},
        onTestClick = printerVIewModel::testPrinter
    )
}

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
        macAddress: String,
        usbDeviceName: String,
        isCurrentSelected: Boolean,
        paperWidth: String,
        context: Context,
    ) -> Unit,
    onSavedClick: () -> Unit,
) {
    val context = LocalContext.current

    var printerName by remember { mutableStateOf("") }
    val printerEthernetDefault =
        stringResource(resourcePrinter.string.core_printer_info_connection_ethernet)
    val connectionTypeBluetooth =
        stringResource(resourcePrinter.string.core_printer_info_connection_bluetooth)
    val connectionTypeUsb = stringResource(resourcePrinter.string.core_printer_info_connection_usb)
    val paperWidthDefault = stringResource(resourcePrinter.string.core_printer_info_paper_width_72)
    var selectedTypeConnection by remember { mutableStateOf(printerEthernetDefault) }
    var selectedPaperWidth by remember { mutableStateOf(paperWidthDefault) }

    var ipAddress by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }
    var nameBluetooth by remember { mutableStateOf("") }
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

    val enableBluetoothIntent = remember { Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE) }
    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Timber.tag("BrowseBluetoothDeviceDialog").i(":request permission result ok")
        } else {
            Timber.tag("BrowseBluetoothDeviceDialog").i(":request permission result canceled / denied")
            showBrowseBluetoothDialog = false
        }
    }



    if(showBrowseBluetoothDialog){
        BrowseBluetoothDeviceDialog(onDismiss = {showBrowseBluetoothDialog = false}, onConfirm = {

            showBrowseBluetoothDialog = false
        })
    }
    Box(
        modifier = modifier
            .fillMaxSize(),
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
                        supportingText =
                        if (typeConnectionError) stringResource(R.string.feature_setting_printer_info_error_interface_connection_empty) else null,

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
                if (selectedTypeConnection == printerEthernetDefault) {
                    PosOutlinedTextField(
                        value = ipAddress,
                        onValueChange = { ipInput ->
                            ipAddress = formatIPAddress(ipInput)
                            ipAddressError = validateIPAddress(ipInput)
                        },
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
                        onValueChange = { portInput ->
                            port = portInput
                            portError = validatePort(portInput)
                        },
                        label = stringResource(R.string.feature_setting_printer_info_interface_connection_ethernet_port),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                        ),
                        isError = portError != null,
                        supportingText = portError?.let { stringResource(it) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                if (selectedTypeConnection == connectionTypeBluetooth) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        PosOutlinedTextField(
                            value = nameBluetooth,
                            onValueChange = { nameBluetooth = it },
                            label = stringResource(R.string.feature_setting_printer_info_interface_connection_bluetooth_name_label),
                            isError = nameBluetoothError,
                            supportingText = if (nameBluetoothError) stringResource(R.string.feature_setting_printer_info_error_bluetooth_name_empty) else null,
                            modifier = Modifier.weight(0.6f),
                        )
                        PosOutlinedButton(
                            onClick = {
                                    activityResultLauncher.launch(enableBluetoothIntent)
                                showBrowseBluetoothDialog = true


                                //TODO: open bluetooth dialog
                            },
                            modifier = Modifier
                                .weight(0.4f)
                                .padding(start = 8.dp)
                                .align(Alignment.CenterVertically),
                            text = { Text(stringResource(R.string.feature_setting_printer_info_interface_connection_bluetooth_search_button_text)) },
                        )
                    }
                }
                if (selectedTypeConnection == connectionTypeUsb) {
                    PosOutlinedTextField(
                        value = nameUsb,
                        onValueChange = { nameUsb = it },
                        label = stringResource(R.string.feature_setting_printer_info_interface_connection_usb_name_label),
                        isError = nameUsbError,
                        supportingText = if (nameUsbError) stringResource(R.string.feature_setting_printer_info_error_usb_name_empty) else null,
                        modifier = Modifier.fillMaxWidth(),
                    )
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
                        supportingText =
                        if (paperWidthError) stringResource(R.string.feature_setting_printer_info_error_papper_width_empty) else null,

                        label = stringResource(R.string.feature_setting_printer_info_papper_width_label),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paperWidthExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                    )
                    val connectionType =
                        stringArrayResource(resourcePrinter.array.core_printer_info_paper_width)

                    ExposedDropdownMenu(
                        expanded = paperWidthExpanded,
                        onDismissRequest = { paperWidthExpanded = false },
                    ) {
                        connectionType.forEach { type ->
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
                    nameBluetooth,
                    nameUsb,
                    false,
                    selectedPaperWidth,
                    context)


            },
            text = { Text(stringResource(R.string.feature_setting_printer_info_test_button_text)) },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
        )
    }
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
                onTestClick = {_, _, _, _, _, _, _, _, _ -> }
            )
        }
    }
}