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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.Supplier
import com.casecode.pos.core.ui.SupplierPreviewParameterProvider
import com.casecode.pos.core.ui.R as uiR

@Composable
fun SupplierScreen(viewModel: SupplierViewModel = hiltViewModel(), onBackClick: () -> Unit) {
    val supplierUiState by viewModel.suppliersUiState.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    var showSupplierDialog by remember { mutableStateOf(false) }
    var showUpdateSupplierDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val countryIsoCode = telephonyManager.networkCountryIso.uppercase()

    SupplierScreen(
        supplierUiState = supplierUiState,
        modifier = Modifier,
        userMessage = userMessage,
        countryIsoCode = countryIsoCode,
        onBackClick = onBackClick,
        onMessageShown = viewModel::snackbarMessageShown,
        onAddSupplierClick = { showSupplierDialog = true },
        onSupplierClick = {
            viewModel.onSelectSupplier(it)
            showUpdateSupplierDialog = true
        },
    )
    if (showSupplierDialog) {
        SupplierDialog(onDismiss = { showSupplierDialog = false })
    }
    if (showUpdateSupplierDialog) {
        SupplierDialog(
            viewModel = viewModel,
            isUpdate = true,
            onDismiss = { showUpdateSupplierDialog = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierScreen(
    supplierUiState: SuppliersUiState,
    modifier: Modifier = Modifier,
    userMessage: Int?,
    countryIsoCode: String,
    onBackClick: () -> Unit,
    onMessageShown: () -> Unit,
    onAddSupplierClick: () -> Unit,
    onSupplierClick: (Supplier) -> Unit,
) {
    val snackState = remember { SnackbarHostState() }
    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            PosTopAppBar(
                navigationIcon = PosIcons.ArrowBack,
                titleRes = R.string.feature_supplier_title,
                onNavigationClick = { onBackClick() },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackState)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddSupplierClick() },
                modifier = Modifier.padding(16.dp),
            ) {
                Icon(
                    imageVector = PosIcons.Add,
                    contentDescription = stringResource(
                        R.string.feature_supplier_add_success_message,
                    ),
                )
            }
        },
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            when (supplierUiState) {
                is SuppliersUiState.Loading -> {
                    PosLoadingWheel(
                        modifier =
                        modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                        contentDesc = "LoadingEmployees",
                    )
                }

                is SuppliersUiState.Empty, is SuppliersUiState.Error -> {
                    SupplierEmptyScreen()
                }

                is SuppliersUiState.Success -> {
                    SupplierCardList(supplierUiState.suppliers, countryIsoCode) {
                        onSupplierClick(it)
                    }
                }
            }
        }
    }
    userMessage?.let { message ->
        val snackbarText = stringResource(message)
        LaunchedEffect(message) {
            snackState.showSnackbar(snackbarText)
            onMessageShown()
        }
    }
}

@Composable
fun SupplierEmptyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier =
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = uiR.drawable.core_ui_ic_outline_inventory_120),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
        )

        Text(
            text = stringResource(id = R.string.feature_supplier_empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            modifier = Modifier.padding(top = 16.dp),
        )

        Text(
            text = stringResource(id = R.string.feature_supplier_empty_message),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun SupplierScreenPreview(
    @PreviewParameter(SupplierPreviewParameterProvider::class)
    suppliers: List<Supplier>,
) {
    POSTheme {
        PosBackground {
            SupplierScreen(
                supplierUiState = SuppliersUiState.Success(suppliers),
                userMessage = null,
                countryIsoCode = "20",
                onBackClick = {},
                onMessageShown = {},
                onAddSupplierClick = {},
                onSupplierClick = {},
            )
        }
    }
}