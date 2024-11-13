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
import com.casecode.pos.core.designsystem.component.SearchWidgetState
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.Supplier
import com.casecode.pos.core.ui.DeleteDialog
import com.casecode.pos.core.ui.DevicePreviews
import com.casecode.pos.core.ui.SupplierPreviewParameterProvider
import com.casecode.pos.core.ui.R as uiR

@Composable
fun SupplierScreen(viewModel: SupplierViewModel = hiltViewModel(), onBackClick: () -> Unit) {
    val supplierUiState by viewModel.suppliersUiState.collectAsStateWithLifecycle()
    val filteredSuppliers by viewModel.filteredSuppliersUiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchWidgetState by viewModel.searchWidgetState.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    var showSupplierDialog by remember { mutableStateOf(false) }
    var showUpdateSupplierDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val countryIsoCode = telephonyManager.networkCountryIso.uppercase()

    SupplierScreen(
        modifier = Modifier,
        supplierUiState = supplierUiState,
        filteredSuppliers = filteredSuppliers,
        searchQuery = searchQuery,
        searchWidgetState = searchWidgetState,
        userMessage = userMessage,
        countryIsoCode = countryIsoCode,
        onBackClick = onBackClick,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onSearchClicked = viewModel::openSearchWidgetState,
        onClearRecentSearches = viewModel::closeSearchWidgetState,
        onMessageShown = viewModel::snackbarMessageShown,
        onAddSupplierClick = { showSupplierDialog = true },
        onSupplierClick = {
            viewModel.onSelectSupplier(it)
            showUpdateSupplierDialog = true
        },
        onDeleteSupplierClick = {
            viewModel.onSelectSupplier(it)
            showDeleteDialog = true
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
    if (showDeleteDialog) {
        DeleteDialog(
            titleRes = R.string.feature_supplier_dialog_delete_title,
            messageRes = R.string.feature_supplier_dialog_delete_message,
            onConfirm = {
                viewModel.deleteSupplier()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierScreen(
    modifier: Modifier = Modifier,
    supplierUiState: SuppliersUiState,
    filteredSuppliers: List<Supplier>,
    searchWidgetState: SearchWidgetState = SearchWidgetState.CLOSED,
    searchQuery: String,
    userMessage: Int?,
    countryIsoCode: String,
    onBackClick: () -> Unit,
    onSearchClicked: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onClearRecentSearches: () -> Unit,
    onMessageShown: () -> Unit,
    onAddSupplierClick: () -> Unit,
    onSupplierClick: (Supplier) -> Unit,
    onDeleteSupplierClick: (Supplier) -> Unit,
) {
    val snackState = remember { SnackbarHostState() }
    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            SupplierTopAppBar(
                searchWidgetState = searchWidgetState,
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
                onBackClick = onBackClick,
                onSearchClicked = onSearchClicked,
                onCloseClicked = onClearRecentSearches,
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
                        contentDesc = "LoadingSuppliers",
                    )
                }

                is SuppliersUiState.Empty, is SuppliersUiState.Error -> {
                    SupplierEmptyScreen()
                }

                is SuppliersUiState.Success -> {
                    SupplierCardList(
                        filteredSuppliers,
                        countryIsoCode,
                        onSupplierClick = onSupplierClick,
                        onSupplierDelete = onDeleteSupplierClick,
                    )
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
fun SupplierScreenLoadingPreview() {
    POSTheme {
        PosBackground {
            SupplierScreen(
                supplierUiState = SuppliersUiState.Loading,
                filteredSuppliers = emptyList(),
                searchWidgetState = SearchWidgetState.CLOSED,
                searchQuery = "",
                userMessage = null,
                countryIsoCode = "EG",
                onBackClick = {},
                onSearchClicked = {},
                onSearchQueryChanged = {},
                onClearRecentSearches = {},
                onMessageShown = {},
                onAddSupplierClick = {},
                onSupplierClick = {},
                onDeleteSupplierClick = {},
            )
        }
    }
}

@ExperimentalMaterial3Api
@DevicePreviews
@Composable
fun SupplierScreenSuccessPreview(
    @PreviewParameter(SupplierPreviewParameterProvider::class)
    suppliers: List<Supplier>,
) {
    POSTheme {
        PosBackground {
            SupplierScreen(
                supplierUiState = SuppliersUiState.Success(suppliers),
                filteredSuppliers = suppliers,
                searchWidgetState = SearchWidgetState.CLOSED,
                userMessage = null,
                searchQuery = "",
                countryIsoCode = "EG",
                onBackClick = {},
                onSearchClicked = {},
                onSearchQueryChanged = {},
                onClearRecentSearches = {},
                onMessageShown = {},
                onAddSupplierClick = {},
                onSupplierClick = {},
                onDeleteSupplierClick = {},
            )
        }
    }
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun SupplierScreenEmptyPreview() {
    POSTheme {
        PosBackground {
            SupplierScreen(
                supplierUiState = SuppliersUiState.Empty,
                filteredSuppliers = emptyList(),
                searchWidgetState = SearchWidgetState.CLOSED,
                searchQuery = "",
                userMessage = null,
                countryIsoCode = "EG",
                onBackClick = {},
                onSearchClicked = {},
                onSearchQueryChanged = {},
                onClearRecentSearches = {},
                onMessageShown = {},
                onAddSupplierClick = {},
                onSupplierClick = {},
                onDeleteSupplierClick = {},
            )
        }
    }
}