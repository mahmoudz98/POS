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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.feature.bill.R
import com.casecode.pos.feature.bill.SearchSupplierUiState

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SupplierExposeDropdownMenuBox(
    modifier: Modifier = Modifier,
    searchSupplierText: String,
    currentSupplier: String,
    filterSupplierState: SearchSupplierUiState,
    onSearchSupplierChange: (String) -> Unit,
    onClickSupplier: (String) -> Unit,
    clearSupplierSelection: () -> Unit,
    label: String,
    isError: Boolean = false,
) {
    val (allowExpanded, setExpanded) = remember { mutableStateOf(false) }
    val expanded = allowExpanded || filterSupplierState is SearchSupplierUiState.Success &&
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
            readOnly = currentSupplier.isNotBlank(),
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