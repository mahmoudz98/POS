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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.ui.DevicePreviews
import com.casecode.pos.core.ui.TrackScreenViewEvent
import com.casecode.pos.core.ui.parameterprovider.SupplierInvoiceParameterProvider
import com.casecode.pos.core.ui.utils.toBigDecimalFormatted
import com.casecode.pos.feature.bill.BillViewModel
import com.casecode.pos.feature.bill.InvoiceSelectionUiState
import com.casecode.pos.feature.bill.R
import com.casecode.pos.feature.bill.toPaymentRes
import com.casecode.pos.feature.bill.toPaymentStatusColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BillScreen(
    viewModel: BillViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onEditBill: () -> Unit,
    onPaymentClick: () -> Unit,
) {
    val uiState by viewModel.supplierInvoiceSelected.collectAsStateWithLifecycle()

    BillScreenContent(
        invoiceSelectionUiState = uiState,
        onNavigateBack = onNavigateBack,
        onEditBill = onEditBill,
        onAddNewPaymentClick = onPaymentClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillScreenContent(
    modifier: Modifier = Modifier,
    invoiceSelectionUiState: InvoiceSelectionUiState,
    onNavigateBack: () -> Unit,
    onEditBill: () -> Unit,
    onAddNewPaymentClick: () -> Unit,
) {
    TrackScreenViewEvent(screenName = "Bill")
    val pagerState = rememberPagerState(
        initialPage = InvoicePagerTab.DETAILS.ordinal,
    ) { InvoicePagerTab.entries.size }
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            PosTopAppBar(
                titleRes = R.string.feature_bill_title_text,
                navigationIcon = PosIcons.ArrowBack,
                navigationIconContentDescription = null,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                ),
                action = {
                    IconButton(onClick = onEditBill) {
                        Icon(imageVector = PosIcons.Edit, contentDescription = null)
                    }
                },
                onNavigationClick = onNavigateBack,
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (invoiceSelectionUiState is InvoiceSelectionUiState.Success &&
                pagerState.currentPage == InvoicePagerTab.PAYMENTS.ordinal &&
                invoiceSelectionUiState.supplierInvoice.isPaid.not()
            ) {
                FloatingActionButton(onClick = onAddNewPaymentClick) {
                    Icon(imageVector = PosIcons.Payment, contentDescription = null)
                }
            }
        },
    ) { innerPadding ->
        Box(
            Modifier.fillMaxSize()
                .padding(innerPadding),
        ) {
            when (invoiceSelectionUiState) {
                is InvoiceSelectionUiState.Loading -> {
                    PosLoadingWheel(contentDesc = "BillLoading", Modifier.align(Alignment.Center))
                }

                is InvoiceSelectionUiState.Error -> {
                }

                is InvoiceSelectionUiState.EmptySelection -> {
                }

                is InvoiceSelectionUiState.Success -> {
                    LazyColumn(
                        modifier = modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        item {
                            BillHeader(invoiceSelectionUiState.supplierInvoice)
                            Spacer(modifier = Modifier.height(16.dp))
                            BillTabs(pagerState, coroutineScope)
                            Spacer(modifier = Modifier.height(16.dp))
                            BillPager(pagerState, invoiceSelectionUiState.supplierInvoice)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BillHeader(invoice: SupplierInvoice) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = invoice.billNumber, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = stringResource(toPaymentRes(invoice.paymentStatus)),
                color = toPaymentStatusColor(invoice.paymentStatus),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = invoice.supplierName, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = invoice.totalAmount.toBigDecimalFormatted(),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun BillTabs(pagerState: PagerState, coroutineScope: CoroutineScope) {
    TabRow(selectedTabIndex = pagerState.currentPage) {
        InvoicePagerTab.entries.forEach { tab ->
            Tab(
                selected = pagerState.currentPage == tab.ordinal,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(tab.ordinal)
                    }
                },
                text = { Text(stringResource(tab.titleRes)) },
            )
        }
    }
}

@Composable
private fun BillPager(pagerState: PagerState, invoice: SupplierInvoice) {
    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerState,
    ) { page ->
        when (InvoicePagerTab.entries[page]) {
            InvoicePagerTab.DETAILS -> BillDetailsTap(invoiceSupplier = invoice)
            InvoicePagerTab.PAYMENTS -> BillPaymentTap(paymentDetails = invoice.paymentDetails)
            InvoicePagerTab.HISTORY -> BillHistoryTap(
                issueDate = invoice.issueDate,
                total = invoice.totalAmount,
                createdBy = invoice.createdBy,
                paymentDetails = invoice.paymentDetails,
            )
        }
    }
}

private enum class InvoicePagerTab(val titleRes: Int) {
    DETAILS(R.string.feature_bill_tap_details_title_text),
    PAYMENTS(R.string.feature_bill_tap_payment_title_text),
    HISTORY(R.string.feature_bill_tap_history_title_text),
}

@OptIn(ExperimentalMaterial3Api::class)
@DevicePreviews
@Composable
fun BillScreenPreview(
    @PreviewParameter(SupplierInvoiceParameterProvider::class) bills: List<SupplierInvoice>,
) {
    POSTheme {
        PosBackground {
            BillScreenContent(
                invoiceSelectionUiState = InvoiceSelectionUiState.Success(bills[0]),
                onNavigateBack = {},
                onEditBill = {},
                onAddNewPaymentClick = {},
            )
        }
    }
}