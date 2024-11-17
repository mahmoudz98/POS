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
package com.casecode.pos.feature.supplier

import android.content.Context
import android.telephony.TelephonyManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosOutlinedTextField
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.Supplier
import com.casecode.pos.core.ui.parameterprovider.SupplierPreviewParameterProvider
import com.casecode.pos.core.ui.validatePhoneNumber
import com.casecode.pos.core.ui.R.string as uiString

@Composable
fun SupplierDialog(
    viewModel: SupplierViewModel = hiltViewModel(),
    isUpdate: Boolean = false,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val countryIsoCode = telephonyManager.networkCountryIso.uppercase()
    val supplierUpdate =
        if (isUpdate) viewModel.supplierSelected.collectAsStateWithLifecycle() else null
    SupplierDialog(
        isUpdate = isUpdate,
        supplierUpdate = supplierUpdate?.value,
        countryIsoCode = countryIsoCode,
        onAddSupplier = viewModel::addSupplier,
        onUpdateSupplier = viewModel::updateSupplier,
        onDismiss = onDismiss,
    )
}

@Composable
fun SupplierDialog(
    isUpdate: Boolean,
    supplierUpdate: Supplier?,
    countryIsoCode: String,
    onAddSupplier: (Supplier) -> Unit,
    onUpdateSupplier: (Supplier) -> Unit,
    onDismiss: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var name by remember { mutableStateOf(supplierUpdate?.contactName ?: "") }
    var companyName by remember { mutableStateOf(supplierUpdate?.companyName ?: "") }
    var phone by remember { mutableStateOf(supplierUpdate?.contactPhone ?: "") }
    var email by remember { mutableStateOf(supplierUpdate?.contactEmail ?: "") }
    var address by remember { mutableStateOf(supplierUpdate?.address ?: "") }
    var category by remember { mutableStateOf(supplierUpdate?.category ?: "") }
    var nameError by remember { mutableStateOf(false) }
    var companyNameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf<Int?>(null) }
    val onClickTriggered = {
        val validatePhoneNumber = validatePhoneNumber(phone, countryIsoCode)
        if (name.isEmpty() || companyName.isEmpty() || validatePhoneNumber != null) {
            nameError = name.isEmpty()
            companyNameError = companyName.isEmpty()
            phoneError = validatePhoneNumber
        } else {
            val supplier =
                Supplier(
                    contactName = name,
                    companyName = companyName,
                    contactPhone = phone,
                    contactEmail = email,
                    address = address,
                    category = category,
                )
            if (isUpdate) {
                onUpdateSupplier(supplier)
            } else {
                onAddSupplier(supplier)
            }
            keyboardController?.hide()
            onDismiss()
        }
    }
    AlertDialog(
        onDismissRequest = {
            focusRequester.freeFocus()
            keyboardController?.hide()
            onDismiss()
        },
        title = {
            Text(
                stringResource(
                    if (isUpdate) {
                        R.string.feature_supplier_update_title
                    } else {
                        R.string.feature_supplier_add_title
                    },
                ),
            )
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                PosOutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = stringResource(R.string.feature_supplier_name_hint),
                    isError = nameError,
                    keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    supportingText =
                    if (nameError) {
                        stringResource(R.string.feature_supplier_error_name_empty)
                    } else {
                        null
                    },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                )
                PosOutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = stringResource(R.string.feature_supplier_company_name_hint),
                    isError = companyNameError,
                    keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    supportingText =
                    if (companyNameError) {
                        stringResource(R.string.feature_supplier_error_company_name_empty)
                    } else {
                        null
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                PosOutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = stringResource(uiString.core_ui_work_phone_number_hint),
                    isError = phoneError != null,
                    keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    supportingText = phoneError?.let { stringResource(it) },
                    modifier = Modifier.fillMaxWidth(),
                )
                PosOutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = stringResource(R.string.feature_supplier_email_hint),
                    keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                )
                PosOutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = stringResource(R.string.feature_supplier_address_hint),
                    keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                )
                PosOutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = stringResource(R.string.feature_supplier_category_hint),
                    keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            PosTextButton(onClick = { onClickTriggered() }) {
                Text(
                    stringResource(
                        if (isUpdate) {
                            R.string.feature_supplier_update_button_text
                        } else {
                            R.string.feature_supplier_add_button_text
                        },
                    ),
                )
            }
        },
    )
}

@Preview
@Composable
fun SupplierDialogPreview() {
    POSTheme {
        SupplierDialog(
            isUpdate = false,
            supplierUpdate = null,
            countryIsoCode = "US",
            onAddSupplier = {},
            onUpdateSupplier = {},
            onDismiss = {},
        )
    }
}

@Preview
@Composable
fun UpdateSupplierDialogPreview(
    @PreviewParameter(SupplierPreviewParameterProvider::class) suppliers: List<Supplier>,
) {
    POSTheme {
        SupplierDialog(
            isUpdate = true,
            supplierUpdate = suppliers[0],
            countryIsoCode = "US",
            onAddSupplier = {},
            onUpdateSupplier = {},
            onDismiss = {},
        )
    }
}