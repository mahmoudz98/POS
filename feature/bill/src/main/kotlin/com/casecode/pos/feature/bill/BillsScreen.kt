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

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.data.utils.toFormattedDateString
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.component.SearchToolbar
import com.casecode.pos.core.designsystem.component.SearchTopAppBar
import com.casecode.pos.core.designsystem.component.SearchWidgetState
import com.casecode.pos.core.designsystem.component.scrollbar.DraggableScrollbar
import com.casecode.pos.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.casecode.pos.core.designsystem.component.scrollbar.scrollbarState
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.PaymentStatus
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.ui.parameterprovider.SupplierInvoiceParameterProvider
import com.casecode.pos.core.ui.utils.toBigDecimalFormatted
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Composable
fun BillsScreen(
    viewModel: BillViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onAddBillClick: () -> Unit,
    onBillClick: () -> Unit,
) {
    val uiState = viewModel.billsUiState.collectAsStateWithLifecycle()
    BillsScreen(
        uiState = uiState.value,
        onBackClick = onBackClick,
        onAddBillClick = onAddBillClick,
        onBillClick = {
            viewModel.onSupplierInvoiceIdSelected(it)
            onBillClick()
        },
    )
}

@Composable
fun BillsScreen(
    uiState: BillsUiState,
    onBackClick: () -> Unit,
    onAddBillClick: () -> Unit,
    onBillClick: (String) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                modifier = Modifier,
                onBackClick = onBackClick,
                onSearchClicked = {},
            )
        },
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAddBillClick()
                },
                modifier = Modifier.padding(8.dp),
            ) {
                Icon(
                    imageVector = PosIcons.Add,
                    contentDescription = null,
                )
            }
        },

    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            when (uiState) {
                is BillsUiState.Loading -> {
                    PosLoadingWheel(
                        modifier =
                        Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                        contentDesc = "LoadingSupplierInvoices",
                    )
                }

                is BillsUiState.Success -> BillsList(
                    bills = uiState.supplierInvoices,
                    onBillClick = onBillClick,
                )

                is BillsUiState.Error, is BillsUiState.Empty -> {
                    BillsEmptyScreen()
                }
            }
        }
    }
}

@Composable
fun BillsTopAppBar(
    searchWidgetState: SearchWidgetState,
    modifier: Modifier,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onBackClick: () -> Unit,
    onSearchClicked: () -> Unit,
    onCloseClicked: () -> Unit,
) {
    SearchTopAppBar(
        searchWidgetState = searchWidgetState,
        defaultTopAppBar = {
            DefaultTopAppBar(
                modifier = modifier,
                onBackClick = onBackClick,
                onSearchClicked = onSearchClicked,
            )
        },
        searchTopAppBar = {
            SearchToolbar(
                modifier = modifier,
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
                onCloseClicked = onCloseClicked,
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onSearchClicked: () -> Unit,
) {
    PosTopAppBar(
        modifier = modifier,
        navigationIcon = PosIcons.ArrowBack,
        titleRes = R.string.feature_bill_header_title,
        actionIcon = PosIcons.Search,
        onNavigationClick = { onBackClick() },
        onActionClick = { onSearchClicked() },
        colors =
        TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
        ),
    )
}

@Composable
internal fun BillsEmptyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(
                id = com.casecode.pos.core.ui.R.drawable.core_ui_ic_outline_inventory_120,
            ),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
        )

        Text(
            text = stringResource(id = R.string.feature_bill_empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            modifier = Modifier.padding(top = 16.dp),
        )

        Text(
            text = stringResource(id = R.string.feature_bill_empty_message),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
fun BillsList(
    bills: Map<String, SupplierInvoice>,
    modifier: Modifier = Modifier,
    onBillClick: (String) -> Unit,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        val scrollableState = rememberLazyListState()

        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clipToBounds(),
            contentPadding = PaddingValues(vertical = 8.dp),
            state = scrollableState,
        ) {
            items(bills.values.toList(), key = { it.invoiceId }) { bill ->
                BillItem(
                    modifier = Modifier.animateItem(),
                    billNumber = bill.billNumber,
                    supplierName = bill.supplierName,
                    issueDate = bill.issueDate,
                    status = bill.paymentStatus,
                    amount = bill.totalAmount,
                    dueAmount = bill.dueAmount,
                ) {
                    onBillClick(bill.invoiceId)
                }
            }
            item {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
            }
        }
        val scrollbarState =
            scrollableState.scrollbarState(
                itemsAvailable = bills.size,
            )
        scrollableState.DraggableScrollbar(
            modifier =
            Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(Alignment.CenterEnd),
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved =
            scrollableState.rememberDraggableScroller(
                itemsAvailable = bills.size,
            ),
        )
    }
}

@Composable
fun BillItem(
    modifier: Modifier = Modifier,
    billNumber: String,
    supplierName: String,
    issueDate: Instant,
    status: PaymentStatus,
    amount: Double,
    dueAmount: Double,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(issueDate.toFormattedDateString())
        },
        leadingContent = {
            Text(
                modifier = Modifier.width(40.dp),
                text = billNumber,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        },
        overlineContent = {
            Text(
                text = supplierName,

                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = { Text(text = stringResource(toPaymentRes(status))) },
        trailingContent = {
            Column {
                Text(amount.toBigDecimalFormatted())
                if (dueAmount < amount) {
                    Text("Due: ${dueAmount.toBigDecimalFormatted()}")
                }
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
            supportingColor = toPaymentStatusColor(status),
        ),
        modifier = modifier.clickable(onClick = onClick),
    )
}

@Preview
@Composable
fun BillsListPreview(
    @PreviewParameter(SupplierInvoiceParameterProvider::class) bills: List<SupplierInvoice>,
) {
    POSTheme {
        PosBackground {
            BillsList(bills.associateBy { it.invoiceId }) {}
        }
    }
}

@Preview
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BillItemPreview() {
    POSTheme {
        PosBackground {
            BillItem(
                billNumber = "1231w321312312312312312q",
                supplierName = "Ahmed",
                issueDate = Clock.System.now(),
                status = PaymentStatus.PAID,
                amount = 12233.2,
                dueAmount = 122.2,
            ) {}
        }
    }
}