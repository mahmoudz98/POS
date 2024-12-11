package com.casecode.pos.feature.bill.creation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.recalculateWindowInsets
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.casecode.pos.core.designsystem.component.PosDatePickerDialog
import com.casecode.pos.core.designsystem.component.PosElevatedCard
import com.casecode.pos.core.designsystem.component.PosEmptyScreen
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.component.PosTonalButton
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.DiscountType
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.ui.TrackScreenViewEvent
import com.casecode.pos.feature.bill.R
import com.casecode.pos.feature.bill.detials.BillDetailUiState
import kotlinx.datetime.Clock
import com.casecode.pos.core.ui.R.string as uiString

@Composable
fun UpdateBillScreen(
    viewModel: BillCreationViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onAddBillItem: () -> Unit,
    onUpdateBillItem: () -> Unit,
) {
    val billInputState by viewModel.billInputState.collectAsStateWithLifecycle()
    val billDetailUiState by viewModel.billDetailsUiState.collectAsStateWithLifecycle()
    UpdateBillScreen(
        billInputState = billInputState,
        billDetailUiState = billDetailUiState,
        onNavigateBack = onBackClick,
        onAddBillItem = onAddBillItem,
        onUpdateBillItem = {
            viewModel.onSelectedItem(it)
            onUpdateBillItem()
        },
        onRemoveBillItem = { viewModel.removeItem(it) },
        onUpdateBill = { viewModel.updateStockThenAddBill() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateBillScreen(
    modifier: Modifier = Modifier,
    billInputState: BillInputState,
    billDetailUiState: BillDetailUiState,
    onNavigateBack: () -> Unit,
    onAddBillItem: () -> Unit,
    onUpdateBillItem: (Item) -> Unit,
    onRemoveBillItem: (Item) -> Unit,
    onUpdateBill: () -> Unit,
) {
    TrackScreenViewEvent(screenName = "updateBill")
    val snackbarHostState = remember { SnackbarHostState() }
    var showIssueDateDialog by rememberSaveable { mutableStateOf(false) }
    var showDueDateDialog by rememberSaveable { mutableStateOf(false) }
    val onSaveTriggered = fun() {
        if (billInputState.supplierName.isEmpty() || billInputState.billNumber.isEmpty()) {
            billInputState.supplierNameError = billInputState.supplierName.isEmpty()
            billInputState.billNumberError = billInputState.billNumber.isEmpty()
            return
        }
        onUpdateBill()
        onNavigateBack()
    }
    Scaffold(
        topBar = {
            PosTopAppBar(
                titleRes = R.string.feature_bill_update_bill_title_text,
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier
                .fillMaxSize()
                .padding(innerPadding)
                .recalculateWindowInsets(),
        ) {
            when (billDetailUiState) {
                is BillDetailUiState.Error, is BillDetailUiState.EmptySelection -> {
                    PosEmptyScreen(
                        Modifier.align(Alignment.Center),
                        icon = PosIcons.Payment,
                        titleRes = R.string.feature_bill_selection_empty_title,
                        messageRes = R.string.feature_bill_selection_empty_title,
                    )
                }

                is BillDetailUiState.Loading -> {
                    PosLoadingWheel(
                        contentDesc = "UpdateBillLoading",
                        Modifier.align(Alignment.Center),
                    )
                }

                is BillDetailUiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(280.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding()
                            .imePadding(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),

                    ) {
                        item {
                            PosElevatedCard {
                                PosOutlinedTextField(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(top = 8.dp)
                                        .padding(horizontal = 8.dp),
                                    value = billInputState.supplierName,
                                    onValueChange = {},
                                    label = stringResource(R.string.feature_bill_supplier_hint),
                                    readOnly = true,
                                )

                                PosOutlinedTextField(
                                    value = billInputState.billNumber,
                                    onValueChange = billInputState::onBillNumberChange,
                                    isError = billInputState.billNumberError,
                                    label = stringResource(R.string.feature_bill_number_hint),
                                    readOnly = true,
                                    keyboardOptions =
                                    KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Next,
                                    ),
                                    supportingText = if (billInputState.billNumberError) {
                                        stringResource(
                                            R.string.feature_bill_number_empty,
                                        )
                                    } else {
                                        null
                                    },
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                )

                                PosOutlinedTextField(
                                    value = billInputState.issueDate.toFormattedDateString(),
                                    onValueChange = {},
                                    label = stringResource(R.string.feature_bill_issue_date_hint),
                                    readOnly = true,
                                    trailingIcon = {
                                        Icon(
                                            imageVector = PosIcons.Calender,
                                            modifier = Modifier.clickable(
                                                onClick = {
                                                    showIssueDateDialog = true
                                                },
                                            ),
                                            contentDescription = null,
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                )
                                PosOutlinedTextField(
                                    value = billInputState.dueDate.toFormattedDateString(),
                                    onValueChange = {},
                                    label = stringResource(R.string.feature_bill_due_date_hint),
                                    readOnly = true,
                                    trailingIcon = {
                                        Icon(
                                            imageVector = PosIcons.Calender,
                                            modifier = Modifier.clickable(
                                                onClick = {
                                                    showDueDateDialog = true
                                                },
                                            ),
                                            contentDescription = null,
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                )
                            }
                        }

                        item {
                            PosElevatedCard {
                                Column(
                                    Modifier
                                        .padding(horizontal = 8.dp)
                                        .padding(bottom = 8.dp),
                                ) {
                                    PosTonalButton(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        onClick = { onAddBillItem() },
                                        text = {
                                            Text(
                                                text = stringResource(
                                                    R.string.feature_bill_add_bill_item_title_text,
                                                ),
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = PosIcons.Add,
                                                contentDescription = null,
                                            )
                                        },
                                    )
                                    if (billInputState.invoiceItems.isNotEmpty()) {
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            Text(stringResource(R.string.feature_bill_items_text))
                                            Text(
                                                stringResource(
                                                    R.string.feature_bill_items_amount_text,
                                                ),
                                            )
                                        }
                                        HorizontalDivider()
                                        billInputState.invoiceItems.forEach {
                                            BillLineItem(
                                                modifier = Modifier.animateItem(),
                                                name = it.name,
                                                quantity = it.quantity,
                                                costPrice = it.costPrice,
                                                sku = it.sku,
                                                onRemoveItem = { onRemoveBillItem(it) },
                                                onClickItem = { onUpdateBillItem(it) },
                                            )
                                        }
                                        val isFixed =
                                            billInputState.discountTypeCurrency == DiscountType.FIXED
                                        BillItemsTotalSection(
                                            subTotal = billInputState.subTotal,
                                            discount = billInputState.discount,
                                            discountTypeCurrency = isFixed,
                                            total = billInputState.total,
                                            onDiscountChange = billInputState::onDiscountChange,
                                            onDiscountTypeChange = billInputState::onDiscountTypeChange,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (showIssueDateDialog) {
        PosDatePickerDialog(
            currentSelectedDate = billInputState.issueDate.toEpochMilliseconds(),
            confirmTextRes = uiString.core_ui_dialog_ok_button_text,
            cancelTextRes = uiString.core_ui_dialog_cancel_button_text,
            onDataSelected = { selectedMillis ->
                billInputState.onIssueDateChange(
                    selectedMillis ?: Clock.System.now().toEpochMilliseconds(),
                )
            },
            onDismiss = { showIssueDateDialog = false },
        )
    }
    if (showDueDateDialog) {
        PosDatePickerDialog(
            currentSelectedDate = billInputState.dueDate.toEpochMilliseconds(),
            confirmTextRes = uiString.core_ui_dialog_ok_button_text,
            cancelTextRes = uiString.core_ui_dialog_cancel_button_text,
            isSelectableDate = { utcTimeMillis ->
                val issueDateStart = millisToStartOfDay(billInputState.issueDate)
                val selectedDateStart = millisToStartOfDay(utcTimeMillis)
                selectedDateStart >= issueDateStart
            },
            onDataSelected = {
                billInputState.onDueDateChange(it ?: Clock.System.now().toEpochMilliseconds())
            },
            onDismiss = { showDueDateDialog = false },
        )
    }
}

@Preview
@Composable
fun UpdateBillScreenPreview() {
    POSTheme {
        UpdateBillScreen(
            billInputState = BillInputState(),
            billDetailUiState = BillDetailUiState.Success(SupplierInvoice()),
            onNavigateBack = {},
            onAddBillItem = {},
            onUpdateBillItem = {},
            onRemoveBillItem = {},
            onUpdateBill = {},
        )
    }
}