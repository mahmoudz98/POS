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
package com.casecode.pos.feature.item.details

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.recalculateWindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.DynamicAsyncImage
import com.casecode.pos.core.designsystem.component.PermissionDialog
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosExposeDropdownMenuBox
import com.casecode.pos.core.designsystem.component.PosInputChip
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.ui.DevicePreviews
import com.casecode.pos.core.ui.TrackScreenViewEvent
import com.casecode.pos.core.ui.scanOptions
import com.casecode.pos.feature.item.ItemsViewModel
import com.casecode.pos.feature.item.R
import com.casecode.pos.feature.item.utils.PhotoUriManager
import com.casecode.pos.feature.item.utils.rememberCurrencyVisualTransformation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.casecode.pos.core.ui.R.string as uiString

@Composable
fun AddOrUpdateItemScreen(
    isUpdate: Boolean = false,
    onNavigateBack: () -> Unit,
    viewModel: ItemsViewModel = hiltViewModel(),
) {
    val itemUpdated = if (isUpdate) viewModel.itemSelected.collectAsStateWithLifecycle() else null
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val categories by viewModel.categoriesUiState.collectAsStateWithLifecycle()

    AddOrUpdateItemScreen(
        isUpdate = isUpdate,
        onNavigateBack = onNavigateBack,
        itemUpdated = itemUpdated?.value,
        categories = categories,
        onUpdateImageItem = viewModel::updateItemImage,
        userMessage = userMessage,
        showSnackbarMessage = viewModel::showSnackbarMessage,
        onMessageSnackbarShown = viewModel::snackbarMessageShown,
        onAddItem = viewModel::checkNetworkAndAddItem,
        onUpdateItem = viewModel::checkNetworkAndUpdateItem,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddOrUpdateItemScreen(
    modifier: Modifier = Modifier,
    isUpdate: Boolean,
    itemUpdated: Item?,
    categories: Set<String>,
    userMessage: Int?,
    onNavigateBack: () -> Unit,
    onUpdateImageItem: () -> Unit,
    showSnackbarMessage: (Int) -> Unit,
    onMessageSnackbarShown: () -> Unit,
    onAddItem: (Item, Bitmap?) -> Unit,
    onUpdateItem: (Item, Bitmap?) -> Unit,
) {
    TrackScreenViewEvent(screenName = "AddOrUpdateItem")

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val itemInputState = rememberItemInputState(itemUpdated)
    val snackbarHostState = remember { SnackbarHostState() }
    var showTakeOrPickImage by remember { mutableStateOf(false) }
    var bitmapImage = remember<Bitmap?> { null }

    val onSaveTriggered = {
        if (itemInputState.hasValidateInput()) {
            val item =
                Item(
                    name = itemInputState.name,
                    category = itemInputState.category,
                    unitPrice = itemInputState.price.toDouble(),
                    costPrice =
                        itemInputState.costPrice
                            .takeIf {
                                it.isNotBlank()
                            }?.toDouble() ?: 0.0,
                    quantity = itemInputState.quantity.takeIf { it.isNotBlank() }?.toInt() ?: 0,
                    qtyPerPack = itemInputState.qtyPerPack,
                    reorderLevel = itemInputState.reorderLevel,
                    sku = itemInputState.sku,
                )
            if (isUpdate) {
                onUpdateItem(item, bitmapImage)
            } else {
                onAddItem(item, bitmapImage)
            }
            onNavigateBack()
        }
        keyboardController?.hide()
    }
    Scaffold(
        topBar = {
            PosTopAppBar(
                titleRes = if (isUpdate) R.string.feature_item_update_item_title_text else R.string.feature_item_add_item_title_text,
                navigationIcon = PosIcons.ArrowBack,
                navigationIconContentDescription = null,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                action = {
                    PosTextButton(
                        onClick = { onSaveTriggered() },
                    ) { Text(text = stringResource(R.string.feature_item_save_action_text)) }
                },
                onNavigationClick = onNavigateBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier =
            Modifier
                .focusRequester(focusRequester)
                .focusProperties {
                    enter = { focusRequester }
                    exit = { FocusRequester.Cancel }
                },
    ) { innerPadding ->
        Box(
            modifier
                .fillMaxSize()
                .padding(innerPadding)
                .recalculateWindowInsets(),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .navigationBarsPadding()
                        .imePadding()
                        .verticalScroll(rememberScrollState()),
            ) {
                DynamicAsyncImage(
                    imageUrl = itemInputState.selectedImageUri,
                    modifier =
                        Modifier
                            .size(64.dp)
                            .align(Alignment.CenterHorizontally)
                            .clickable { showTakeOrPickImage = true },
                    placeholder = PosIcons.AddPhoto,
                    contentDescription = null,
                    onSuccess = { image -> bitmapImage = image },
                )

                Spacer(modifier = Modifier.height(16.dp))
                PosOutlinedTextField(
                    value = itemInputState.name,
                    onValueChange = itemInputState::onNameChange,
                    isError = itemInputState.nameError,
                    label = stringResource(R.string.feature_item_name_hint),
                    keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions =
                    KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    ),
                    supportingText = if (itemInputState.nameError) stringResource(R.string.feature_item_error_name_empty) else null,
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .testTag("nameOutlinedTextField"),
                )
                PosExposeDropdownMenuBox(
                    label = stringResource(R.string.feature_item_category_hint),
                    currentText = itemInputState.category,
                    items = categories.toList(),
                    onClickItem = itemInputState::onCategoryChange,
                    menuAnchorType = MenuAnchorType.PrimaryEditable,
                    readOnly = false,
                    keyboardOption =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    onKeyboardAction = { focusManager.moveFocus(FocusDirection.Down) },
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    PosOutlinedTextField(
                        enabled = isUpdate.not(),
                        value = itemInputState.sku,
                        onValueChange = itemInputState::onSkuChange,
                        label = stringResource(R.string.feature_item_barcode_hint),
                        isError = itemInputState.skuError != null,
                        supportingText = itemInputState.skuError?.let { stringResource(it) },
                        keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                        ),
                        keyboardActions =
                        KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                        ),
                        modifier = Modifier.weight(0.9f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        enabled = isUpdate.not(),
                        modifier =
                        Modifier
                            .weight(0.1f)
                            .align(Alignment.CenterVertically),
                        onClick = {
                            context.scanOptions(
                                onModuleDownloaded = { showSnackbarMessage(it) },
                                onModuleDownloading = { showSnackbarMessage(it) },
                                onResult = itemInputState::onSkuChange,
                                onFailure = { showSnackbarMessage(it) },
                                onCancel = { showSnackbarMessage(it) },
                            )
                        },
                    ) { Icon(PosIcons.QrCodeScanner, "Scan barcode") }
                }
                PriceItemContent(
                    itemInputState.price,
                    itemInputState.priceError,
                    itemInputState.costPrice,
                    itemInputState.costPriceError,
                    onPriceChange = itemInputState::onUnitPriceChange,
                    onCostPriceChange = itemInputState::onCostPriceChange,
                )
                TrackQuantityContent(
                    trackSelected = itemInputState.isTrackSelected,
                    reorderLevel = itemInputState.reorderLevel.toString(),
                    quantity = itemInputState.quantity,
                    quantityError = itemInputState.quantityError,
                    qtyPerPack = itemInputState.qtyPerPack.toString(),
                    onSelectedChange = itemInputState::onReorderTrack,
                    onQuantityChange = itemInputState::onQuantityChange,
                    onReorderLevelChange = itemInputState::onReorderLevelChange,
                    onQtyPerPackChange = itemInputState::onQtyPerPackChange,
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    LaunchedTakePictureOrImage(
        showTakeOrPickImage = showTakeOrPickImage,
        context = context,
        onSelectedImageUri = {
            itemInputState.onSelectImage(it)
            showTakeOrPickImage = false
            if (isUpdate) {
                onUpdateImageItem()
            }
        },
        onCancelTakeImage = {
            if (it != null) showSnackbarMessage(it)
            showTakeOrPickImage = false
        },
    )
    userMessage?.let { message ->
        val snackbarText = stringResource(message)
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(snackbarText)
            onMessageSnackbarShown()
        }
    }
}

@Composable
private fun PriceItemContent(
    price: String,
    priceError: Int?,
    costPrice: String,
    costPriceError: Int?,
    onPriceChange: (String) -> Unit,
    onCostPriceChange: (String) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val currencyVisualTransformation =
        rememberCurrencyVisualTransformation(configuration.locales[0])
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        PosOutlinedTextField(
            value = price,
            onValueChange = {
                onPriceChange(it)
            },
            isError = priceError != null,
            label = stringResource(R.string.feature_item_price_hint),
            visualTransformation = currencyVisualTransformation,
            supportingText = priceError?.let { stringResource(it) },
            keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
            modifier =
            Modifier
                .padding(end = 8.dp)
                .weight(0.5f),
        )
        PosOutlinedTextField(
            value = costPrice,
            onValueChange = onCostPriceChange,
            isError = costPriceError != null,
            label = stringResource(R.string.feature_item_cost_price_hint),
            visualTransformation = currencyVisualTransformation,
            supportingText = costPriceError?.let { stringResource(it) },
            keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            modifier = Modifier.weight(0.5f),
        )
    }
}

@Composable
private fun TrackQuantityContent(
    trackSelected: Boolean,
    reorderLevel: String,
    quantity: String,
    quantityError: Boolean,
    qtyPerPack: String,
    onSelectedChange: (Boolean) -> Unit,
    onQuantityChange: (String) -> Unit,
    onReorderLevelChange: (String) -> Unit,
    onQtyPerPackChange: (String) -> Unit,
) {
    PosInputChip(
        selected = trackSelected,
        onSelectedChange = onSelectedChange,
        modifier = Modifier.padding(end = 24.dp),
    ) {
        if (trackSelected) {
            Text(stringResource(R.string.feature_item_tracked_stock_chip_label))
        } else {
            Text(stringResource(R.string.feature_item_not_track_stock_chip_label))
        }
    }
    AnimatedContent(
        targetState = trackSelected,
        transitionSpec = {
            expandVertically(animationSpec = tween(200, 150)) togetherWith
                    shrinkVertically(animationSpec = tween(150, 150))
        },
        label = "size transform",
    ) { targetExpanded ->
        if (targetExpanded) {
            Column(Modifier.wrapContentSize()) {
                PosOutlinedTextField(
                    value = quantity,
                    onValueChange = {
                        onQuantityChange(it)
                    },
                    isError = quantityError,
                    label = stringResource(uiString.core_ui_item_quantity_label),
                    supportingText = if (quantityError) stringResource(R.string.feature_item_error_quantity_empty) else null,
                    keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    PosOutlinedTextField(
                        value = reorderLevel,
                        onValueChange = {
                            onReorderLevelChange(it)
                        },
                        label = stringResource(uiString.core_ui_item_reorder_level_label),
                        keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                        ),
                        modifier =
                        Modifier
                            .padding(end = 8.dp)
                            .weight(0.5f),
                    )
                    PosOutlinedTextField(
                        value = qtyPerPack,
                        onValueChange = { onQtyPerPackChange(it) },
                        label = stringResource(uiString.core_ui_item_qty_per_pack_label),
                        keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done,
                        ),
                        modifier = Modifier.weight(0.5f),
                    )
                }
            }
        }
    }
}

// Issue: not open dialog permission when cancel outside dialog permission
@Composable
@OptIn(ExperimentalPermissionsApi::class)
internal fun LaunchedTakePictureOrImage(
    showTakeOrPickImage: Boolean,
    context: Context,
    onSelectedImageUri: (Uri) -> Unit,
    onCancelTakeImage: (Int?) -> Unit,
) {
    if (LocalInspectionMode.current) return

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var currentPhotoPath by rememberSaveable { mutableStateOf("") }
    var showPermissionDialog by remember { mutableStateOf(false) }
    val takePictureOrPickPhotoLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { onSelectedImageUri(it) } ?: run {
                    onSelectedImageUri(currentPhotoPath.toUri())
                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                onCancelTakeImage(R.string.feature_item_error_no_image_selected)
            }
        }
    if (showPermissionDialog) {
        PermissionDialog(
            onDismiss = {
                showPermissionDialog = false
                onCancelTakeImage(null)
            },
            messagePermission = R.string.feature_item_need_permission_for_camera,
        )
    }
    LaunchedEffect(showTakeOrPickImage, cameraPermissionState.status) {
        if (showTakeOrPickImage) {
            val status = cameraPermissionState.status
            if (status.isGranted) {
                val takePicture =
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        val uri = PhotoUriManager(context).buildNewUri()
                        currentPhotoPath = uri.toString()
                        putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    }

                val pickPhoto =
                    Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    )

                val chooserIntent =
                    Intent
                        .createChooser(
                            pickPhoto,
                            context.getString(R.string.feature_item_dialog_select_image_text),
                        ).apply {
                            putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePicture))
                        }
                takePictureOrPickPhotoLauncher.launch(chooserIntent)
            } else if (status is PermissionStatus.Denied && !status.shouldShowRationale) {
                cameraPermissionState.launchPermissionRequest()
            } else {
                showPermissionDialog = true
            }
        }
    }
}

@DevicePreviews
@Composable
fun AddItemPreview() {
    POSTheme {
        PosBackground {
            AddOrUpdateItemScreen(
                isUpdate = false,
                itemUpdated = null,
                categories = setOf(),
                onNavigateBack = {},
                onUpdateImageItem = {},
                userMessage = null,
                showSnackbarMessage = {},
                onMessageSnackbarShown = {},
                onAddItem = { _, _ -> },
                onUpdateItem = { _, _ -> },
            )
        }
    }
}

@DevicePreviews
@Composable
fun UpdateItemPreview() {
    POSTheme {
        PosBackground {
            AddOrUpdateItemScreen(
                isUpdate = true,
                itemUpdated =
                Item(
                    name = "Product",
                    category = "Category",
                    costPrice = 123454678.0,
                    unitPrice = 123456789.4,
                    reorderLevel = 5,
                    quantity = 20,
                    qtyPerPack = 12,
                    sku = "1231231",
                ),
                categories = setOf(),
                onNavigateBack = {},
                onUpdateImageItem = {},
                userMessage = null,
                showSnackbarMessage = {},
                onMessageSnackbarShown = {},
                onAddItem = { _, _ -> },
                onUpdateItem = { _, _ -> },
            )
        }
    }
}