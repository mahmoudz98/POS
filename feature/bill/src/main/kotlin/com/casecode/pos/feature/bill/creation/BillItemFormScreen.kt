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
package com.casecode.pos.feature.bill.creation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.model.utils.toFormattedString
import com.casecode.pos.core.ui.TrackScreenViewEvent
import com.casecode.pos.core.ui.utils.MAX_CURRENCY_LENGTH_SIZE
import com.casecode.pos.feature.bill.R
import timber.log.Timber
import com.casecode.pos.core.ui.R.string as uiString

@Composable
fun BillItemFormScreen(
    isUpdate: Boolean = false,
    itemUpdated: Item?,
    searchItemQuery: String,
    filterItemState: SearchItemUiState,
    onSearchItemChange: (String) -> Unit,
    onAddBillItem: (Item) -> Unit = {},
    onUpdateBillItem: (Item) -> Unit = {},
    onBackClick: () -> Unit,
) {
    TrackScreenViewEvent(screenName = "BillItem")

    BillItemFormScreen(
        isUpdate = isUpdate,
        itemUpdated = itemUpdated,
        searchItemQuery = searchItemQuery,
        filterItemState = filterItemState,
        onNavigateBack = onBackClick,
        onSearchItemChange = onSearchItemChange,
        onAddBillItem = onAddBillItem,
        onUpdateBillItem = onUpdateBillItem,
    )
    BackHandler(onBack = onBackClick)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BillItemFormScreen(
    modifier: Modifier = Modifier,
    isUpdate: Boolean,
    itemUpdated: Item?,
    searchItemQuery: String,
    filterItemState: SearchItemUiState,
    onNavigateBack: () -> Unit,
    onSearchItemChange: (String) -> Unit,
    onAddBillItem: (Item) -> Unit,
    onUpdateBillItem: (Item) -> Unit,
) {
    TrackScreenViewEvent(screenName = "AddOrUpdateBillItem")

    var name by rememberSaveable { mutableStateOf(if (isUpdate) itemUpdated?.name ?: "" else "") }
    var sku by rememberSaveable { mutableStateOf(if (isUpdate) itemUpdated?.sku ?: "" else "") }
    var costPrice by rememberSaveable {
        mutableStateOf(
            if (isUpdate) itemUpdated?.costPrice?.toString() ?: "" else "",
        )
    }
    var quantity by rememberSaveable {
        mutableStateOf(
            if (isUpdate) itemUpdated?.quantity?.toString() ?: "" else "",
        )
    }
    var nameError by remember { mutableStateOf(false) }
    var quantityError by remember { mutableStateOf(false) }
    var costPriceError by remember { mutableStateOf<Int?>(null) }
    val onItemTriggered =
        { nameItem: String, skuItem: String, costItem: String, quantityItem: String ->
            name = nameItem
            sku = skuItem
            costPrice = costItem
            quantity = quantityItem
        }
    val onSaveTriggered = onSaveTriggered@{
        if (name.isBlank() || costPrice.isBlank() || quantity.isBlank()) {
            nameError = name.isBlank()
            quantityError = quantity.isBlank()
            costPriceError =
                if (costPrice.isBlank()) R.string.feature_bill_item_error_price_empty else null
            return@onSaveTriggered
        }
        val item = Item(
            name = name,
            sku = sku.toString(),
            quantity = quantity.toInt(),
            costPrice = costPrice.toDouble(),
        )
        if (isUpdate) {
            onUpdateBillItem(item)
        } else {
            onAddBillItem(item)
        }
        onNavigateBack()
    }
    Scaffold(
        topBar = {
            PosTopAppBar(
                titleRes = if (isUpdate) {
                    R.string.feature_bill_update_bill_item_title_text
                } else {
                    R.string.feature_bill_add_bill_item_title_text
                },
                navigationIcon = PosIcons.ArrowBack,
                navigationIconContentDescription = null,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),

                action = {
                    PosTextButton(
                        onClick = { onSaveTriggered() },
                    ) { Text(text = stringResource(uiString.core_ui_save_action_text)) }
                },
                onNavigationClick = onNavigateBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            ItemExposeDropdownMenuBox(
                searchItemText = searchItemQuery,
                currentNameItem = name,
                filterItemUiState = filterItemState,
                onSearchItemChange = onSearchItemChange,
                onItemClick = {
                    onItemTriggered(it.name, it.sku, it.costPrice.toFormattedString(), "1")
                    onSearchItemChange("")
                    nameError = false
                },
                clearItemSelection = {
                    onItemTriggered("", "", "", "")
                },
                label = stringResource(R.string.feature_bill_add_item_hint),
                isError = nameError,
            )
            PosOutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = stringResource(R.string.feature_bill_quantity_hint),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                isError = quantityError,
                supportingText =
                if (quantityError) {
                    stringResource(R.string.feature_bill_item_error_quantity_empty)
                } else {
                    null
                },
            )
            PosOutlinedTextField(
                value = costPrice,
                onValueChange = { newCostPrice ->
                    if (newCostPrice.filter { it.isDigit() }.length <= MAX_CURRENCY_LENGTH_SIZE) {
                        costPrice =
                            if (newCostPrice.startsWith("0")) {
                                newCostPrice.replaceFirst("0", "")
                            } else {
                                newCostPrice
                            }
                    } else {
                        costPriceError = R.string.feature_bill_item_error_price_size
                    }
                    costPrice = newCostPrice
                },
                label = stringResource(R.string.feature_bill_cost_price_hint),
                keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                isError = costPriceError != null,
                supportingText = costPriceError?.let { stringResource(it) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ItemExposeDropdownMenuBox(
    modifier: Modifier = Modifier,
    searchItemText: String,
    currentNameItem: String,
    filterItemUiState: SearchItemUiState,
    onSearchItemChange: (String) -> Unit,
    onItemClick: (Item) -> Unit,
    clearItemSelection: () -> Unit,
    label: String,
    readOnly: Boolean = false,
    isError: Boolean,
) {
    val (allowExpanded, setExpanded) = remember { mutableStateOf(false) }
    val expanded = allowExpanded ||
        filterItemUiState is SearchItemUiState.Success &&
        currentNameItem.isBlank()
    Timber.e("expended: $expanded")
    Timber.e("filterItemUiState: $filterItemUiState")
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = setExpanded,
    ) {
        PosOutlinedTextField(
            modifier =
            modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .fillMaxWidth(),
            label = label,
            value = if (currentNameItem.isNotBlank()) currentNameItem else searchItemText,
            onValueChange = { onSearchItemChange(it) },
            readOnly = readOnly || currentNameItem.isNotBlank(),
            supportingText =
            if (filterItemUiState is SearchItemUiState.EmptyResult) {
                stringResource(R.string.feature_bill_search_items_empty_result_message)
            } else if (filterItemUiState is SearchItemUiState.LoadFailed) {
                stringResource(R.string.feature_bill_search_items_failed_message)
            } else if (isError) {
                stringResource(R.string.feature_bill_item_not_selected_message)
            } else {
                null
            },
            trailingIcon = {
                if (currentNameItem.isNotBlank()) {
                    Icon(
                        modifier = Modifier.clickable(onClick = { clearItemSelection() }),
                        imageVector = PosIcons.Close,
                        contentDescription = null,
                    )
                } else {
                    if (filterItemUiState is SearchItemUiState.Loading) {
                        PosLoadingWheel(contentDesc = "searchItems")
                    } else {
                        Icon(
                            modifier = Modifier.clickable(onClick = {}),
                            imageVector = PosIcons.Add,
                            contentDescription = null,
                        )
                    }
                }
            },
            isError = filterItemUiState is SearchItemUiState.LoadFailed || isError,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                setExpanded(false)
            },
        ) {
            if (filterItemUiState is SearchItemUiState.Success) {
                filterItemUiState.items.forEach { item ->
                    DropdownMenuItem(
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        onClick = {
                            onItemClick(item)
                            setExpanded(false)
                        },

                        text = { ItemDropMenuItem(item) },
                    )
                }
            }
        }
    }
}

@Composable
fun ItemDropMenuItem(item: Item) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name)
            Text(item.sku)
        }
        if (item.isTrackStock()) {
            Text(
                stringResource(
                    uiString.core_ui_item_quantity_format,
                    item.quantity,
                ),
            )
        }
    }
}