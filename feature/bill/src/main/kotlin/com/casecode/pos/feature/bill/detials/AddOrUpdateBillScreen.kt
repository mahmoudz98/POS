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

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CurrencyPound
import androidx.compose.material.icons.rounded.Percent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
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
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosDatePickerDialog
import com.casecode.pos.core.designsystem.component.PosElevatedCard
import com.casecode.pos.core.designsystem.component.PosFilledTextField
import com.casecode.pos.core.designsystem.component.PosInputChip
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.component.PosTonalButton
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.DiscountType
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.ui.DevicePreviews
import com.casecode.pos.core.ui.TrackScreenViewEvent
import com.casecode.pos.core.ui.utils.toFormattedString
import com.casecode.pos.feature.bill.R
import com.casecode.pos.feature.bill.SearchSupplierUiState
import kotlinx.datetime.Clock
import com.casecode.pos.core.ui.R.string as uiString

@Composable
fun AddOrUpdateBillScreen(
    viewModel: BillCreationViewModel = hiltViewModel(),
    isUpdate: Boolean = false,
    onBackClick: () -> Unit,
    onAddBillItem: () -> Unit,
    onUpdateBillItem: () -> Unit,
) {
    val billInputState by viewModel.billInputState.collectAsStateWithLifecycle()
    val filterSupplierState by viewModel.filterSupplierState.collectAsStateWithLifecycle()
    val searchSupplier by viewModel.searchQuerySupplier.collectAsStateWithLifecycle()
    AddOrUpdateBillScreen(
        isUpdate = isUpdate,
        billInputState = billInputState,
        searchSupplier = searchSupplier,
        filterSupplierState = filterSupplierState,
        onNavigateBack = onBackClick,
        onSearchSupplierChange = viewModel::onSearchQuerySupplierChanged,
        onAddBillItem = onAddBillItem,
        onUpdateBillItem = {
            viewModel.onSelectedItem(it)
            onUpdateBillItem()
        },
        onRemoveBillItem = {
            viewModel.removeItem(it)
        },
        onAddBill = { viewModel.updateStockThenAddBill() },
        onUpdateBill = { },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrUpdateBillScreen(
    modifier: Modifier = Modifier,
    isUpdate: Boolean,
    billInputState: BillInputState,
    searchSupplier: String,
    filterSupplierState: SearchSupplierUiState,
    onSearchSupplierChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onAddBillItem: () -> Unit,
    onUpdateBillItem: (Item) -> Unit,
    onRemoveBillItem: (Item) -> Unit,
    onAddBill: () -> Unit,
    onUpdateBill: () -> Unit,
) {
    TrackScreenViewEvent(screenName = "AddOrUpdateBill")
    val snackbarHostState = remember { SnackbarHostState() }
    var showIssueDateDialog by rememberSaveable { mutableStateOf(false) }
    var showDueDateDialog by rememberSaveable { mutableStateOf(false) }
    val onSaveTriggered = fun() {
        if (billInputState.supplierName.isEmpty() || billInputState.billNumber.isEmpty()) {
            billInputState.supplierNameError = billInputState.supplierName.isEmpty()
            billInputState.billNumberError = billInputState.billNumber.isEmpty()
            return
        }
        if (isUpdate) {
            onUpdateBill()
        } else {
            onAddBill()
        }
        onNavigateBack()
    }
    Scaffold(
        topBar = {
            PosTopAppBar(
                titleRes = if (isUpdate) {
                    R.string.feature_bill_update_bill_title_text
                } else {
                    R.string.feature_bill_add_bill_title_text
                },
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
                        SupplierExposeDropdownMenuBox(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .padding(horizontal = 8.dp),
                            searchSupplierText = searchSupplier,
                            currentSupplier = billInputState.supplierName,
                            filterSupplierState = filterSupplierState,
                            onSearchSupplierChange = onSearchSupplierChange,
                            onClickSupplier = billInputState::onSupplierNameChange,
                            clearSupplierSelection = {
                                onSearchSupplierChange("")
                                billInputState.onSupplierNameChange("")
                            },
                            label = stringResource(R.string.feature_bill_supplier_hint),
                            isError = billInputState.supplierNameError,

                        )
                        PosOutlinedTextField(
                            value = billInputState.billNumber,
                            onValueChange = billInputState::onBillNumberChange,
                            isError = billInputState.billNumberError,
                            label = stringResource(R.string.feature_bill_number_hint),
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
                            Modifier.padding(horizontal = 8.dp)
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
                                    Text(stringResource(R.string.feature_bill_items_amount_text))
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

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun BillItemsTotalSection(
    subTotal: Double,
    discount: String,
    discountTypeCurrency: Boolean,
    total: Double,
    onDiscountChange: (String) -> Unit,
    onDiscountTypeChange: () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
    ) {
        val quarterWidth = maxWidth / 4
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "SubTotal",
                    modifier = Modifier.padding(start = quarterWidth),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = subTotal.toFormattedString(),

                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                PosInputChip(
                    selected = discountTypeCurrency,
                    onSelectedChange = { onDiscountTypeChange() },
                    modifier = Modifier
                        .padding(start = quarterWidth)
                        .align(Alignment.Bottom),
                    selectedIcon = Icons.Rounded.CurrencyPound,
                    unSelectedIcon = Icons.Rounded.Percent,
                ) {
                    if (discountTypeCurrency) {
                        Text(stringResource(R.string.feature_bill_currency_discount_text))
                    } else {
                        Text(stringResource(R.string.feature_bill_percentage_discount_text))
                    }
                }
                PosFilledTextField(
                    value = discount,
                    onValueChange = {
                        onDiscountChange(it)
                    },
                    keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(0.4f),
                )
            }
            HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Total",
                    modifier = Modifier.padding(start = quarterWidth),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = total.toFormattedString(),
                )
            }
        }
    }
}

@Composable
fun BillLineItem(
    modifier: Modifier = Modifier,
    name: String,
    quantity: Int,
    costPrice: Double,
    sku: String,
    onRemoveItem: () -> Unit,
    onClickItem: () -> Unit,
) {
    ListItem(
        leadingContent = {
            IconButton(onClick = onRemoveItem) {
                Icon(
                    imageVector = PosIcons.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        },
        headlineContent = {
            Text(name)
        },
        trailingContent = {
            Text(quantity.times(costPrice).toBigDecimal().toString())
        },
        supportingContent = {
            Column {
                Text("$quantity x ${costPrice.toBigDecimal()}")
                Text("SKU: $sku")
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = modifier.clickable(onClick = onClickItem),
    )
}

@DevicePreviews
@Composable
fun AddOrUpdateBillScreenPreview() {
    val billInputState = BillInputState()
    POSTheme {
        AddOrUpdateBillScreen(
            isUpdate = false,
            billInputState = billInputState,
            searchSupplier = "",
            filterSupplierState = SearchSupplierUiState.EmptyQuery,
            onSearchSupplierChange = {},
            onNavigateBack = {},
            onAddBillItem = {},
            onUpdateBillItem = {},
            onRemoveBillItem = {},
            onAddBill = {},
            onUpdateBill = {},
        )
    }
}

@Preview
@Composable
fun ItemBillListItemPreview() {
    POSTheme {
        PosBackground {
            BillLineItem(
                name = "item",
                quantity = 12,
                costPrice = 22.1,
                sku = "12312312312",
                onRemoveItem = {},
                onClickItem = {},
            )
        }
    }
}