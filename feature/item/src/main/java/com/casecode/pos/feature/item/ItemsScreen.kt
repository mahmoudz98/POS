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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.ui.ItemsPreviewParameterProvider
import com.casecode.pos.core.ui.TrackScreenViewEvent
import com.casecode.pos.feature.item.delete.DeleteItemDialog

@Composable
fun ItemsScreen(
    viewModel: ItemsViewModel,
    onAddItemClick: () -> Unit,
    onItemClick: () -> Unit,
    onPrintItemClick: () -> Unit = {},
) {
    val uiState by viewModel.itemsUiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchWidgetState by viewModel.searchWidgetState.collectAsStateWithLifecycle()
    val categories by viewModel.categoriesUiState.collectAsStateWithLifecycle()
    val filterUiState by viewModel.filterUiState.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    var showDialogItemDelete by remember { mutableStateOf(false) }
    ItemsScreen(
        uiState = uiState,
        searchWidgetState = searchWidgetState,
        onSearchClicked = viewModel::openSearchWidgetState,
        searchQuery = searchQuery,
        categories = categories,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onClearRecentSearches = { viewModel.closeSearchWidgetState() },
        filterUiState = filterUiState,
        onFilterStockChange = viewModel::onSortFilterStockChanged,
        onCategorySelected = viewModel::onCategorySelected,
        onCategoryUnselected = viewModel::onCategoryUnselected,
        onSortPriceChanged = viewModel::onSortPriceChanged,
        onClearFilter = viewModel::onClearFilter,
        userMessage = userMessage,
        onItemClick = { item ->
            viewModel.setItemSelected(item)
            onItemClick()
        },
        onItemLongClick = {
            showDialogItemDelete = true
            viewModel.setItemSelected(it)
        },

        onPrintItemClick = {
            viewModel.setItemSelected(it)
            onPrintItemClick()
        },
        onAddItemClick = onAddItemClick,
        onShownMessage = viewModel::snackbarMessageShown,

        )
    if (showDialogItemDelete) {
        DeleteItemDialog(
            onConfirm = {
                viewModel.checkNetworkAndDeleteItem()
                showDialogItemDelete = false
            },
            onDismiss = { showDialogItemDelete = false },
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun ItemsScreen(
    modifier: Modifier = Modifier,
    uiState: ItemsUIState,
    searchWidgetState: SearchWidgetState,
    onSearchClicked: () -> Unit,
    searchQuery: String = "",
    categories: Set<String>,
    filterUiState: FilterUiState,
    onSearchQueryChanged: (String) -> Unit = {},
    onClearRecentSearches: () -> Unit = {},
    onFilterStockChange: (FilterStockState) -> Unit,
    onCategorySelected: (String) -> Unit,
    onCategoryUnselected: (String) -> Unit,
    onSortPriceChanged: (SortPriceState) -> Unit,
    onClearFilter: () -> Unit,
    userMessage: Int?,
    onAddItemClick: () -> Unit,
    onItemClick: (Item) -> Unit,
    onItemLongClick: (Item) -> Unit,
    onPrintItemClick: (Item) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onShownMessage: () -> Unit,
) {
    TrackScreenViewEvent(screenName = "Items")
    var filtersVisible by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            ItemTopAppBar(
                modifier = modifier,
                searchWidgetState = searchWidgetState,
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
                onSearchClicked = { onSearchClicked() },
                onCloseClicked = { onClearRecentSearches() },
            )
        },
        containerColor = Color.Transparent,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier
                    .padding(16.dp)
                    .wrapContentHeight(Alignment.Top),
            )
        },
        floatingActionButton = {
            AnimatedVisibility(visible = !filtersVisible) {
                FloatingActionButton(
                    onClick = {
                        onAddItemClick()
                    },
                    modifier = modifier.padding(8.dp),
                ) {
                    Icon(
                        imageVector = PosIcons.Add,
                        contentDescription = null,
                    )
                }
            }
        },
    ) { padding ->

        SharedTransitionLayout {
            Box(modifier = Modifier.padding(padding)) {
                AnimatedContent(
                    uiState,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith
                                fadeOut(animationSpec = tween(300))
                    },
                    modifier =
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                    },
                    label = "Items Content",
                ) { targetState ->
                    when (targetState) {
                        is ItemsUIState.Loading -> {
                            PosLoadingWheel(
                                modifier =
                                Modifier
                                    .fillMaxSize()
                                    .wrapContentSize(Alignment.Center),
                                contentDesc = "LoadingItems",
                            )
                        }

                        is ItemsUIState.Empty, is ItemsUIState.Error -> ItemsEmptyScreen()
                        is ItemsUIState.Success -> {
                            ItemsContent(
                                items = targetState.filteredItems,
                                filterScreenVisible = filtersVisible,
                                onShowFilters = { filtersVisible = true },
                                sharedTransitionScope = this@SharedTransitionLayout,
                                categories = categories,
                                selectedCategories = filterUiState.selectedCategories,
                                onCategorySelected = onCategorySelected,
                                onCategoryUnselected = onCategoryUnselected,
                                onItemClick = {
                                    onItemClick(it)
                                },
                                onPrintItemClick = { onPrintItemClick(it) },
                                onItemLongClick = {
                                    onItemLongClick(it)
                                },

                                )
                        }
                    }
                }
                FilterScreenOverlay(
                    visible = filtersVisible,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    categories = categories,
                    filterUiState = filterUiState,
                    onSortFilterStockChanged = onFilterStockChange,
                    onSortPriceChanged = onSortPriceChanged,
                    onCategorySelected = onCategorySelected,
                    onCategoryUnselected = onCategoryUnselected,
                    onRestDefaultFilter = onClearFilter,
                    onDismiss = { filtersVisible = false },
                )
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
private fun ItemsEmptyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = com.casecode.pos.core.ui.R.drawable.core_ui_ic_outline_inventory_120),
            contentDescription = stringResource(id = R.string.feature_item_empty_items_icon_description),
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ItemScreenSuccessPreview(
    @PreviewParameter(ItemsPreviewParameterProvider::class) items: List<Item>,
) {
    POSTheme {
        ItemsScreen(
            uiState = ItemsUIState.Success(HashMap(items.associateBy { it.sku })),
            searchWidgetState = SearchWidgetState.CLOSED,
            onSearchClicked = {},
            searchQuery = "",
            categories = emptySet(),
            filterUiState = FilterUiState(),
            onSearchQueryChanged = {},
            onClearRecentSearches = {},
            onFilterStockChange = {},
            onCategorySelected = {},
            onCategoryUnselected = {},
            onClearFilter = {},
            onSortPriceChanged = {},
            userMessage = null,
            onItemClick = {},
            onItemLongClick = {},
            onPrintItemClick = {},
            onAddItemClick = {},
            onShownMessage = {},

            )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ItemScreenSuccessWithSearchPreview(
    @PreviewParameter(ItemsPreviewParameterProvider::class) items: List<Item>,
) {
    POSTheme {
        ItemsScreen(
            uiState = ItemsUIState.Success(
                HashMap(items.associateBy { it.sku }),
            ),
            categories = setOf("Phones", "Headphones", "Computer"),
            filterUiState = FilterUiState(),
            onCategorySelected = {},
            onCategoryUnselected = {},
            onFilterStockChange = {},
            onSortPriceChanged = {},
            onClearFilter = {},
            userMessage = null,
            onItemClick = {},
            onItemLongClick = {},
            onPrintItemClick = {},
            onAddItemClick = {},
            onShownMessage = {},
            searchWidgetState = SearchWidgetState.OPENED,
            onSearchClicked = {},
            searchQuery = "",
            onSearchQueryChanged = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ItemScreenLoadingPreview() {
    POSTheme {
        ItemsScreen(
            uiState = ItemsUIState.Loading,
            filterUiState = FilterUiState(),
            onCategorySelected = {},
            onCategoryUnselected = {},
            categories = setOf(),
            onFilterStockChange = {},
            onSortPriceChanged = {},
            onClearFilter = {},
            userMessage = null,
            onItemClick = {},
            onItemLongClick = {},
            onPrintItemClick = {},
            onAddItemClick = {},
            onShownMessage = {},
            searchWidgetState = SearchWidgetState.CLOSED,
            onSearchClicked = {},
            searchQuery = "",
            onSearchQueryChanged = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ItemScreenEmptyPreview() {
    POSTheme {
        ItemsScreen(
            uiState = ItemsUIState.Empty,
            filterUiState = FilterUiState(),
            categories = setOf(),
            onCategorySelected = {},
            onCategoryUnselected = {},
            onFilterStockChange = {},
            onSortPriceChanged = {},
            onClearFilter = {},
            userMessage = null,
            onItemClick = {},
            onItemLongClick = {},
            onPrintItemClick = {},
            onAddItemClick = {},
            onShownMessage = {},
            searchWidgetState = SearchWidgetState.CLOSED,
            onSearchClicked = {},
            searchQuery = "",
            onSearchQueryChanged = {},
        )
    }
}