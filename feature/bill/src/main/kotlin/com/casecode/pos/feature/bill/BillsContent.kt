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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosEmptyScreen
import com.casecode.pos.core.designsystem.component.PosFilterChip
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.component.SearchToolbar
import com.casecode.pos.core.designsystem.component.SearchTopAppBar
import com.casecode.pos.core.designsystem.component.SearchWidgetState
import com.casecode.pos.core.designsystem.component.diagonalGradientBorder
import com.casecode.pos.core.designsystem.component.scrollbar.DraggableScrollbar
import com.casecode.pos.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.casecode.pos.core.designsystem.component.scrollbar.scrollbarState
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.PaymentStatus
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.model.utils.toBigDecimalFormatted
import com.casecode.pos.core.model.utils.toFormattedDateString
import com.casecode.pos.core.ui.FilterSharedElementKey
import com.casecode.pos.core.ui.parameterprovider.SupplierInvoiceParameterProvider
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Composable
fun BillsTopAppBar(
    modifier: Modifier = Modifier,
    searchWidgetState: SearchWidgetState,
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun BillsContent(
    modifier: Modifier = Modifier,
    filterScreenVisible: Boolean,
    bills: Map<String, SupplierInvoice>,
    suppliers: Set<String>,
    selectedSuppliers: Set<String>,
    onBillsUiEvent: (BillsUiEvent) -> Unit,
    onShowFilters: () -> Unit,
    onBillClick: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
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
            item {
                BillsFilteredSection(
                    filterScreenVisible = filterScreenVisible,
                    suppliers = suppliers,
                    selectedSuppliers = selectedSuppliers,
                    onBillsUiEvent = onBillsUiEvent,
                    onShowFilterScreen = onShowFilters,
                    sharedTransitionScope = sharedTransitionScope,
                )
                HorizontalDivider(Modifier.padding(bottom = 4.dp))
            }
            if (bills.isEmpty()) {
                item {
                    PosEmptyScreen(
                        icon = PosIcons.EmptySearch,
                        titleRes = R.string.feature_bill_empty_title,
                        messageRes = R.string.feature_bill_empty_filter_message,
                    )
                }
            } else {
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun BillsFilteredSection(
    modifier: Modifier = Modifier,
    filterScreenVisible: Boolean,
    suppliers: Set<String>,
    selectedSuppliers: Set<String>,
    onShowFilterScreen: () -> Unit,
    onBillsUiEvent: (BillsUiEvent) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
) {
    with(sharedTransitionScope) {
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 8.dp),
            modifier = modifier.heightIn(min = 56.dp),
        ) {
            item {
                AnimatedVisibility(visible = !filterScreenVisible) {
                    IconButton(
                        onClick = onShowFilterScreen,
                        modifier =
                        Modifier
                            .sharedBounds(
                                rememberSharedContentState(FilterSharedElementKey),
                                animatedVisibilityScope = this@AnimatedVisibility,
                                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                            ),
                    ) {
                        Icon(
                            imageVector = PosIcons.Filter,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null,
                            modifier =
                            Modifier.diagonalGradientBorder(
                                colors =
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.secondaryContainer,
                                ),
                                shape = CircleShape,
                            ),
                        )
                    }
                }
            }
            suppliers.forEach { supplier ->
                val isSelected = selectedSuppliers.contains(supplier)
                item(supplier) {
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
                    )
                }
            }
        }
    }
}

@Composable
private fun BillItem(
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
fun BillsListPreview(
    @PreviewParameter(SupplierInvoiceParameterProvider::class) bills: List<SupplierInvoice>,
) {
    POSTheme {
        PosBackground {
            SharedTransitionLayout {
                BillsContent(
                    filterScreenVisible = false,
                    onShowFilters = {},
                    sharedTransitionScope = this,
                    bills = bills.associateBy { it.invoiceId },
                    suppliers = bills.map { it.supplierName }.toSet(),
                    selectedSuppliers = emptySet(),
                    onBillsUiEvent = {},
                    onBillClick = {},
                )
            }
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