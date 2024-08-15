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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.ui.DevicePreviews
import com.casecode.pos.core.ui.scanOptions


@Composable
internal fun PosScreen(
    viewModel: SaleViewModel = hiltViewModel(),
    onGoToItems: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showUpdateQuantityItem by remember { mutableStateOf(false) }
    val snackState = remember { SnackbarHostState() }
    SnackbarHost(hostState = snackState, Modifier.zIndex(1f))
    uiState.userMessage?.let { message ->
        val snackbarText = stringResource(message)
        LaunchedEffect(snackState, viewModel, message, snackbarText) {
            snackState.showSnackbar(snackbarText)
            viewModel.snackbarMessageShown()
        }
    }

    PosScreen(
        uiState = uiState,
        onScan = {
            context.scanOptions(
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
            oldQuantity = uiState.itemInvoiceSelected?.quantity ?: 0.0,
            inStock = uiState.itemSelected?.quantity?.plus(
                uiState.itemInvoiceSelected?.quantity ?: 0.0,
            ) ?: 0.0,
            onDismiss = { showUpdateQuantityItem = false },
            onConfirm = {
                viewModel.updateQuantityItemInvoice(it)
                showUpdateQuantityItem = false
            },
        )
    }
}


@Composable
internal fun PosScreen(
    modifier: Modifier = Modifier,
    uiState: SaleUiState,
    onSearchItemClick: (Item) -> Unit,
    onScan: () -> Unit,
    onGoToItems: () -> Unit,
    onRemoveItem: (Item) -> Unit,
    onUpdateQuantity: (Item) -> Unit,
    onAmountChanged: (String) -> Unit,
    onSaveInvoice: () -> Unit,
    windowSizeClass: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {

    val hasItemsSale by remember(uiState.itemsInvoice) {
        derivedStateOf { uiState.itemsInvoice.isNotEmpty() }
    }
    val configuration = LocalConfiguration.current

    if (!isExpended(windowSizeClass.windowSizeClass, configuration)) {
        Column(modifier = modifier.padding(8.dp)) {
            ExposedDropdownMenuBoxSearch(
                items = uiState.items,
                onScan = onScan,
                onSearchItemClick = onSearchItemClick,
            )
            when (uiState.invoiceState) {
                InvoiceState.Loading -> {
                    PosLoadingWheel(
                        contentDesc = "SaleLoading",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }

                InvoiceState.EmptyItems -> {
                    SaleItemsEmpty(modifier = Modifier.weight(1f), onGoToItems)
                }

                InvoiceState.EmptyItemInvoice -> {
                    SaleItemsInvoiceEmpty()
                }

                InvoiceState.HasItems -> {
                    SaleItems(
                        itemsInvoice = uiState.itemsInvoice,
                        onRemoveItem = onRemoveItem,
                        onUpdateQuantity = onUpdateQuantity,
                    )

                }

            }

            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visible = hasItemsSale,
                enter = slideInVertically(initialOffsetY = { -40 }) + expandVertically(
                    expandFrom = Alignment.Top,
                ) + scaleIn(
                    transformOrigin = TransformOrigin(
                        0.5f,
                        0f,
                    ),
                ) + fadeIn(initialAlpha = 0.3f),
                exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(
                    targetScale = 1.2f,
                ),
            ) {
                Column {
                    OutlinedTextField(
                        value = uiState.amountInput,
                        onValueChange = { onAmountChanged(it) },
                        label = { Text(stringResource(R.string.feature_sale_enter_amount_hint)) },
                        supportingText = {
                            Text(
                                stringResource(
                                    R.string.feature_sale_invoice_total_price_text,
                                    uiState.totalItemsInvoice,
                                ) + stringResource(
                                    R.string.feature_sale_sale_invoice_rest_amount_text,
                                    uiState.restOfAmount,
                                ),
                            )

                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.End),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onSaveInvoice,
                        modifier = Modifier.fillMaxWidth(),

                        ) {
                        Text(stringResource(R.string.feature_sale_button_text))
                    }
                }
            }

        }
    } else {
        Row(
            modifier = modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,

            ) {
            Column(Modifier.weight(0.5f)) {
                ExposedDropdownMenuBoxSearch(
                    items = uiState.items,
                    onScan = onScan,
                    onSearchItemClick = onSearchItemClick,
                )
                when (uiState.invoiceState) {
                    InvoiceState.Loading -> {
                        PosLoadingWheel(
                            contentDesc = "",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )
                    }

                    InvoiceState.EmptyItems -> {
                        SaleItemsEmpty(modifier = Modifier.weight(1f), onGoToItems)
                    }

                    InvoiceState.EmptyItemInvoice -> {
                        SaleItemsInvoiceEmpty()
                    }

                    InvoiceState.HasItems -> {
                        SaleItems(
                            itemsInvoice = uiState.itemsInvoice,
                            onRemoveItem = onRemoveItem,
                            onUpdateQuantity = onUpdateQuantity,
                        )

                    }

                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            AnimatedVisibility(
                modifier = Modifier.weight(0.5f),
                visible = hasItemsSale,
                enter = slideInVertically(initialOffsetY = { -40 }) + expandVertically(
                    expandFrom = Alignment.Top,
                ) + scaleIn(
                    transformOrigin = TransformOrigin(
                        0.5f,
                        0f,
                    ),
                ) + fadeIn(initialAlpha = 0.3f),
                exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(
                    targetScale = 1.2f,
                ),
            ) {
                Column {
                    OutlinedTextField(
                        value = uiState.amountInput,
                        onValueChange = { onAmountChanged(it) },
                        label = { Text(stringResource(R.string.feature_sale_enter_amount_hint)) },
                        supportingText = {
                            Text(
                                stringResource(
                                    R.string.feature_sale_invoice_total_price_text,
                                    uiState.totalItemsInvoice,
                                ) + stringResource(
                                    R.string.feature_sale_sale_invoice_rest_amount_text,
                                    uiState.restOfAmount,
                                ),
                            )

                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.End),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onSaveInvoice,
                        modifier = Modifier.fillMaxWidth(),

                        ) {
                        Text(stringResource(R.string.feature_sale_button_text))
                    }
                }
            }

        }


    }


}

@Composable
fun isExpended(windowSizeClass: WindowSizeClass, configuration: Configuration): Boolean {
    return windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED ||
            configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

@DevicePreviews
@Composable
fun PosScreenPreview() {
    POSTheme {
        PosBackground {
            PosScreen(
                uiState = SaleUiState(
                    invoiceState = InvoiceState.HasItems,
                    itemsInvoice = mutableSetOf(
                        Item(
                            name = "item1",
                            price = 1.0,
                            quantity = 1.0,
                            sku = "23233232",
                            unitOfMeasurement = null,
                            imageUrl = null,
                        ),
                        Item(
                            name = "item2",
                            price = 20.0,
                            quantity = 1.0,
                            sku = "2323232323",
                            unitOfMeasurement = null,
                            imageUrl = null,
                        ),
                        Item(
                            name = "item33",
                            price = 11.0,
                            quantity = 1.0,
                            sku = "23232323",
                            unitOfMeasurement = null,
                            imageUrl = null,
                        ),
                        Item(
                            name = "item22",
                            price = 11.0,
                            quantity = 1.0,
                            sku = "21121212",
                            unitOfMeasurement = null,
                            imageUrl = null,
                        ),
                        Item(
                            name = "item222",
                            price = 10.0,
                            quantity = 0.0,
                            sku = "211111111111",
                            unitOfMeasurement = null,
                            imageUrl = null,
                        ),
                        Item(
                            name = "item2221",
                            price = 11.0,
                            quantity = 2.0,
                            sku = "22223",
                            unitOfMeasurement = null,
                            imageUrl = null,
                        ),

                        ),
                ),
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