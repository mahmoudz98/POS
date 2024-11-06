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
package com.casecode.pos.feature.sale

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.casecode.pos.core.designsystem.component.DynamicAsyncImage
import com.casecode.pos.core.designsystem.component.PosOutlinedButton
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.Item
import java.text.DecimalFormat
@Composable
internal fun SectionSaleItems(
    modifier: Modifier = Modifier,
    hasItemsSale: Boolean,
    totalSaleItems: Double,
    amountInput: String,
    restOfAmount: Double,
    onAmountChanged: (String) -> Unit,
    onSaveInvoice: () -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = hasItemsSale,
        enter = slideInVertically(initialOffsetY = { -40 }) +
            expandVertically(expandFrom = Alignment.Top) +
            scaleIn(
                transformOrigin = TransformOrigin(0.5f, 0f),
            ) + fadeIn(initialAlpha = 0.3f),
        exit =
        slideOutVertically() + shrinkVertically() + fadeOut() +
            scaleOut(
                targetScale = 1.2f,
            ),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PosOutlinedTextField(
                value = amountInput,
                onValueChange = { onAmountChanged(it) },
                label = stringResource(R.string.feature_sale_enter_amount_hint),
                supportingText =
                stringResource(
                    R.string.feature_sale_total_price_text,
                    totalSaleItems.toBigDecimal(),
                ) + stringResource(
                    R.string.feature_sale_sale_rest_amount_text,
                    restOfAmount.toBigDecimal(),
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(0.7f),
            )
            PosOutlinedButton(
                onClick = onSaveInvoice,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .fillMaxWidth()
                    .weight(0.3f)
                    .align(Alignment.CenterVertically),
            ) {
                Text(stringResource(R.string.feature_sale_button_text))
            }
        }
    }
}

@Composable
internal fun ColumnScope.SectionCartItems(
    searchItemsUiState: SearchItemsUiState,
    items: Set<Item>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onScan: () -> Unit,
    onSearchItemClick: (Item) -> Unit,
    onRemoveItem: (Item) -> Unit,
    onUpdateQuantity: (Item) -> Unit,
) {
    ExposedDropdownMenuBoxSearch(
        searchItemsUiState = searchItemsUiState,
        searchQuery = searchQuery,
        onSearchQueryChanged = onSearchQueryChanged,
        onScan = onScan,
        onSearchItemClick = onSearchItemClick,
    )
    if (items.isEmpty()) {
        SaleItemsInvoiceEmpty()
    } else {
        SaleItems(
            itemsInvoice = items,
            onRemoveItem = onRemoveItem,
            onUpdateQuantity = onUpdateQuantity,
        )
    }
}

@Composable
fun isExpended(windowSizeClass: WindowSizeClass, configuration: Configuration): Boolean =
    windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED ||
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuBoxSearch(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    searchItemsUiState: SearchItemsUiState,
    onScan: () -> Unit,
    onSearchItemClick: (Item) -> Unit,
) {
    val (allowExpanded, setExpanded) = remember { mutableStateOf(false) }

    val expanded = allowExpanded || searchItemsUiState is SearchItemsUiState.Success
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    ExposedDropdownMenuBox(
        modifier = modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = setExpanded,
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            label = {
                Text(
                    stringResource(R.string.feature_sale_sale_search_hint),
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            modifier =
            Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onKeyEvent {
                    if (it.key == Key.Enter) {
                        keyboardController?.hide()
                        true
                    } else {
                        false
                    }
                },
            maxLines = 1,
            singleLine = true,
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChanged("") }) {
                        Icon(
                            PosIcons.Close,
                            contentDescription = null,
                        )
                    }
                } else {
                    IconButton(onClick = onScan) {
                        Icon(
                            PosIcons.QrCodeScanner,
                            contentDescription = stringResource(
                                R.string.feature_sale_scan_item_text,
                            ),
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions =
            KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                },
            ),
        )
        when (searchItemsUiState) {
            is SearchItemsUiState.Success -> {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { setExpanded(false) },
                ) {
                    searchItemsUiState.items.forEach { item ->
                        DropdownMenuItem(
                            text = { ItemDropMenuItem(item) },
                            onClick = {
                                onSearchItemClick(item)
                                setExpanded(false)
                                onSearchQueryChanged("")
                            },
                        )
                    }
                }
            }
            else -> {
                Text(stringResource(R.string.feature_sale_search_empty))
            }
        }
    }
}

@Composable
fun ItemDropMenuItem(item: Item) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name)
            Text(item.sku)
        }
        if (item.isTrackStock()) {
            Text(
                stringResource(
                    com.casecode.pos.core.ui.R.string.core_ui_item_quantity_format,
                    item.quantity,
                ),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColumnScope.SaleItems(
    itemsInvoice: Set<Item>,
    onRemoveItem: (Item) -> Unit,
    onUpdateQuantity: (Item) -> Unit,
) {
    val scrollableState = rememberLazyListState()
    LazyColumn(
        modifier =
        Modifier
            .weight(1f)
            .imeNestedScroll()
            .padding(horizontal = 8.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        state = scrollableState,
    ) {
        itemsInvoice.forEach { item ->
            val sku = item.sku
            item(key = sku) {
                ItemSale(
                    name = item.name,
                    price = item.unitPrice,
                    quantity = item.quantity,
                    itemImageUrl = item.imageUrl ?: "",
                    onRemoveItem = { onRemoveItem(item) },
                    onUpdateQuantity = { onUpdateQuantity(item) },
                )
            }
        }
    }
}

@Composable
internal fun ItemSale(
    name: String,
    price: Double,
    quantity: Int,
    itemImageUrl: String,
    onRemoveItem: () -> Unit,
    onUpdateQuantity: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        leadingContent = { ItemIcon(itemImageUrl, modifier.size(64.dp)) },
        headlineContent = { Text(name, fontWeight = FontWeight.Bold) },
        supportingContent = {
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                val formattedQuantity =
                    stringResource(
                        com.casecode.pos.core.ui.R.string.core_ui_item_quantity_format,
                        quantity,
                    )
                Text(formattedQuantity)
                VerticalDivider(
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
                val formattedPrice =
                    DecimalFormat("#,###.##").format(price)
                Text(
                    stringResource(
                        com.casecode.pos.core.ui.R.string.core_ui_currency,
                        formattedPrice,
                    ),
                )
            }
        },
        trailingContent = {
            IconButton(onClick = { onRemoveItem() }) {
                Icon(
                    imageVector = PosIcons.Delete,
                    contentDescription = stringResource(
                        R.string.feature_sale_dialog_delete_invoice_item_title,
                    ),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable(onClick = { onUpdateQuantity() }),
    )
}

@Composable
private fun ItemIcon(topicImageUrl: String, modifier: Modifier = Modifier) {
    if (topicImageUrl.isEmpty()) {
        Icon(
            modifier =
            modifier
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

@Preview(showBackground = true)
@Composable
fun ItemDropMenuItemPreview() {
    POSTheme {
        ItemDropMenuItem(
            Item(
                name = "Iphone5",
                unitPrice = 202.0,
                quantity = 12222,
                sku = "12345678912345",
                unitOfMeasurement = null,
                imageUrl = null,
            ),
        )
    }
}

@Preview
@Composable
fun SaleItemPreview() {
    POSTheme {
        ItemSale("Item Name", 10.0, 5, "", {}, {})
    }
}