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
package com.casecode.pos.feature.bill.detials

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.data.utils.toFormattedDateString
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosDatePickerDialog
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.PaymentDetails
import com.casecode.pos.core.model.data.users.PaymentMethod
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.ui.utils.toBigDecimalFormatted
import com.casecode.pos.feature.bill.R
import com.casecode.pos.feature.bill.toPaymentMethodRes
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import com.casecode.pos.core.ui.R.string as uiString

@Composable
internal fun AddBillPaymentScreen(
    viewModel: BillDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.billDetailsUiState.collectAsStateWithLifecycle(
        BillDetailUiState.Loading,
    )
    AddBillPaymentScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onAddPaymentDetails = { viewModel.addPaymentDetails(it) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddBillPaymentScreen(
    uiState: BillDetailUiState,
    onNavigateBack: () -> Unit,
    onAddPaymentDetails: (PaymentDetails) -> Unit,
) {
    val restDue =
        (uiState as? BillDetailUiState.Success)?.supplierInvoice?.totalAmount ?: 0.0.minus(
            (uiState as? BillDetailUiState.Success)?.supplierInvoice?.paymentDetails?.sumOf {
                it.amountPaid
            } ?: 0.0,
        )
    var amountPaid by remember { mutableStateOf(restDue.toBigDecimalFormatted()) }
    var date by remember { mutableStateOf(Clock.System.now()) }
    var paymentModeSelected by remember { mutableStateOf(PaymentMethod.CASH) }
    var showDateDialog by remember { mutableStateOf(false) }

    val onSaveTriggered = fun() {
        if (amountPaid.isEmpty()) return
        onAddPaymentDetails(
            PaymentDetails(
                amountPaid = amountPaid.toDouble(),
                paymentDate = date,
                paymentMethod = paymentModeSelected,
            ),
        )
        onNavigateBack()
    }
    Scaffold(
        topBar = {
            PosTopAppBar(
                titleRes = R.string.feature_bill_add_payment_title,
                navigationIcon = PosIcons.ArrowBack,
                navigationIconContentDescription = null,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                ),
                action = {
                    PosTextButton(
                        onClick = { onSaveTriggered() },
                    ) { Text(text = stringResource(uiString.core_ui_save_action_text)) }
                },
                onNavigationClick = onNavigateBack,
            )
        },
    ) { innerPadding ->
        when (uiState) {
            is BillDetailUiState.Success -> {
                Column(Modifier.padding(innerPadding).padding(16.dp)) {
                    PosOutlinedTextField(
                        value = uiState.supplierInvoice.supplierName,
                        onValueChange = {},
                        label = stringResource(R.string.feature_bill_payment_supplier_hint),
                        readOnly = true,
                        modifier =
                        Modifier
                            .fillMaxWidth(),
                    )
                    PosOutlinedTextField(
                        value = amountPaid,
                        onValueChange = { amountPaid = it },
                        label = stringResource(R.string.feature_bill_payment_made_text),
                        keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    PosOutlinedTextField(
                        value = date.toFormattedDateString(),
                        onValueChange = {},
                        label = stringResource(R.string.feature_bill_payment_date_hint),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = PosIcons.Calender,
                                modifier = Modifier.clickable(
                                    onClick = {
                                        showDateDialog = true
                                    },
                                ),
                                contentDescription = null,
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                    var paymentModeExpended by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = paymentModeExpended,
                        onExpandedChange = { paymentModeExpended = !paymentModeExpended },
                    ) {
                        PosOutlinedTextField(
                            value = stringResource(toPaymentMethodRes(paymentModeSelected)),
                            onValueChange = {},
                            readOnly = true,
                            label = stringResource(R.string.feature_bill_payment_method_hint),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = paymentModeExpended,
                                )
                            },
                            modifier =
                            Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                        )
                        ExposedDropdownMenu(
                            expanded = paymentModeExpended,
                            onDismissRequest = { paymentModeExpended = false },
                        ) {
                            PaymentMethod.entries.forEach { payment ->
                                DropdownMenuItem(
                                    text = { Text(stringResource(toPaymentMethodRes(payment))) },
                                    onClick = {
                                        paymentModeSelected = payment
                                        paymentModeExpended = false
                                    },
                                )
                            }
                        }
                    }
                }
            }

            is BillDetailUiState.Error -> {}
            is BillDetailUiState.Loading -> {}
            is BillDetailUiState.EmptySelection -> {}
        }
    }
    if (showDateDialog) {
        PosDatePickerDialog(
            currentSelectedDate = date.toEpochMilliseconds(),
            confirmTextRes = uiString.core_ui_dialog_ok_button_text,
            cancelTextRes = uiString.core_ui_dialog_cancel_button_text,
            onDataSelected = { selectedMillis ->
                date = Instant.fromEpochMilliseconds(
                    selectedMillis ?: Clock.System.now().toEpochMilliseconds(),
                )
            },
            onDismiss = { showDateDialog = false },
        )
    }
}

@Preview
@Composable
fun AddBillPaymentScreenPreview() {
    val supplierInvoice = SupplierInvoice(
        supplierName = "Sample Supplier",
        totalAmount = 100.0,
    )
    POSTheme {
        PosBackground {
            AddBillPaymentScreen(
                uiState = BillDetailUiState.Success(supplierInvoice),
                onNavigateBack = {},
                onAddPaymentDetails = {},
            )
        }
    }
}