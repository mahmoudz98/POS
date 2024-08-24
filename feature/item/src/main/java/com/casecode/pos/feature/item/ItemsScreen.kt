package com.casecode.pos.feature.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
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
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Item
import timber.log.Timber

@Composable
fun ItemsRoute(
    modifier: Modifier = Modifier,
    viewModel: ItemsViewModel,
    onAddItemClick: () -> Unit,
    onItemClick: () -> Unit,
    onPrintItemClick: () -> Unit = {},
) {
    val uiState by viewModel.uiItemsState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchWidgetState by viewModel.searchWidgetState.collectAsStateWithLifecycle()

    var showDialogItemDelete by remember { mutableStateOf(false) }
    ItemsScreen(
        modifier = modifier,
        uiState = uiState,
        onItemClick = { item ->
            viewModel.setItemSelected(item)
            onItemClick()
        },
        onItemLongClick = {
            showDialogItemDelete = true
            viewModel.setItemSelected(it)
        },
        searchWidgetState = searchWidgetState,
        onSearchClicked = viewModel::openSearchWidgetState,
        searchQuery = searchQuery,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onClearRecentSearches = { viewModel.closeSearchWidgetState() },
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

@Composable
internal fun ItemsScreen(
    modifier: Modifier = Modifier,
    uiState: UIItemsState,
    onAddItemClick: () -> Unit,
    onItemClick: (Item) -> Unit,
    onItemLongClick: (Item) -> Unit,
    onPrintItemClick: (Item) -> Unit,
    searchWidgetState: SearchWidgetState,
    onSearchClicked: () -> Unit,
    searchQuery: String = "",
    onSearchQueryChanged: (String) -> Unit = {},
    onClearRecentSearches: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onShownMessage: () -> Unit,
) {
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
            FloatingActionButton(
                onClick = {
                    onAddItemClick()
                },
                modifier = modifier.padding(16.dp),
            ) {
                Icon(
                    imageVector = PosIcons.Add,
                    contentDescription = stringResource(R.string.feature_item_add_item_action_text),
                )
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding),
        ) {
            ItemTopAppBar(
                modifier = modifier,
                searchWidgetState = searchWidgetState,
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
                onSearchClicked = { onSearchClicked() },
                onCloseClicked = {
                    onClearRecentSearches()
                },
            )
            when (uiState.resourceItems) {
                is Resource.Loading -> {
                    PosLoadingWheel(
                        modifier =
                            modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center),
                        contentDesc = "LoadingItems",
                    )
                }

                is Resource.Empty -> {
                    ItemsEmptyScreen()
                }

                is Resource.Error -> {
                    ItemsEmptyScreen()
                }

                is Resource.Success -> {
                    val filteredItems =
                        remember(uiState, searchQuery) {
                            if (searchQuery.isNotBlank()) {
                                uiState.resourceItems.data.filter {
                                    it.name.contains(
                                        searchQuery,
                                        ignoreCase = true,
                                    ) ||
                                            it.sku.contains(searchQuery, ignoreCase = true)
                                }
                            } else {
                                uiState.resourceItems.data
                            }
                        }
                    ItemsContent(
                        items = filteredItems,
                        modifier = modifier,
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
    }
    // Check for user messages to display on the screen
    uiState.userMessage?.let { message ->
        val snackbarText = stringResource(message)
        Timber.e("snackbarText $snackbarText")
        LaunchedEffect(snackbarHostState, message, snackbarText) {
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
    @PreviewParameter(com.casecode.pos.core.ui.ItemsPreviewParameterProvider::class) items: List<Item>,
) {
    POSTheme {
        ItemsScreen(
            uiState = UIItemsState(Resource.Success(items), null),
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ItemScreenSuccessWithSearchPreview(
    @PreviewParameter(com.casecode.pos.core.ui.ItemsPreviewParameterProvider::class) items: List<Item>,
) {
    POSTheme {
        ItemsScreen(
            uiState = UIItemsState(Resource.Success(items), null),
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
            uiState = UIItemsState(Resource.Loading, null),
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
            uiState = UIItemsState(Resource.Empty(), null),
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