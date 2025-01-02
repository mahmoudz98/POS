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

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.recalculateWindowInsets
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.ui.DevicePreviews
import com.casecode.pos.core.ui.scanOptions

@Composable
internal fun SaleScreen(viewModel: SaleViewModel = hiltViewModel(), onGoToItems: () -> Unit) {
    val context = LocalContext.current
    val itemsUiState by viewModel.itemsUiState.collectAsStateWithLifecycle()
    val saleItemsState = viewModel.saleItemsState
    val totalSaleItems = viewModel.totalSaleItems
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchItemsUiState by viewModel.searchResultItemsUiState.collectAsStateWithLifecycle()
    val amountInput by viewModel.amountInput.collectAsStateWithLifecycle()
    val restOfAmount by viewModel.restOfAmount.collectAsStateWithLifecycle()
    val itemSelected by viewModel.itemSelected.collectAsStateWithLifecycle()
    val itemInvoiceSelected by viewModel.itemInvoiceSelected.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    var showUpdateQuantityItem by remember { mutableStateOf(false) }

    SaleScreen(
        itemsUiState = itemsUiState,
        saleItemsState = saleItemsState,
        totalSaleItems = totalSaleItems,
        searchItemsUiState = searchItemsUiState,
        searchQuery = searchQuery,
        amountInput = amountInput,
        restOfAmount = restOfAmount,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        userMessage = userMessage,
        onSnackbarMessageShown = viewModel::snackbarMessageShown,
        onScan = {
            context.scanOptions(
                onModuleDownloaded = { viewModel.showSnackbarMessage(it) },
                onModuleDownloading = { viewModel.showSnackbarMessage(it) },
                onResult = {
                    viewModel.scanItem(it)
                },
                onFailure = {
                    viewModel.showSnackbarMessage(it)
                },
                onCancel = { viewModel.showSnackbarMessage(it) },
            )
        },
        onSearchItemClick = viewModel::addItemInvoice,
        onGoToItems = onGoToItems,
        onRemoveItem = viewModel::deleteItemInvoice,
        onUpdateQuantity = {
            viewModel.updateQuantityItemSelected(it)
            showUpdateQuantityItem = true
        },
        onAmountChanged = viewModel::updateAmount,
        onSaveInvoice = viewModel::updateStockAndAddItemInvoice,
    )
    if (showUpdateQuantityItem) {
        QuantityDialog(
            oldQuantity = itemInvoiceSelected?.quantity ?: 0,
            inStock =
            itemSelected?.quantity?.plus(
                itemInvoiceSelected?.quantity ?: 0,
            ) ?: 0,
            isTrackingQuantity = itemSelected?.isTrackStock() == true,
            onDismiss = { showUpdateQuantityItem = false },
            onConfirm = {
                viewModel.updateQuantityItemInvoice(it)
                showUpdateQuantityItem = false
            },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SaleScreen(
    modifier: Modifier = Modifier,
    itemsUiState: ItemsUiState,
    saleItemsState: Set<Item>,
    totalSaleItems: Double,
    searchItemsUiState: SearchItemsUiState,
    searchQuery: String,
    amountInput: String,
    restOfAmount: Double,
    userMessage: Int? = null,
    onSearchQueryChanged: (String) -> Unit,
    onSearchItemClick: (Item) -> Unit,
    onSnackbarMessageShown: () -> Unit = {},
    onScan: () -> Unit,
    onGoToItems: () -> Unit,
    onRemoveItem: (Item) -> Unit,
    onUpdateQuantity: (Item) -> Unit,
    onAmountChanged: (String) -> Unit,
    onSaveInvoice: () -> Unit,
    windowSizeClass: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    ReportDrawnWhen { saleItemsState.isNotEmpty() }
    ReportDrawnWhen { itemsUiState is ItemsUiState.Success }
    val hasItemsSale by remember(saleItemsState) {
        derivedStateOf { saleItemsState.isNotEmpty() }
    }
    val configuration = LocalConfiguration.current
    val snackState = remember { SnackbarHostState() }
    SnackbarHost(hostState = snackState, Modifier.zIndex(1f))
    Box(
        modifier
            .fillMaxSize()
            .recalculateWindowInsets(),
    ) {
        if (!isExpended(windowSizeClass.windowSizeClass, configuration)) {
            Column(
                modifier =
                modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .navigationBarsPadding()
                    .imePadding(),
            ) {
                SaleContentPortrait(
                    itemsUiState = itemsUiState,
                    searchItemsUiState = searchItemsUiState,
                    saleItems = saleItemsState,
                    totalSaleItems = totalSaleItems,
                    onGoToItems = onGoToItems,
                    searchQuery = searchQuery,
                    amountInput = amountInput,
                    restOfAmount = restOfAmount,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onScan = onScan,
                    onSearchItemClick = onSearchItemClick,
                    onRemoveItem = onRemoveItem,
                    onUpdateQuantity = onUpdateQuantity,
                    hasItemsSale = hasItemsSale,
                    onAmountChanged = onAmountChanged,
                    onSaveInvoice = onSaveInvoice,
                )
            }
        } else {
            Row(
                modifier = modifier
                    .padding(8.dp)
                    .navigationBarsPadding()
                    .imePadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                SaleContentLandscape(
                    itemsUiState = itemsUiState,
                    saleItemsState = saleItemsState,
                    searchItemsUiState = searchItemsUiState,
                    searchQuery = searchQuery,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onScan = onScan,
                    onSearchItemClick = onSearchItemClick,
                    onRemoveItem = onRemoveItem,
                    onUpdateQuantity = onUpdateQuantity,
                    hasItemsSale = hasItemsSale,
                    totalSaleItems = totalSaleItems,
                    amountInput = amountInput,
                    restOfAmount = restOfAmount,
                    onGoToItems = onGoToItems,
                    onAmountChanged = onAmountChanged,
                    onSaveInvoice = onSaveInvoice,
                )
            }
        }
    }
    userMessage?.let { message ->
        val snackbarText = stringResource(message)
        LaunchedEffect(snackState, message, snackbarText) {
            snackState.showSnackbar(snackbarText)
            onSnackbarMessageShown()
        }
    }
}

@DevicePreviews
@Composable
fun SaleScreenWithItemsSalePreview() {
    POSTheme {
        PosBackground {
            SaleScreen(
                itemsUiState = ItemsUiState.Success,
                saleItemsState = setOf(
                    Item(
                        name = "item1",
                        unitPrice = 123.0,
                        reorderLevel = 5,
                        quantity = 10,
                        sku = "242342343423",
                    ),
                    Item(
                        name = "item3",
                        unitPrice = 12312.0,
                        quantity = 10,
                        sku = "242342423423",
                    ),
                    Item(
                        name = "item4",
                        unitPrice = 12.0,
                        reorderLevel = 5,
                        quantity = 10,
                        sku = "24233423423",
                    ),
                ),
                totalSaleItems = 0.0,
                searchItemsUiState = SearchItemsUiState.EmptySearch,
                searchQuery = "",
                amountInput = "",
                restOfAmount = 0.0,
                onSearchQueryChanged = {},
                userMessage = null,
                onSnackbarMessageShown = {},
                onScan = {},
                onGoToItems = {},
                onSearchItemClick = { },
                onRemoveItem = {},
                onUpdateQuantity = {},
                onSaveInvoice = {},
                onAmountChanged = {},
            )
        }
    }
}