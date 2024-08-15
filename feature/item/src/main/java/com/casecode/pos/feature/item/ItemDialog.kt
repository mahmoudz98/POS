package com.casecode.pos.feature.item

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.DynamicAsyncImage
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.ui.scanOptions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import timber.log.Timber

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ItemDialog(
    modifier: Modifier = Modifier,
    isUpdate: Boolean = false,
    viewModel: ItemsViewModel,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current

    val configuration = LocalConfiguration.current
    val itemUpdated = if (isUpdate) viewModel.itemSelected.collectAsStateWithLifecycle() else null

    val name = rememberSaveable { mutableStateOf(itemUpdated?.value?.name ?: "") }
    val price = rememberSaveable { mutableStateOf(itemUpdated?.value?.price?.toString() ?: "") }
    val quantity =
        rememberSaveable { mutableStateOf(itemUpdated?.value?.quantity?.toString() ?: "") }
    val barcode = rememberSaveable { mutableStateOf(itemUpdated?.value?.sku ?: "") }
    var selectedImageUri by rememberSaveable {
        mutableStateOf(itemUpdated?.value?.imageUrl?.toUri() ?: Uri.EMPTY)
    }

    val nameError = remember { mutableStateOf(false) }
    val priceError = remember { mutableStateOf(false) }
    val quantityError = remember { mutableStateOf(false) }
    val barcodeError = remember { mutableStateOf(false) }

    var showTakeOrPickImage by remember { mutableStateOf(false) }
    var bitmapImage = remember<Bitmap?> { null }
    val snackState = remember { SnackbarHostState() }
    val userMessage = remember { mutableStateOf<Int?>(null) }
    LaunchedTakePictureOrImage(
        showTakeOrPickImage,
        context,
        onSelectedImageUri = {
            selectedImageUri = it; showTakeOrPickImage = false
            if (isUpdate) viewModel.updateItemImage()
        },
        onCancelTakeImage = {
            if (it != null) userMessage.value = it; showTakeOrPickImage = false
        },
    )



    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (isUpdate) stringResource(R.string.feature_item_update_item_button_text) else stringResource(
                    R.string.feature_item_add_item_action_text,
                ),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,

                )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                SnackbarHost(hostState = snackState, Modifier)
                userMessage.value?.let { message ->
                    val snackbarText = stringResource(message)
                    LaunchedEffect(snackState, viewModel, message, snackbarText) {
                        snackState.showSnackbar(snackbarText)
                        viewModel.snackbarMessageShown()
                    }
                }

                IconButton(
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.CenterHorizontally),
                    onClick = {
                        showTakeOrPickImage = true
                    },
                ) {
                    if (selectedImageUri == Uri.EMPTY) {
                        Icon(
                            Icons.Filled.CameraAlt,
                            stringResource(R.string.feature_item_error_no_image_selected),
                        )
                    } else {
                        selectedImageUri?.let {
                            DynamicAsyncImage(
                                imageUrl = it,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                onSuccess = { image -> bitmapImage = image },
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                PosOutlinedTextField(
                    value = name.value,
                    onValueChange = { name.value = it; nameError.value = it.isEmpty() },
                    isError = nameError.value,
                    label = stringResource(R.string.feature_item__name_hint),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = if (nameError.value) stringResource(R.string.feature_item_error_item_name_empty) else null,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(

                    modifier = Modifier.fillMaxWidth(),
                ) {
                    PosOutlinedTextField(
                        enabled = isUpdate.not(),
                        value = barcode.value,
                        onValueChange = {
                            barcode.value = it; barcodeError.value = it.isEmpty()
                        },
                        label = stringResource(R.string.feature_item__barcode_hint),
                        isError = barcodeError.value,
                        supportingText = if (barcodeError.value) stringResource(R.string.feature_item_error_item_barcode_empty) else null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                        ),

                        modifier = Modifier.weight(3f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        enabled = isUpdate.not(),
                        modifier = Modifier
                            .weight(.5f)
                            .align(Alignment.CenterVertically),
                        onClick = {
                            context.scanOptions(
                                onResult = {
                                    barcode.value = it

                                },
                                onFailure = {
                                    userMessage.value = it
                                },
                                onCancel = { userMessage.value = it },
                            )

                        },
                    ) {
                        Icon(PosIcons.QrCodeScanner, "Close")
                    }

                }

                Spacer(modifier = Modifier.height(8.dp))
                PosOutlinedTextField(
                    value = price.value,
                    onValueChange = { price.value = it; priceError.value = it.isEmpty() },
                    isError = priceError.value,
                    label = stringResource(R.string.feature_item_price_hint),
                    supportingText = if (priceError.value) stringResource(R.string.feature_item_error_item_price_empty) else null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                PosOutlinedTextField(
                    value = quantity.value,
                    onValueChange = {
                        quantity.value = it; quantityError.value = it.isEmpty()
                    },
                    isError = quantityError.value,
                    label = stringResource(R.string.feature_item_item_quantity_label),
                    supportingText = if (quantityError.value) stringResource(R.string.feature_item_error_item_quantity_empty) else null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.value.isEmpty() || price.value.isEmpty() || quantity.value.isEmpty() || barcode.value.isEmpty()) {
                        nameError.value = name.value.isEmpty()
                        priceError.value = price.value.isEmpty()
                        quantityError.value = quantity.value.isEmpty()
                        barcodeError.value = barcode.value.isEmpty()
                    } else {
                        Timber.e("selectedImageUri= $bitmapImage")
                        if (isUpdate) {
                            viewModel.checkNetworkAndUpdateItem(
                                name.value,
                                price.value.toDouble(),
                                quantity.value.toDouble(),
                                barcode.value,
                                bitmapImage,
                            )
                        } else {

                            viewModel.checkNetworkAndAddItem(
                                name.value,
                                price.value.toDouble(),
                                quantity.value.toDouble(),
                                barcode.value,
                                bitmapImage,
                            )
                        }
                        onDismiss()

                    }

                },
            ) {
                Text(stringResource(if (isUpdate) R.string.feature_item_update_item_button_text else R.string.feature_item_add_item_action_text))
            }
        },
    )

}


@Composable
@OptIn(ExperimentalPermissionsApi::class)
internal fun LaunchedTakePictureOrImage(
    showTakeOrPickImage: Boolean,
    context: Context,
    onSelectedImageUri: (Uri) -> Unit,
    onCancelTakeImage: (Int?) -> Unit,
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var currentPhotoPath by rememberSaveable { mutableStateOf("") }
    val takePictureOrPickPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { onSelectedImageUri(it) } ?: run {
                onSelectedImageUri(currentPhotoPath.toUri())
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            onCancelTakeImage(R.string.feature_item_error_no_image_selected)
            Timber.i("Image selection canceled")
        }
    }

    LaunchedEffect(showTakeOrPickImage, cameraPermissionState.status) {
        if (showTakeOrPickImage) {
            when (cameraPermissionState.status) {
                is PermissionStatus.Denied -> {
                    cameraPermissionState.launchPermissionRequest()
                }

                PermissionStatus.Granted -> {
                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                        resolveActivity(context.packageManager)?.also {
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            val uri = PhotoUriManager(context).buildNewUri()
                            currentPhotoPath = uri.toString()
                            putExtra(MediaStore.EXTRA_OUTPUT, uri)
                        }
                    }

                    val pickPhoto = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    )

                    val chooserIntent = Intent.createChooser(
                        pickPhoto,
                        context.getString(R.string.feature_item_dialog_select_image_text),
                    ).apply {
                        putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePicture))
                    }
                    takePictureOrPickPhotoLauncher.launch(chooserIntent)
                }
            }
        }
    }
}