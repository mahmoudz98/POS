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

import android.Manifest
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PermissionDialog
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosEmptyScreen
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.model.utils.toBigDecimalFormatted
import com.casecode.pos.core.ui.DevicePreviews
import com.casecode.pos.core.ui.TrackScreenViewEvent
import com.casecode.pos.core.ui.parameterprovider.SupplierInvoiceParameterProvider
import com.casecode.pos.core.ui.utils.PdfInvoiceUtils
import com.casecode.pos.feature.bill.R
import com.casecode.pos.feature.bill.toPaymentRes
import com.casecode.pos.feature.bill.toPaymentStatusColor
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BillScreen(
    viewModel: BillDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onEditBill: () -> Unit,
    onPaymentClick: () -> Unit,
) {
    val uiState by viewModel.billDetailsUiState.collectAsStateWithLifecycle(
        BillDetailUiState.Loading,
    )
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    BillScreenContent(
        billUiState = uiState,
        userMessage = userMessage,
        onShownMessage = viewModel::snackbarMessageShown,
        onNavigateBack = onNavigateBack,
        onEditBill = onEditBill,
        onAddNewPaymentClick = onPaymentClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillScreenContent(
    modifier: Modifier = Modifier,
    billUiState: BillDetailUiState,
    userMessage: Int? = null,
    onShownMessage: () -> Unit = {},
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
    val snackbarHostState = remember { SnackbarHostState() }
    Box(
        Modifier.fillMaxSize(),
    ) {
        SnackbarHost(
            snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
        )
        when (billUiState) {
            is BillDetailUiState.Loading -> {
                PosLoadingWheel(
                    contentDesc = "BillLoading",
                    Modifier.align(Alignment.Center),
                )
            }

            is BillDetailUiState.Error, is BillDetailUiState.EmptySelection -> {
                PosEmptyScreen(
                    Modifier.align(Alignment.Center),
                    icon = PosIcons.Payment,
                    titleRes = R.string.feature_bill_selection_empty_title,
                    messageRes = R.string.feature_bill_selection_empty_title,
                )
            }

            is BillDetailUiState.Success -> {
                Column {
                    PosTopAppBar(
                        titleRes = R.string.feature_bill_title_text,
                        navigationIcon = PosIcons.ArrowBack,
                        navigationIconContentDescription = null,
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent,
                        ),
                        action = {
                            IconButton(
                                onClick = { onEditBill() },
                            ) {
                                Icon(imageVector = PosIcons.Edit, contentDescription = null)
                            }
                        },
                        onNavigationClick = onNavigateBack,
                        scrollBehavior = scrollBehavior,
                    )
                    LazyColumn(
                        modifier = modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        item {
                            BillHeader(billUiState.supplierInvoice)
                        }
                        item {
                            BillTabs(pagerState, coroutineScope)
                        }
                        item {
                            BillPager(
                                pagerState = pagerState,
                                invoice = billUiState.supplierInvoice,
                            )
                        }
                    }
                }
                if (pagerState.currentPage == InvoicePagerTab.PAYMENTS.ordinal &&
                    billUiState.supplierInvoice.isPaid.not()
                ) {
                    FloatingActionButton(
                        onClick = onAddNewPaymentClick,
                        Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomEnd),
                    ) {
                        Icon(imageVector = PosIcons.Payment, contentDescription = null)
                    }
                }
            }
        }
    }
    userMessage?.let { message ->
        val snackbarText = stringResource(message)
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(snackbarText)
            onShownMessage()
        }
    }
}

@Composable
private fun BillHeader(invoice: SupplierInvoice) {
    val context = LocalContext.current
    var openPdf by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)) {
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
        Text(text = invoice.supplierName, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = invoice.totalAmount.toBigDecimalFormatted(),
                style = MaterialTheme.typography.titleMedium,
            )
            Image(
                imageVector = PosIcons.Pdf, contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        openPdf = true

                    },
            )
        }
    }
    HandlePdfExport(
        openPdf,
        onPermissionGranted = {
            val result = PdfInvoiceUtils.createInvoicePDF(context, invoice)
            val uri = PdfInvoiceUtils.savePDFUsingFileProvider(context, result)
            PdfInvoiceUtils.openPDF(context, uri)
            openPdf = false
        },
        onPermissionDenied = {
            openPdf = false
        },
    )


}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun HandlePdfExport(
    openPdf: Boolean,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
) {
    if (LocalInspectionMode.current) return

    var showPermissionDialog by remember { mutableStateOf(false) }

    val permissionState = rememberMultiplePermissionsState(
        permissions = when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )

            else -> emptyList()
        },
    )

    if (showPermissionDialog) {
        PermissionDialog(
            onDismiss = {
                showPermissionDialog = false
                onPermissionDenied()
            },
            messagePermission = R.string.feature_bill_need_permission_for_storage,
        )
    }

    LaunchedEffect(openPdf, permissionState.allPermissionsGranted) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (permissionState.allPermissionsGranted) {
                onPermissionGranted()
            } else {
                permissionState.launchMultiplePermissionRequest()
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BillTabs(pagerState: PagerState, coroutineScope: CoroutineScope) {
    TabRow(
        selectedTabIndex = pagerState.currentPage,
    ) {
        InvoicePagerTab.entries.forEachIndexed { index, tab ->
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
private fun BillPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    invoice: SupplierInvoice,
) {
    HorizontalPager(
        modifier = modifier.wrapContentSize(),
        state = pagerState,
    ) { page ->
        when (InvoicePagerTab.entries[page]) {
            InvoicePagerTab.DETAILS -> BillDetailsTap(supplierInvoice = invoice)
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
                billUiState = BillDetailUiState.Success(bills[0]),
                onNavigateBack = {},
                onEditBill = {},
                onAddNewPaymentClick = {},
            )
        }
    }
}