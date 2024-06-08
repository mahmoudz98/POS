package com.casecode.pos.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.casecode.data.utils.encodeAsBitmap
import com.casecode.domain.model.users.Item
import com.casecode.pos.R
import com.casecode.pos.design.component.PosTextButton
import com.casecode.pos.design.theme.POSTheme
import com.casecode.pos.design.component.PosOutlinedTextField

@Composable
fun QRCodePrintItemDialog(viewModel: ItemsViewModel, onDismiss: () -> Unit) {
    val itemPrint = viewModel.itemSelected.collectAsStateWithLifecycle()
    QRCodePrintItemDialog(itemPrint = itemPrint.value, onDismiss = onDismiss)
}

@Composable
internal fun QRCodePrintItemDialog(
    itemPrint: Item?,
    onDismiss: () -> Unit,
) {

    val configuration = LocalConfiguration.current
    val name = rememberSaveable { mutableStateOf(itemPrint?.name) }
    val barcode = rememberSaveable { mutableStateOf(itemPrint?.sku) }
    val price = rememberSaveable { mutableStateOf(itemPrint?.price) }
    var printedItemCount by rememberSaveable { mutableIntStateOf(1) }
    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                stringResource(R.string.print_qr_code_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Column {
                PosOutlinedTextField(
                    value = printedItemCount.toString(),
                    onValueChange = { newValue -> printedItemCount = newValue.toIntOrNull() ?: 1 },
                    isError = printedItemCount < 1,
                    label = stringResource(R.string.copies),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = if (printedItemCount < 1) stringResource(R.string.copies_empty) else null,
                )
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(120.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(barcode.value?.encodeAsBitmap()).crossfade(true).build(),
                    placeholder = painterResource(R.drawable.baseline_qr_code_scanner_24),
                    contentDescription = null,
                    colorFilter = null,
                )
                Text(
                    text = name.value ?: "",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = barcode.value.toString(),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    price.value.toString(),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            PosTextButton(
                onClick = {
                    //TODO: handle print item
                },
            ) {
                Text(stringResource(R.string.print_text))
            }
        },
        dismissButton = {
            PosTextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

@Preview
@Composable
fun PreviewQRCodePrintItemDialog() {
    POSTheme {
        QRCodePrintItemDialog(
            Item(
                name = "Janine Whitfield",
                price = 4.5,
                quantity = 6.7,
                sku = "12345666",
                unitOfMeasurement = null,
                imageUrl = null,
            ),
        ) { }
    }
}