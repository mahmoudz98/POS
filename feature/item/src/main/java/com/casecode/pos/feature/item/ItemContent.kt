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
package com.casecode.pos.feature.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.component.DynamicAsyncImage
import com.casecode.pos.core.designsystem.component.PosFilterChip
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.component.SearchToolbar
import com.casecode.pos.core.designsystem.component.SearchTopAppBar
import com.casecode.pos.core.designsystem.component.SearchWidgetState
import com.casecode.pos.core.designsystem.component.scrollbar.DraggableScrollbar
import com.casecode.pos.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.casecode.pos.core.designsystem.component.scrollbar.scrollbarState
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.ui.parameterprovider.ItemsPreviewParameterProvider
import java.text.DecimalFormat
import com.casecode.pos.core.ui.R.string as uiString

@Composable
fun ItemTopAppBar(
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
        titleRes = R.string.feature_item_header_title,
        actionIcon = PosIcons.Search,
        onNavigationClick = { onBackClick() },
        onActionClick = { onSearchClicked() },
        actionIconContentDescription = stringResource(R.string.feature_item_search_action_text),
        colors =
        TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
        ),
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun CategoryFilterChips(
    filterScreenVisible: Boolean,
    onShowFilters: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    categories: Set<String>,
    selectedCategories: Set<String>,
    onCategorySelected: (String) -> Unit,
    onCategoryUnselected: (String) -> Unit,
) {
    with(sharedTransitionScope) {
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 8.dp),
            modifier = Modifier.heightIn(min = 56.dp),
        ) {
            item {
                AnimatedVisibility(visible = !filterScreenVisible) {
                    IconButton(
                        onClick = onShowFilters,
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
                            contentDescription = stringResource(R.string.feature_item_filter_label),
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
            categories.forEach { category ->
                val isSelected = selectedCategories.contains(category)
                item(category) {
                    PosFilterChip(
                        selected = isSelected,
                        onSelectedChange = {
                            if (isSelected) {
                                onCategoryUnselected(category)
                            } else {
                                onCategorySelected(category)
                            }
                        },
                        label = { Text(text = category) },
                    )
                }
            }
        }
    }
}

private fun Modifier.diagonalGradientBorder(
    colors: List<Color>,
    borderSize: Dp = 2.dp,
    shape: Shape,
) = border(
    width = borderSize,
    brush = Brush.linearGradient(colors),
    shape = shape,
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun ItemsContent(
    items: List<Item>,
    filterScreenVisible: Boolean,
    onShowFilters: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    categories: Set<String>,
    selectedCategories: Set<String>,
    onCategorySelected: (String) -> Unit,
    onCategoryUnselected: (String) -> Unit,
    onItemClick: (Item) -> Unit = {},
    onItemLongClick: (Item) -> Unit = {},
    onPrintItemClick: (Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        val scrollableState = rememberLazyListState()
        LazyColumn(
            modifier = modifier.padding(horizontal = 8.dp).clipToBounds(),
            contentPadding = PaddingValues(vertical = 8.dp),
            state = scrollableState,
        ) {
            item {
                CategoryFilterChips(
                    filterScreenVisible = filterScreenVisible,
                    onShowFilters = onShowFilters,
                    sharedTransitionScope = sharedTransitionScope,
                    categories = categories,
                    selectedCategories = selectedCategories,
                    onCategorySelected = onCategorySelected,
                    onCategoryUnselected = onCategoryUnselected,
                )
                if (categories.isNotEmpty()) {
                    HorizontalDivider(Modifier.padding(bottom = 4.dp))
                }
            }
            if (items.isEmpty()) {
                item { ItemsSearchEmptyScreen() }
            } else {
                items.forEach { item ->
                    val itemKey = item.hashCode()
                    item(key = itemKey) {
                        ItemItem(
                            modifier = Modifier.animateItem(),
                            name = item.name,
                            price = item.unitPrice,
                            quantity = item.quantity,
                            isTracked = item.isTrackStock(),
                            itemImageUrl = item.imageUrl,
                            onClick = { onItemClick(item) },
                            onPrintButtonClick = { onPrintItemClick(item) },
                            onLongClick = { onItemLongClick(item) },
                        )
                    }
                }
            }
            item {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
            }
        }
        val scrollbarState =
            scrollableState.scrollbarState(
                itemsAvailable = items.size,
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
                itemsAvailable = items.size,
            ),
        )
    }
}

@Composable
private fun ItemsSearchEmptyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            imageVector = PosIcons.EmptySearch,
            contentDescription = stringResource(
                id = R.string.feature_item_empty_items_icon_description,
            ),
            modifier = Modifier.size(120.dp),
        )

        Text(
            text = stringResource(id = R.string.feature_item_empty_items_filter_title),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
internal fun ItemsEmptyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(
                id = com.casecode.pos.core.ui.R.drawable.core_ui_ic_outline_inventory_120,
            ),
            contentDescription = stringResource(
                id = R.string.feature_item_empty_items_icon_description,
            ),
            modifier = Modifier.size(120.dp),
        )

        Text(
            text = stringResource(id = R.string.feature_item_empty_items_title),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            modifier = Modifier.padding(top = 16.dp),
        )

        Text(
            text = stringResource(id = R.string.feature_item_empty_items_message),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun ItemItem(
    name: String,
    price: Double,
    quantity: Int,
    isTracked: Boolean,
    itemImageUrl: String?,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onPrintButtonClick: () -> Unit,
) {
    ListItem(
        leadingContent = { ItemIcon(itemImageUrl, iconModifier.size(64.dp)) },
        overlineContent = { Text(name) },
        headlineContent = {
            if (isTracked) {
                Text(
                    stringResource(
                        uiString.core_ui_item_quantity_format,
                        quantity,
                    ),
                )
            }
        },
        supportingContent = {
            val formattedPrice = DecimalFormat("#,###.##").format(price)
            Text(stringResource(uiString.core_ui_currency, formattedPrice))
        },
        trailingContent = {
            IconButton(onClick = onPrintButtonClick) {
                Icon(
                    imageVector = PosIcons.Print,
                    contentDescription = stringResource(
                        R.string.feature_item_dialog_print_button_text,
                    ),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier =
        modifier.combinedClickable(
            onClick = { onClick() },
            onLongClick = { onLongClick() },
            onLongClickLabel = stringResource(R.string.feature_item_dialog_delete_item_title),
        ),
    )
}

@Composable
private fun ItemIcon(itemImageUrl: String?, modifier: Modifier = Modifier) {
    if (itemImageUrl.isNullOrEmpty()) {
        Icon(
            modifier =
            modifier
                .background(Color.Transparent)
                .padding(4.dp),
            imageVector = PosIcons.EmptyImage,
            contentDescription = null,
        )
    } else {
        DynamicAsyncImage(
            imageUrl = itemImageUrl,
            contentDescription = null,
            modifier = modifier,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultAppBarPreview() {
    DefaultTopAppBar(
        onBackClick = {},
        onSearchClicked = {},
    )
}

@Preview
@Composable
private fun ItemItemCardPreview() {
    POSTheme {
        Surface {
            ItemItem(
                name = "name",
                price = 200.0,
                quantity = 12,
                isTracked = true,
                itemImageUrl = "",
                onClick = {},
                onPrintButtonClick = {},
                onLongClick = {},
            )
            ItemItem(
                name = "name",
                price = 200.0,
                quantity = 12,
                isTracked = false,
                itemImageUrl = "",
                onClick = {},
                onPrintButtonClick = {},
                onLongClick = {},
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun ItemsContentPreview(
    @PreviewParameter(ItemsPreviewParameterProvider::class) items: List<Item>,
) {
    POSTheme {
        SharedTransitionLayout {
            ItemsContent(
                items,
                onItemClick = { _ -> },
                onItemLongClick = { _ -> },
                onPrintItemClick = { _ -> },
                filterScreenVisible = false,
                onShowFilters = {},
                sharedTransitionScope = this@SharedTransitionLayout,
                categories = setOf<String>(),
                selectedCategories = setOf(),
                onCategorySelected = {},
                onCategoryUnselected = {},
            )
        }
    }
}