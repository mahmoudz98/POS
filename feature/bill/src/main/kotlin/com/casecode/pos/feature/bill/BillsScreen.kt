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
package com.casecode.pos.feature.bill

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosEmptyScreen
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.SearchWidgetState
import com.casecode.pos.core.designsystem.icon.PosIcons

@Composable
fun BillsScreen(
    viewModel: BillsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onAddBillClick: () -> Unit,
    onBillClick: (String) -> Unit,
) {
    val uiState = viewModel.billsUiState.collectAsStateWithLifecycle()
    val searchWidgetState = viewModel.searchWidgetState.collectAsStateWithLifecycle()
    val searchQuery = viewModel.searchQuery.collectAsStateWithLifecycle()
    BillsScreen(
        uiState = uiState.value,
        onBackClick = onBackClick,
        onSearchClicked = viewModel::openSearchWidgetState,
        searchQuery = searchQuery.value,
        searchWidgetState = searchWidgetState.value,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onClearRecentSearches = { viewModel.closeSearchWidgetState() },
        onAddBillClick = onAddBillClick,
        onBillClick = {
            onBillClick(it)
        },
    )
}

@Composable
fun BillsScreen(
    uiState: BillsUiState,
    searchWidgetState: SearchWidgetState,
    searchQuery: String,
    onBackClick: () -> Unit,
    onSearchClicked: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onClearRecentSearches: () -> Unit,
    onAddBillClick: () -> Unit,
    onBillClick: (String) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            BillsTopAppBar(
                searchWidgetState = searchWidgetState,
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
                onBackClick = onBackClick,
                onSearchClicked = onSearchClicked,
                onCloseClicked = onClearRecentSearches,
            )
        },
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAddBillClick()
                },
                modifier = Modifier.padding(8.dp),
            ) {
                Icon(
                    imageVector = PosIcons.Add,
                    contentDescription = null,
                )
            }
        },

    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            when (uiState) {
                is BillsUiState.Loading -> {
                    PosLoadingWheel(
                        modifier =
                        Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                        contentDesc = "LoadingSupplierInvoices",
                    )
                }

                is BillsUiState.Success -> BillsList(
                    bills = uiState.supplierInvoices,
                    onBillClick = onBillClick,
                )

                is BillsUiState.Error, is BillsUiState.Empty -> {
                    PosEmptyScreen(
                        Modifier.align(Alignment.CenterHorizontally),
                        com.casecode.pos.core.ui.R.drawable.core_ui_ic_outline_inventory_120,
                        R.string.feature_bill_empty_title,
                        R.string.feature_bill_empty_message,
                    )
                }
            }
        }
    }
}