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

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CurrencyPound
import androidx.compose.material.icons.rounded.Percent
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.component.PosFilledTextField
import com.casecode.pos.core.designsystem.component.PosInputChip
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.model.data.users.DiscountType
import com.casecode.pos.core.ui.utils.toFormattedString
import com.casecode.pos.feature.bill.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun SupplierExposeDropdownMenuBox(
    modifier: Modifier = Modifier,
    searchSupplierText: String,
    currentSupplier: String,
    filterSupplierState: SearchSupplierUiState,
    onSearchSupplierChange: (String) -> Unit,
    onClickSupplier: (String) -> Unit,
    clearSupplierSelection: () -> Unit,
    label: String,
    isError: Boolean = false,
    readonly: Boolean = false,
) {
    val (allowExpanded, setExpanded) = remember { mutableStateOf(false) }
    val expanded = allowExpanded ||
        filterSupplierState is SearchSupplierUiState.Success &&
        currentSupplier.isBlank()
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = setExpanded,
    ) {
        PosOutlinedTextField(
            modifier =
            modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth(),
            readOnly = readonly || currentSupplier.isNotBlank(),
            label = label,
            value = if (currentSupplier.isNotBlank()) currentSupplier else searchSupplierText,
            supportingText =
            if (filterSupplierState is SearchSupplierUiState.EmptyResult) {
                stringResource(R.string.feature_bill_search_supplier_empty_result_message)
            } else if (filterSupplierState is SearchSupplierUiState.LoadFailed) {
                stringResource(R.string.feature_bill_search_suppliers_failed_message)
            } else if (isError) {
                stringResource(R.string.feature_bill_supplier_not_selected_message)
            } else {
                null
            },
            onValueChange = { onSearchSupplierChange(it) },
            trailingIcon = {
                if (currentSupplier.isBlank()) {
                    Icon(
                        modifier = Modifier.clickable(onClick = {}),
                        imageVector = PosIcons.Add,
                        contentDescription = null,
                    )
                } else {
                    Icon(
                        modifier = Modifier.clickable(onClick = { clearSupplierSelection() }),
                        imageVector = PosIcons.Close,
                        contentDescription = null,
                    )
                }
            },
            isError = isError,

        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                setExpanded(false)
            },
        ) {
            if (filterSupplierState is SearchSupplierUiState.Success) {
                filterSupplierState.suppliers.forEach { supplierName ->
                    DropdownMenuItem(
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        onClick = {
                            onClickSupplier(supplierName)
                            setExpanded(false)
                        },

                        text = { Text(supplierName) },
                    )
                }
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun BillItemsTotalSection(
    subTotal: Double,
    discount: String,
    discountTypeCurrency: DiscountType,
    total: Double,
    onDiscountChange: (String) -> Unit,
    onDiscountTypeChange: () -> Unit,
) {
    val isFixed = discountTypeCurrency == DiscountType.FIXED
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
                    selected = isFixed,
                    onSelectedChange = { onDiscountTypeChange() },
                    modifier = Modifier
                        .padding(start = quarterWidth)
                        .align(Alignment.Bottom),
                    selectedIcon = Icons.Rounded.CurrencyPound,
                    unSelectedIcon = Icons.Rounded.Percent,
                ) {
                    if (isFixed) {
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
                    text = stringResource(R.string.feature_bill_total_text),
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

@Preview
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SupplierExposeDropdownMenuBoxPreview(
    @PreviewParameter(SearchSupplierUiStateProvider::class)
    filterSupplierState: SearchSupplierUiState,
) {
    SupplierExposeDropdownMenuBox(
        searchSupplierText = "Current Text",
        currentSupplier = "Current Supplier",
        filterSupplierState = filterSupplierState,
        onClickSupplier = {},
        onSearchSupplierChange = {},
        clearSupplierSelection = {},
        label = "Label",
        isError = false,
    )
}

class SearchSupplierUiStateProvider : PreviewParameterProvider<SearchSupplierUiState> {
    override val values: Sequence<SearchSupplierUiState>
        get() = sequenceOf(
            SearchSupplierUiState.Success(listOf("Supplier 1", "Supplier 2", "Supplier 3")),
            SearchSupplierUiState.EmptyResult,
            SearchSupplierUiState.EmptyQuery,
            SearchSupplierUiState.LoadFailed,
            SearchSupplierUiState.Loading,
        )
}