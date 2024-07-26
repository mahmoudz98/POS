package com.casecode.pos.feature.invoice

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.data.utils.toDateFormatString
import com.casecode.pos.core.data.utils.toTimeFormatedString
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Invoice
import com.casecode.pos.core.model.data.users.InvoiceGroup
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.component.VerticalGrid
import com.casecode.pos.core.designsystem.component.scrollbar.DraggableScrollbar
import com.casecode.pos.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.casecode.pos.core.designsystem.component.scrollbar.scrollbarState
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesRoute(
    viewModel: InvoicesViewModel = hiltViewModel(),
    onMenuClick: () -> Unit,
    onInvoiceDetailsClick: () -> Unit,
) {
    val uiState by viewModel.uiInvoiceState.collectAsStateWithLifecycle()
    var showDialogDate by remember { mutableStateOf(false) }

    InvoicesScreen(
        uiInvoiceState = uiState,
        onInvoiceClick = {
            onInvoiceDetailsClick()
            viewModel.setSelectedInvoice(it)
        },
        onMenuClick = { onMenuClick() },
        onActionClick = { showDialogDate = true },
        onClearFilterDate = { viewModel.setDateInvoiceSelected(null) },
    )

    if (showDialogDate) {
        DatePickerView(
            onDismiss = { showDialogDate = false },
            onDataSelected = {
                viewModel.setDateInvoiceSelected(it)
            },
            currentSelectedDate = uiState.dateInvoiceSelected,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(
    modifier: Modifier = Modifier,
    uiInvoiceState: UiInvoiceState,
    onInvoiceClick: (Invoice) -> Unit,
    onMenuClick: () -> Unit,
    onActionClick: () -> Unit,
    onClearFilterDate: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val isShowingClearFilterDate by remember(uiInvoiceState.dateInvoiceSelected) {
        derivedStateOf { uiInvoiceState.dateInvoiceSelected != null }
    }
    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier
                    .padding(16.dp)
                    .wrapContentHeight(Alignment.Top),
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                modifier = modifier,
                visible = isShowingClearFilterDate,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                SmallFloatingActionButton(
                    onClick = { onClearFilterDate() },
                    modifier = modifier.padding(16.dp),
                ) {
                    Icon(
                        imageVector = PosIcons.Close,
                        contentDescription = null,
                    )
                }

            }
        },


        ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            PosTopAppBar(
                modifier = modifier,
                titleRes = R.string.feature_invoice_title,
                navigationIcon = PosIcons.Menu,
                navigationIconContentDescription = "",
                onActionClick = { onActionClick() },
                actionIconContentDescription = null,
                actionIcon = PosIcons.Calender,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                ),
                onNavigationClick = { onMenuClick() },
            )
            AnimatedContent(
                uiInvoiceState.resourceInvoiceGroups,
                transitionSpec = {
                    fadeIn(
                        animationSpec = tween(3000),
                    ) togetherWith fadeOut(animationSpec = tween(3000))
                },
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    uiInvoiceState.resourceInvoiceGroups =
                        when (uiInvoiceState.resourceInvoiceGroups) {
                            Resource.Loading -> Resource.Loading
                            is Resource.Empty -> Resource.empty()
                            is Resource.Error -> uiInvoiceState.resourceInvoiceGroups
                            is Resource.Success -> uiInvoiceState.resourceInvoiceGroups
                        }
                },
                label = "Animated Content",
            ) { targetState ->
                when (targetState) {
                    is Resource.Loading -> {
                        PosLoadingWheel(
                            modifier = modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center),
                            contentDesc = "LoadingItems",
                        )
                    }

                    is Resource.Empty -> {
                        InvoiceEmptyScreen()
                    }

                    is Resource.Error -> {
                        InvoiceEmptyScreen()
                    }

                    is Resource.Success -> {
                        val filteredItems =
                            remember(targetState, uiInvoiceState.dateInvoiceSelected) {
                                if (uiInvoiceState.dateInvoiceSelected != null) {
                                    val date = Date(uiInvoiceState.dateInvoiceSelected)
                                    val dateFormat = date.toDateFormatString()

                                    targetState.data.filter {
                                        it.date.contains(dateFormat)
                                    }
                                } else {
                                    targetState.data
                                }
                            }
                        InvoiceGroupList(invoiceGroups = filteredItems) {
                            onInvoiceClick(it)
                        }
                    }
                }

            }

        }

    }


}

@Composable
fun InvoiceEmptyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = com.casecode.pos.core.ui.R.drawable.core_ui_ic_outline_inventory_120),
            contentDescription = stringResource(id = R.string.feature_invoice_empty_message),
            modifier = Modifier.size(120.dp))

        Text(
            text = stringResource(id = R.string.feature_invoice_empty),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            modifier = Modifier.padding(top = 16.dp))

        Text(
            text = stringResource(id = R.string.feature_invoice_empty_message),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
fun InvoiceGroupList(
    invoiceGroups: List<InvoiceGroup>,
    modifier: Modifier = Modifier,
    onItemClick: (Invoice) -> Unit,
) {
    val scrollableState = rememberLazyListState()
    Box(modifier = modifier.fillMaxWidth()) {
        LazyColumn(
            modifier = modifier.padding(horizontal = 8.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            state = scrollableState,
        ) {
            invoiceGroups.forEach {
                item {
                    InvoiceGroupItem(invoiceGroup = it, onItemClick = onItemClick)
                }
            }

            item { Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing)) }

        }
        val scrollbarState = scrollableState.scrollbarState(itemsAvailable = invoiceGroups.size)
        scrollableState.DraggableScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(CenterEnd),
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved = scrollableState.rememberDraggableScroller(
                itemsAvailable = invoiceGroups.size,
            ),
        )
    }
}

@Composable
fun InvoiceGroupItem(
    invoiceGroup: InvoiceGroup,
    onItemClick: (Invoice) -> Unit,
) {

    Column(modifier = Modifier.padding(start = 8.dp)) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(start = 4.dp),
        ) {
            Text(
                text = invoiceGroup.date,
                style = MaterialTheme.typography.titleSmall,
            )
            VerticalDivider(Modifier.padding(horizontal = 4.dp))
            Text(
                text = stringResource(com.casecode.pos.core.ui.R.string.core_ui_currency, invoiceGroup.totalInvoiceGroup),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondaryContainer,
            )
        }
        VerticalGrid(Modifier.padding(4.dp)) {
            invoiceGroup.invoices.forEach { invoice ->
                InvoiceCard(
                    modifier = Modifier.padding(4.dp),
                    invoice = invoice,
                    onItemClick = onItemClick,
                )
            }
        }

    }
}

@Composable
fun InvoiceCard(modifier: Modifier = Modifier, invoice: Invoice, onItemClick: (Invoice) -> Unit) {
    ElevatedCard(
        modifier = modifier.clickable { onItemClick(invoice) },
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(
                text = invoice.date.toDateFormatString(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp),
            )
            Text(
                text = invoice.date.toTimeFormatedString(),
                style = MaterialTheme.typography.bodyMedium,
            )
            HorizontalDivider(Modifier.padding(vertical = 4.dp))
            Text(
                text = stringResource(com.casecode.pos.core.ui.R.string.core_ui_currency, invoice.total.toString()),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}


@Preview(device = "spec:width=360dp,height=640dp,dpi=320", showBackground = true)
@Composable
fun InvoiceScreenSuccessPreview() {
    InvoicesScreen(
        uiInvoiceState = UiInvoiceState(
            resourceInvoiceGroups = Resource.Success(
                listOf(
                    InvoiceGroup(

                        "12 june 2024",
                        listOf(
                            Invoice(
                                invoiceId = "12",
                                date = Date(),
                                createdBy = "1423",
                                customer = null,
                                items = listOf(),
                            ),
                            Invoice(
                                invoiceId = "12",
                                date = Date(),
                                createdBy = "1423",
                                customer = null,
                                items = listOf(),
                            ),
                            Invoice(
                                invoiceId = "12",
                                date = Date(),
                                createdBy = "1423",
                                customer = null,
                                items = listOf(),
                            ),
                            Invoice(
                                invoiceId = "12",
                                date = Date(),
                                createdBy = "1423",
                                customer = null,
                                items = listOf(),
                            ),
                            Invoice(
                                invoiceId = "12",
                                date = Date(),
                                createdBy = "1423",
                                customer = null,
                                items = listOf(),
                            ),
                            Invoice(
                                invoiceId = "12",
                                date = Date(),
                                createdBy = "1423",
                                customer = null,
                                items = listOf(),
                            ),
                        ),
                    ),
                    InvoiceGroup(
                        "",
                        listOf(
                            Invoice(
                                invoiceId = "12",
                                date = Date(),
                                createdBy = "1423",
                                customer = null,
                                items = listOf(),
                            ),
                            Invoice(
                                invoiceId = "12",
                                date = Date(),
                                createdBy = "1423",
                                customer = null,
                                items = listOf(),
                            ),
                        ),
                    ),
                    InvoiceGroup(
                        "",
                        listOf(
                            Invoice(
                                invoiceId = "12",
                                date = Date(),
                                createdBy = "1423",
                                customer = null,
                                items = listOf(),
                            ),

                            ),
                    ),
                ),
            ),
        ),
        onInvoiceClick = {},
        onMenuClick = {},
        onActionClick = {},
    )
}

@Preview(device = "id:pixel_2", showBackground = true)
@Composable
fun InvoiceScreenEmptyPreview() {
    InvoicesScreen(
        uiInvoiceState = UiInvoiceState(resourceInvoiceGroups = Resource.empty()),
        onInvoiceClick = {},
        onMenuClick = {},
        onActionClick = {},
    )
}

@Preview(device = "id:pixel_2", showBackground = true)
@Composable
fun InvoiceScreenErrorPreview() {
    InvoicesScreen(
        uiInvoiceState = UiInvoiceState(resourceInvoiceGroups = Resource.error("")),
        onInvoiceClick = {},
        onMenuClick = {},
        onActionClick = {},
    )
}

@Preview(device = "id:pixel_2", showBackground = true)
@Composable
fun InvoiceScreenLoadingPreview() {
    InvoicesScreen(
        uiInvoiceState = UiInvoiceState(resourceInvoiceGroups = Resource.loading()),
        onInvoiceClick = {},
        onMenuClick = {},
        onActionClick = {},
    )
}


@Preview(showBackground = true)
@Composable
fun InvoiceGroupItemPreview() {
    POSTheme {
        InvoiceGroupItem(
            invoiceGroup = InvoiceGroup(
                "",
                listOf(
                    Invoice(
                        invoiceId = "12",
                        date = Date(),
                        createdBy = "1423",
                        customer = null,
                        items = listOf(),
                    ),
                    Invoice(
                        invoiceId = "12",
                        date = Date(),
                        createdBy = "1423",
                        customer = null,
                        items = listOf(),
                    ),
                    Invoice(
                        invoiceId = "12",
                        date = Date(),
                        createdBy = "1423",
                        customer = null,
                        items = listOf(),
                    ),
                    Invoice(
                        invoiceId = "12",
                        date = Date(),
                        createdBy = "1423",
                        customer = null,
                        items = listOf(),
                    ),
                    Invoice(
                        invoiceId = "12",
                        date = Date(),
                        createdBy = "1423",
                        customer = null,
                        items = listOf(),
                    ),
                    Invoice(
                        invoiceId = "12",
                        date = Date(),
                        createdBy = "1423",
                        customer = null,
                        items = listOf(),
                    ),
                ),
            ),
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
fun InvoiceItemPreview() {
    POSTheme {
        InvoiceCard(
            invoice = Invoice(
                invoiceId = "12",
                date = Date(),
                createdBy = "1423",
                customer = null,
                items = listOf(),
            ),
        ) { }
    }
}