package com.casecode.pos.feature.item

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.ui.TrackScreenViewEvent

@Composable
fun ItemsSaleScreen(
    viewModel: ItemsViewModel = hiltViewModel(),
    onPrintItemClick: () -> Unit = {},
) {
    val uiState by viewModel.itemsUiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchWidgetState by viewModel.searchWidgetState.collectAsStateWithLifecycle()
    val categories by viewModel.categoriesUiState.collectAsStateWithLifecycle()
    val filterUiState by viewModel.filterUiState.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    ItemsSaleScreen(
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
        onPrintItemClick = {
            viewModel.setItemSelected(it)
            onPrintItemClick()
        },
        onShownMessage = viewModel::snackbarMessageShown,

        )

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun ItemsSaleScreen(
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
                                onPrintItemClick = { onPrintItemClick(it) },
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