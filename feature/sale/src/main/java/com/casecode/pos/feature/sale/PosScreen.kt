package com.casecode.pos.feature.sale


import android.content.Context
import android.icu.text.Collator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.DynamicAsyncImage
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.ui.startScanningBarcode
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import timber.log.Timber
import java.text.DecimalFormat


@Composable
internal fun PosScreen(
    viewModel: SaleViewModel = hiltViewModel(),
    onGoToItems: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showUpdateQuantityItem by remember { mutableStateOf(false) }

    var isScanLauncher by remember { mutableStateOf(false) }
    isScanLauncher = launchedScanBarcode(
        isScanLauncher, context,
        onFailureScanEmpty = { message -> viewModel.showSnackbarMessage(message) },
        onResultScan = { viewModel.scanItem(it) },
    )
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
            isScanLauncher = true
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosScreen(
    modifier: Modifier = Modifier,
    uiState: SaleUiState,
    onSearchItemClick: (Item) -> Unit,
    onScan: () -> Unit,
    onGoToItems: () -> Unit,
    onRemoveItem: (Item) -> Unit,
    onUpdateQuantity: (Item) -> Unit,
    onAmountChanged: (String) -> Unit,
    onSaveInvoice: () -> Unit,
) {

    val hasItemsSale by remember(uiState.itemsInvoice) {
        derivedStateOf { uiState.itemsInvoice.isNotEmpty() }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {

        ExposedDropdownMenuBoxSearch(uiState.items, onScan, onSearchItemClick)
        when (uiState.invoiceState) {
            InvoiceState.EmptyItems -> {
                SaleItemsEmpty(modifier = Modifier.weight(1f), onGoToItems)
            }

            InvoiceState.EmptyItemInvoice -> {
                SaleItemsInvoiceEmpty()
            }

            InvoiceState.HasItems -> {
                SaleItems(uiState.itemsInvoice, onRemoveItem, onUpdateQuantity)

            }

        }

        Spacer(modifier = Modifier.height(16.dp))
        AnimatedVisibility(
            visible = hasItemsSale,
            enter = slideInVertically(initialOffsetY = { -40 }) +
                    expandVertically(expandFrom = Alignment.Top) +
                    scaleIn(transformOrigin = TransformOrigin(0.5f, 0f)) +
                    fadeIn(initialAlpha = 0.3f),
            exit = slideOutVertically() +
                    shrinkVertically() +
                    fadeOut() +
                    scaleOut(targetScale = 1.2f),
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
                            ) +
                                    stringResource(
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

@Composable
private fun launchedScanBarcode(
    isScanLauncher: Boolean,
    context: Context,
    onResultScan: (String) -> Unit,
    onFailureScanEmpty: (Int) -> Unit,
): Boolean {
    var isScanLauncher1 = isScanLauncher
    val scanBarcodeLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result ->
            result.contents.let {
                if (it == null) {
                    onFailureScanEmpty(com.casecode.pos.core.ui.R.string.core_ui_scan_result_empty)
                } else {
                    Timber.e("barcodeResult: $it")
                    onResultScan(it)
                }
                isScanLauncher1 = false
            }
        },
    )

    // Launch activity result request within the effect
    LaunchedEffect(isScanLauncher1) {
        if (isScanLauncher1) {

            scanBarcodeLauncher.launch(
                ScanOptions().startScanningBarcode(
                    context, R.string.feature_sale_scan_item_text,
                ),
            )
        }
    }
    return isScanLauncher1
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuBoxSearch(
    items: List<Item>,
    onScan: () -> Unit,
    onSearchItemClick: (Item) -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    val (allowExpanded, setExpanded) = remember { mutableStateOf(false) }
    val filteredItems by remember(searchQuery, items) {
        derivedStateOf {
            if (searchQuery.isBlank()) {

                emptyList()
            } else {
                val collator = Collator.getInstance() // Use the default locale
                collator.strength = Collator.PRIMARY // Case-insensitive and ignores accents
                val normalizedQuery = normalizeString(searchQuery, collator)
                items.filter {
                    val normalizedName = normalizeString(it.name, collator)
                    val normalizedSku = normalizeString(it.sku, collator)

                    collator.compare(normalizedName, normalizedQuery) == 0 ||
                            collator.compare(normalizedSku, normalizedQuery) == 0 ||
                            normalizedName.contains(normalizedQuery) ||
                            normalizedSku.contains(normalizedQuery)

                }
            }
        }
    }
    val expanded = allowExpanded || filteredItems.isNotEmpty()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val keyboardController = LocalSoftwareKeyboardController.current
    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = setExpanded,
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = {
                Text(
                    stringResource(R.string.feature_sale_sale_search_hint),
                    style = MaterialTheme.typography.bodySmall,
                )
            },

            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth()
                .focusRequester(focusRequester),
            maxLines = 1,
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = onScan) {
                    Icon(
                        PosIcons.QrCodeScanner,
                        contentDescription = stringResource(R.string.feature_sale_scan_item_text),
                    )
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    setExpanded(false)
                    keyboardController?.hide()
                    focusManager.clearFocus()
                },
            ),
        )
        if (filteredItems.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { setExpanded(false) },

                ) {
                filteredItems.forEach { item ->
                    DropdownMenuItem(
                        text = { ItemDropMenuItem(item) },
                        onClick = {
                            onSearchItemClick(item)
                            setExpanded(false)
                            searchQuery = ""
                        },

                        )
                }
            }
        }
    }
    LaunchedEffect(expanded) {
        if (expanded) {
            keyboardController?.show()
            focusRequester.requestFocus()
        }
    }
}

private fun normalizeString(input: String, collator: Collator): String {
    return collator.getCollationKey(input.lowercase()).sourceString
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
        Text(
            stringResource(
                com.casecode.pos.core.ui.R.string.core_ui_item_quantity_format,
                item.quantity,
            ),
        )
    }
}

@Composable
private fun SaleItemsEmpty(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {


        Text(
            text = stringResource(id = R.string.feature_sale_items_empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            modifier = Modifier.padding(top = 16.dp),
        )

        Text(
            text = stringResource(id = R.string.feature_sale_items_empty_message),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onClick) { Text(stringResource(R.string.feature_sale_go_to_items_button_text)) }
    }
}

@Composable
fun SaleItemsInvoiceEmpty(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = com.casecode.pos.core.ui.R.drawable.core_ui_ic_outline_inventory_120),
            contentDescription = stringResource(id = R.string.feature_sale_empty_title),
            modifier = Modifier.size(120.dp),
        )

        Text(
            text = stringResource(id = R.string.feature_sale_empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            modifier = Modifier.padding(top = 16.dp),
        )

        Text(
            text = stringResource(id = R.string.feature_sale_empty_message),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
fun ColumnScope.SaleItems(
    itemsInvoice: Set<Item>,
    onRemoveItem: (Item) -> Unit,
    onUpdateQuantity: (Item) -> Unit,
) {
    val scrollableState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 8.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        state = scrollableState,
    ) {
        itemsInvoice.forEach { item ->
            val sku = item.sku
            item(key = sku) {
                ItemSale(
                    name = item.name,
                    price = item.price,
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
fun ItemSale(
    name: String,
    price: Double,
    quantity: Double,
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
                val formattedQuantity = stringResource(
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
                    contentDescription = stringResource(R.string.feature_sale_dialog_delete_invoice_item_title),
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

@Preview(showBackground = true)
@Composable
fun ItemDropMenuItemPreview() {
    POSTheme {
        ItemDropMenuItem(
            Item(
                name = "Iphone5",
                price = 202.0,
                quantity = 12222222220.0,
                sku = "12345678912345",
                unitOfMeasurement = null,
                imageUrl = null,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PosScreenPreview() {
    POSTheme {
        PosScreen(
            uiState = SaleUiState(
                itemsInvoice = mutableSetOf(
                    Item(
                        name = "",
                        price = 0.0,
                        quantity = 0.0,
                        sku = "",
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

@Preview
@Composable
fun SaleItemPreview() {
    POSTheme {
        ItemSale("Item Name", 10.0, 5.0, "", {}, {})
    }
}