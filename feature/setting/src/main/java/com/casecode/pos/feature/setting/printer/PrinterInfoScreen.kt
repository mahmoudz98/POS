package com.casecode.pos.feature.setting.printer

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.casecode.pos.core.designsystem.component.PosButton
import com.casecode.pos.core.designsystem.component.PosOutlinedButton
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.feature.setting.R
import kotlin.text.append
import kotlin.text.forEachIndexed
import kotlin.text.isDigit

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
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterInfoScreen(
    modifier: Modifier = Modifier,
    isUpdate: Boolean,
    onBackClick: () -> Unit,
    onSavedClick: () -> Unit,
) {
    var printerName by remember { mutableStateOf("") }
    val printerEthernetDefault =
        stringResource(R.string.feature_setting_printer_info_connection_ethernet)
    val paperWidthDefault = stringResource(R.string.feature_setting_printer_info_paper_width_72)
    var selectedTypeConnection by remember { mutableStateOf(printerEthernetDefault) }
    var selectedPaperWidth by remember { mutableStateOf(paperWidthDefault) }

    var ipAddress by remember { mutableStateOf("") }
    var nameBluetooth by remember { mutableStateOf("") }
    var nameUsb by remember { mutableStateOf("") }

    var typeConnectionExpanded by remember { mutableStateOf(false) }
    var paperWidthExpanded by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf(false) }
    val typeConnectionError by remember { mutableStateOf(false) }
    var ipAddressError by remember { mutableStateOf(false) }
    var nameBluetoothError by remember { mutableStateOf(false) }
    var nameUsbError by remember { mutableStateOf(false) }
    val paperWidthError by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column {
            PosTopAppBar(
                modifier = Modifier,
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
                    stringArrayResource(R.array.feature_setting_printer_info_connection)

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
                    onValueChange = {newValue ->
//                        if(isValidIpAddress(newValue)){
//
//                            ipAddress = newValue.filter { it.isDigit() }.take(12)
//                        }
                       val cleanInput = newValue.filter { it.isDigit() || it == '.' }
                        val segments = cleanInput.split(".").filter { it.isNotEmpty() }

                        if (segments.size <= 4) {
                            val updatedSegments = segments.map { segment ->
                                segment.take(3).filter { it.isDigit() }
                            }.joinToString(".")

                            val isValid = updatedSegments.split(".").all { segment ->
                                segment.toIntOrNull()?.let { num -> num in 0..255 } ?: false
                            }
                            if(isValid){
                                ipAddress = updatedSegments

                            }
                            //ipAddressError = if (isValid || updatedSegments.isEmpty()) false else true
                                //"Each segment must be between 0 and 255"
                        } else {
                          //  ipAddressError = "Too many segments"
                        }
                         },
                    label = stringResource(R.string.feature_setting_printer_info_interface_connection_ethernet_ip_address),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                    visualTransformation = IpAddressTransformation(),
                    isError = ipAddressError,
                    supportingText = if (ipAddressError) stringResource(R.string.feature_setting_printer_info_error_ethernet_ip_address_empty) else null,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            val connectionTypeBluetooth =
                stringResource(R.string.feature_setting_printer_info_connection_bluetooth)
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
            if (selectedTypeConnection == stringResource(R.string.feature_setting_printer_info_connection_usb)) {
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
                    stringArrayResource(R.array.feature_setting_printer_info_paper_width)

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

        PosButton(
            onClick = {},
            text = { Text(stringResource(R.string.feature_setting_printer_info_test_button_text)) },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
        )
    }
}