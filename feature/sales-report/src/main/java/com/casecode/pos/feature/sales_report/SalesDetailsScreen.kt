package com.casecode.pos.feature.sales_report

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.data.utils.toFormattedDateTimeString
import com.casecode.pos.core.model.data.users.Invoice
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.designsystem.component.DynamicAsyncImage
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.component.scrollbar.DraggableScrollbar
import com.casecode.pos.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.casecode.pos.core.designsystem.component.scrollbar.scrollbarState
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import java.text.DecimalFormat
import java.util.Date

@Composable
fun SalesReportDetailsRoute(viewModel: SalesReportViewModel, onBackClick: () -> Unit) {
    val uiSalesReportDetails =
        viewModel.invoiceSelected.collectAsStateWithLifecycle(UISalesReportDetails.Loading)
    SalesReportDetailsScreen(
        uiSalesReportDetails = uiSalesReportDetails.value,
        onPrintClick = {},
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesReportDetailsScreen(
    modifier: Modifier = Modifier,
    uiSalesReportDetails: UISalesReportDetails,
    onPrintClick: () -> Unit,
    onBackClick: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        PosTopAppBar(
            modifier = Modifier,
            titleRes = R.string.feature_sales_report_details_title,
            navigationIcon = PosIcons.ArrowBack,
            navigationIconContentDescription = stringResource(
                id = com.casecode.pos.core.ui.R.string.core_ui_dialog_cancel_button_text,
            ),
            onActionClick = { onPrintClick() },
            actionIconContentDescription = stringResource(R.string.feature_sales_report_print_action_text),
            actionIcon = PosIcons.Print,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent,
            ),
            onNavigationClick = { onBackClick() },
        )
        when (uiSalesReportDetails) {
            is UISalesReportDetails.Loading -> {
                PosLoadingWheel(
                    modifier = modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    contentDesc = "LoadingItems",
                )
            }

            is UISalesReportDetails.Empty -> {
                SalesReportEmptyScreen()
            }

            is UISalesReportDetails.Success -> {
                SalesReportDetailsContent(uiSalesReportDetails.invoice)
            }
        }
    }
}


@Composable
fun SalesReportDetailsContent(invoice: Invoice) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(start = 16.dp),
        ) {
            Text(
                text = invoice.date.toFormattedDateTimeString(),
                style = MaterialTheme.typography.titleSmall,
            )
            VerticalDivider(Modifier.padding(horizontal = 4.dp))
            Text(
                text = stringResource(com.casecode.pos.core.ui.R.string.core_ui_currency, invoice.total),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondaryContainer,
            )
        }

        HorizontalDivider(Modifier.padding(16.dp))

        ItemsSalesReportDetailsContent(invoice.items)
    }

}

@Composable
fun ItemsSalesReportDetailsContent(
    items: List<Item>,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        val scrollableState = rememberLazyListState()
        LazyColumn(
            modifier = modifier.padding(horizontal = 8.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            state = scrollableState,
        ) {
            items.forEach { item ->
                val sku = item.sku
                item(key = sku) {
                    ItemInvoice(
                        name = item.name,
                        price = item.price,
                        quantity = item.quantity,
                        itemImageUrl = item.imageUrl ?: "",
                    )
                }
            }

            item {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
            }

        }
        val scrollbarState = scrollableState.scrollbarState(
            itemsAvailable = items.size,
        )
        scrollableState.DraggableScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(Alignment.CenterEnd),
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved = scrollableState.rememberDraggableScroller(
                itemsAvailable = items.size,
            ),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemInvoice(
    name: String,
    price: Double,
    quantity: Double,
    itemImageUrl: String,
    modifier: Modifier = Modifier,
) {
    ListItem(
        leadingContent = { ItemIcon(itemImageUrl, modifier.size(64.dp)) },
        headlineContent = { Text(name) },
        supportingContent = {
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                val formattedQuantity = "${stringResource(com.casecode.pos.core.ui.R.string.core_ui_item_quantity_format)} $quantity"
                Text(formattedQuantity)
                VerticalDivider(
                    modifier = Modifier.padding(horizontal = 4.dp),
                )

                val formattedPrice =
                    DecimalFormat("#,###.##").format(price * quantity)
                Text(stringResource(com.casecode.pos.core.ui.R.string.core_ui_currency, formattedPrice))
            }
        },

        colors = ListItemDefaults.colors(containerColor = Color.Transparent),


        )
}

@Composable
private fun ItemIcon(topicImageUrl: String, modifier: Modifier = Modifier) {
    if (topicImageUrl.isEmpty()) {
        Icon(
            modifier = modifier
                .background(Color.Transparent)
                .padding(4.dp),
            imageVector = PosIcons.EmptyImage,
            // decorative image
            contentDescription = null,
        )
    } else {
        DynamicAsyncImage(
            imageUrl = topicImageUrl,
            contentDescription = null,
            modifier = modifier,
        )
    }
}

@Preview(device = "spec:width=360dp,height=640dp,dpi=320", showBackground = true)
@Composable
fun SalesReportDetailsScreenSuccessPreview() {
    POSTheme {
        SalesReportDetailsContent(
            Invoice(
                invoiceId = "1212",
                date = Date(),
                createdBy = "2",
                customer = null,
                items = listOf(
                    Item(
                        name = "Eloise McCoy",
                        price = 4.5,
                        quantity = 6.7,
                        sku = "ad",
                        unitOfMeasurement = null,
                        imageUrl = null,
                    ),
                ),
            ),
        )
    }
}