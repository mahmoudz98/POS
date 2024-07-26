package com.casecode.pos.feature.item

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.designsystem.component.DynamicAsyncImage
import com.casecode.pos.core.designsystem.component.scrollbar.DraggableScrollbar
import com.casecode.pos.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.casecode.pos.core.designsystem.component.scrollbar.scrollbarState
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import java.text.DecimalFormat

@Composable
fun ItemsContent(
    items: List<Item>,
    onItemClick: (Item) -> Unit,
    onItemLongClick: (Item) -> Unit,
    onPrintItemClick: (Item) -> Unit,
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
                    ItemItem(
                        name = item.name,
                        price = item.price,
                        quantity = item.quantity,
                        itemImageUrl = item.imageUrl ?: "",
                        onClick = { onItemClick(item) },
                        onPrintButtonClick = { onPrintItemClick(item) },
                        onLongClick = { onItemLongClick(item) },
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
fun ItemItem(
    name: String,
    price: Double,
    quantity: Double,
    itemImageUrl: String,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onPrintButtonClick: () -> Unit) {
    ListItem(
        leadingContent = { ItemIcon(itemImageUrl, iconModifier.size(64.dp)) },
        headlineContent = { Text(name) },
        supportingContent = {
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                val formattedQuantity = "${stringResource(com.casecode.pos.core.ui.R.string.core_ui_item_quantity_format)} $quantity"
                Text(formattedQuantity)
                VerticalDivider(
                    modifier = Modifier.padding(horizontal = 4.dp),
                )

                val formattedPrice =
                    DecimalFormat("#,###.##").format(price)
                Text(stringResource(com.casecode.pos.core.ui.R.string.core_ui_currency, formattedPrice))
            }
        },
        trailingContent = {
            IconButton(onClick = onPrintButtonClick) {
                Icon(
                    imageVector = PosIcons.Print,
                    contentDescription = stringResource(R.string.feature_item_dialog_print_button_text),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = modifier.combinedClickable(
            onClick = { onClick() },
            onLongClick = { onLongClick() },
            onLongClickLabel = stringResource(R.string.feature_item_dialog_delete_item_title),
        ),

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

@Preview
@Composable
private fun ItemItemCardPreview() {
    POSTheme {
        Surface {
            ItemItem(
                name = "name",
                price = 200.0,
                quantity = 12.0,
                itemImageUrl = "",
                onClick = {},
                onPrintButtonClick = {},
                onLongClick = {},
            )

        }
    }
}

@Preview
@Composable
private fun ItemsContentPreview(
    @PreviewParameter(com.casecode.pos.core.ui.ItemsPreviewParameterProvider::class) items: List<Item>,
) {
    POSTheme {
        ItemsContent(
            items,
            onItemClick = { _ -> },
            onItemLongClick = { _ -> },
            onPrintItemClick = { _ -> },
        )
    }

}