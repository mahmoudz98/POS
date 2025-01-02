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
package com.casecode.pos.feature.bill

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.component.FilterTitleText
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosElevatedFilterChip
import com.casecode.pos.core.designsystem.component.PosFilterChip
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.ui.DevicePreviews
import com.casecode.pos.core.ui.PosFilterScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun FilterBillsScreenOverlay(
    isVisible: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    filterUiState: BillsFilterUiState,
    suppliers: Set<String>,
    onBillsUiEvent: (BillsUiEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    AnimatedVisibility(visible = isVisible, enter = fadeIn(), exit = fadeOut()) {
        FilterBillsScreen(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this,
            filterUiState = filterUiState,
            suppliers = suppliers,
            onBillsUiEvent = onBillsUiEvent,
            onDismiss = onDismiss,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun FilterBillsScreen(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    filterUiState: BillsFilterUiState,
    suppliers: Set<String>,
    onBillsUiEvent: (BillsUiEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    val resetEnabled =
        filterUiState.paymentStatusFilter != PaymentStatusFilter.All ||
            filterUiState.selectedSuppliers.isNotEmpty() ||
            filterUiState.sortType != InvoiceSortType.DateNewToOld
    PosFilterScreen(
        modifier = modifier,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        enableFilter = resetEnabled,
        onRestFilterClick = { onBillsUiEvent(BillsUiEvent.RestDefaultFilter) },
        onDismiss = onDismiss,
    ) {
        FilterPaymentStatusSection(
            paymentStatusFilter = filterUiState.paymentStatusFilter,
            onSortPaymentStatusChanged = {
                onBillsUiEvent(
                    BillsUiEvent.SortPaymentStatusChanged(
                        it,
                    ),
                )
            },
        )
        InvoiceSortSection(
            sortState = filterUiState.sortType,
            onSortChanged = { onBillsUiEvent(BillsUiEvent.SortTypeChanged(it)) },
        )
        FilterSuppliersSection(
            suppliers = suppliers,
            selectedSuppliers = filterUiState.selectedSuppliers,
            onBillsUiEvent = onBillsUiEvent,
        )
        BackHandler(onBack = onDismiss)
    }
}

@Composable
private fun FilterPaymentStatusSection(
    paymentStatusFilter: PaymentStatusFilter,
    onSortPaymentStatusChanged: (PaymentStatusFilter) -> Unit,
) {
    FilterTitleText(text = stringResource(R.string.feature_bill_filter_payment_status_label))
    Column(Modifier.padding(bottom = 24.dp)) {
        FilterPaymentStatusItems(
            sortState = paymentStatusFilter,
            onChanged = onSortPaymentStatusChanged,
        )
    }
}

@Composable
private fun FilterPaymentStatusItems(
    paymentStatusFilters: List<PaymentStatusFilter> = PaymentStatusFilter.entries,
    sortState: PaymentStatusFilter,
    onChanged: (PaymentStatusFilter) -> Unit,
) {
    paymentStatusFilters.forEach { paymentStatusFilter ->
        PosElevatedFilterChip(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(paymentStatusFilter.labelId)) },
            selected = sortState == paymentStatusFilter,
            onSelectedChange = {
                onChanged(paymentStatusFilter)
            },
        )
    }
}

@Composable
private fun InvoiceSortSection(
    sortState: InvoiceSortType,
    onSortChanged: (InvoiceSortType) -> Unit,
) {
    FilterTitleText(text = stringResource(R.string.feature_bill_sort_label))
    Column(Modifier.padding(bottom = 24.dp)) {
        InvoiceSortItems(sortState = sortState, onChanged = onSortChanged)
    }
}

@Composable
private fun InvoiceSortItems(
    invoiceSortTypes: List<InvoiceSortType> = InvoiceSortType.entries,
    sortState: InvoiceSortType,
    onChanged: (InvoiceSortType) -> Unit,
) {
    invoiceSortTypes.forEach { invoiceSortType ->
        PosElevatedFilterChip(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(invoiceSortType.labelId)) },
            selected = sortState == invoiceSortType,
            onSelectedChange = {
                onChanged(invoiceSortType)
            },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterSuppliersSection(
    modifier: Modifier = Modifier,
    suppliers: Set<String>,
    selectedSuppliers: Set<String>,
    onBillsUiEvent: (BillsUiEvent) -> Unit,
) {
    FilterTitleText(text = stringResource(R.string.feature_bill_filter_supplier_label))
    FlowRow(
        modifier = modifier
            .padding(vertical = 8.dp)
            .padding(horizontal = 4.dp),
    ) {
        suppliers.forEach { supplier ->
            val isSelected = selectedSuppliers.contains(supplier)
            PosFilterChip(
                selected = isSelected,
                onSelectedChange = {
                    if (isSelected) {
                        onBillsUiEvent(BillsUiEvent.SupplierUnselected(supplier))
                    } else {
                        onBillsUiEvent(BillsUiEvent.SupplierSelected(supplier))
                    }
                },
                label = { Text(text = supplier) },
                modifier = Modifier.padding(end = 4.dp, bottom = 8.dp),
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@DevicePreviews
@Composable
private fun FilterBillsScreenPreview() {
    POSTheme {
        PosBackground {
            SharedTransitionLayout {
                FilterBillsScreenOverlay(
                    isVisible = true,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    filterUiState = BillsFilterUiState(),
                    suppliers = setOf("Mahmoud", "Ahmed", "Ali"),
                    onBillsUiEvent = {},
                    onDismiss = {},
                )
            }
        }
    }
}

@Preview
@Composable
private fun InvoiceSortSectionPreview() {
    POSTheme {
        PosBackground {
            InvoiceSortSection(sortState = InvoiceSortType.DateNewToOld, onSortChanged = {})
        }
    }
}

@Preview
@Composable
private fun FilterPaymentStatusSectionPreview() {
    POSTheme {
        PosBackground {
            FilterPaymentStatusSection(
                paymentStatusFilter = PaymentStatusFilter.All,
                onSortPaymentStatusChanged = {},
            )
        }
    }
}