package com.casecode.pos.ui.item

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.R
import com.casecode.pos.design.component.DynamicAsyncImage
import com.casecode.pos.design.icon.PosIcons
import com.casecode.pos.design.theme.POSTheme
import com.casecode.pos.design.component.PosOutlinedTextField
import com.casecode.pos.utils.ComposeFileProvider
import com.casecode.pos.utils.startScanningBarcode
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
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

    var isScanLauncher by remember { mutableStateOf(false) }
    var isTakeImageOrPick by remember { mutableStateOf(false) }
    var bitmapImage = remember<Bitmap?> { null }
    val snackState = remember { SnackbarHostState() }
    val userMessage = remember { mutableStateOf<Int?>(null) }
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    isScanLauncher = LaunchedScanBarcode(
        isScanLauncher, cameraPermissionState, context,
        onFailureScanEmpty = { message -> userMessage.value = message },
        onResultScan = { barcode.value = it },
    )


    LaunchedTakePictureOrImage(
        isTakeImageOrPick, cameraPermissionState, context,
        onSelectedImageUri = {
            selectedImageUri = it; isTakeImageOrPick = false
            if (isUpdate) viewModel.updateItemImage() },
        onCancelTakeImage = { if (it != null) userMessage.value = it; isTakeImageOrPick = false },
    )

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                if (isUpdate) stringResource(R.string.update_item) else stringResource(
                    R.string.add_item,
                ),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,

                )
        },
        text = {
            Column(
            ) {
                SnackbarHost(hostState = snackState, Modifier)
                userMessage.value?.let { message ->
                    val snackbarText = stringResource(message)
                    Timber.e("snackbarText $snackbarText")
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
                        isTakeImageOrPick = true
                    },
                ) {
                    if (selectedImageUri == Uri.EMPTY) {
                        Icon(
                            Icons.Filled.CameraAlt,
                            stringResource(R.string.item_no_image_selected),
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
                    label = stringResource(R.string.item_name),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = if (nameError.value) stringResource(R.string.item_name_empty) else null,
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
                        label = stringResource(R.string.item_barcode),
                        isError = barcodeError.value,
                        supportingText = if (barcodeError.value) stringResource(R.string.item_barcode_empty) else null,
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
                            isScanLauncher = true

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
                    label = stringResource(R.string.item_price),
                    supportingText = if (priceError.value) stringResource(R.string.item_price_empty) else null,
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
                    label = stringResource(R.string.item_quantity),
                    supportingText = if (quantityError.value) stringResource(R.string.item_quantity_empty) else null,
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
                Text(stringResource(if (isUpdate) R.string.update else R.string.add))
            }
        },
    )

}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun LaunchedTakePictureOrImage(
    isTakeImageOrPick: Boolean,
    cameraPermissionState: PermissionState,
    context: Context,
    onSelectedImageUri: (Uri) -> Unit,
    onCancelTakeImage: (Int?) -> Unit,
) {
    var currentPhotoPath by rememberSaveable { mutableStateOf("") }
    val takePictureOrPickPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null && result.data?.data != null) {
                    result.data?.data?.let { onSelectedImageUri(it) }
                } else {
                    onSelectedImageUri(currentPhotoPath.toUri())
                }

            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                onCancelTakeImage(R.string.item_no_image_selected)
                Timber.i("result for take image or gallery is null")
            }
        },
    )

    LaunchedEffect(isTakeImageOrPick, cameraPermissionState.status) {
        if (isTakeImageOrPick) {
            when {
                cameraPermissionState.status.isGranted -> {
                    val takePicture =
                        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                            takePictureIntent.resolveActivity(context.packageManager)?.also {
                                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                                val uri = ComposeFileProvider.getImageUri(context)
                                currentPhotoPath = uri.toString()
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                            }
                        }

                    val pickPhoto =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    // Create a chooser intent to let the user select between camera and gallery
                    val chooserIntent = Intent.createChooser(
                        pickPhoto,
                        context.getString(R.string.select_image),
                    )
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePicture))
                    takePictureOrPickPhotoLauncher.launch(chooserIntent)
                }

                cameraPermissionState.status.shouldShowRationale -> {
                    cameraPermissionState.launchPermissionRequest()
                }
            }
            onCancelTakeImage(null)

        }
    }
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun LaunchedScanBarcode(
    isScanLauncher: Boolean,
    cameraPermissionState: PermissionState,
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
                    onFailureScanEmpty(R.string.scan_result_empty)
                } else {
                    Timber.e("barcodeResult: $it")
                    onResultScan(it)
                }
                isScanLauncher1 = false
            }
        },
    )

    // Launch activity result request within the effect
    LaunchedEffect(isScanLauncher1, cameraPermissionState.status) {
        if (isScanLauncher1) {
            when {
                cameraPermissionState.status.isGranted -> {
                    scanBarcodeLauncher.launch(
                        ScanOptions().startScanningBarcode(
                            context,
                        ),
                    )
                }

                else -> {
                    if (cameraPermissionState.status.shouldShowRationale) {
                        isScanLauncher1 = false
                        cameraPermissionState.launchPermissionRequest()

                    } else {
                        isScanLauncher1 = false
                        cameraPermissionState.launchPermissionRequest()
                    }
                }
            }

        }
    }
    return isScanLauncher1
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionRequestCameraDialog(
    onPositiveClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.title_permission_dialog)) },
        text = { Text(text = stringResource(id = R.string.message_permission_dialog)) },
        confirmButton = {
            Button(onClick = onPositiveClick) {
                Text(text = stringResource(id = R.string.positive_text_dialog))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
                Manifest.permission.CAMERA
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    POSTheme {
        //ItemDialog{}
        //  ItemDialog(isUpdate = false) {}
    }
}