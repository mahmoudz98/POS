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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosEmptyScreen
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.SearchWidgetState
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.ui.DevicePreviews
import com.casecode.pos.core.ui.parameterprovider.SupplierInvoiceParameterProvider
import com.casecode.pos.core.ui.R as uiR

@Composable
fun BillsScreen(
    viewModel: BillsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onAddBillClick: () -> Unit,
    onBillClick: (String) -> Unit,
) {
    val uiState by viewModel.billsUiState.collectAsStateWithLifecycle()
    val searchWidgetState by viewModel.searchWidgetState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val filterUiState by viewModel.filterUiState.collectAsStateWithLifecycle()
    BillsScreen(
        uiState = uiState,
        searchQuery = searchQuery,
        searchWidgetState = searchWidgetState,
        filterUiState = filterUiState,
        userMessage = userMessage,
        onBillsEffect = {
            when (it) {
                is BillsEffect.NavigateToDetail -> onBillClick(it.billId)
                is BillsEffect.NavigateBack -> onBackClick()
                is BillsEffect.NavigateToAdd -> onAddBillClick()
            }
        },
        onBillsUiEvent = viewModel::onBillsUiEvent,
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BillsScreen(
    uiState: BillsUiState,
    searchWidgetState: SearchWidgetState,
    searchQuery: String,
    filterUiState: BillsFilterUiState,
    userMessage: Int?,
    onBillsEffect: (BillsEffect) -> Unit,
    onBillsUiEvent: (BillsUiEvent) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var filtersVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            BillsTopAppBar(
                searchWidgetState = searchWidgetState,
                searchQuery = searchQuery,
                onSearchQueryChanged = { onBillsUiEvent(BillsUiEvent.SearchQueryChanged(it)) },
                onBackClick = { onBillsEffect(BillsEffect.NavigateBack) },
                onSearchClicked = { onBillsUiEvent(BillsUiEvent.SearchClicked) },
                onCloseClicked = { onBillsUiEvent(BillsUiEvent.ClearRecentSearches) },
            )
        },
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            AnimatedVisibility(visible = !filtersVisible) {
                FloatingActionButton(
                    onClick = {
                        onBillsEffect(BillsEffect.NavigateToAdd)
                    },
                    modifier = Modifier.padding(8.dp),
                ) {
                    Icon(
                        imageVector = PosIcons.Add,
                        contentDescription = null,
                    )
                }
            }
        },

    ) { innerPadding ->
        SharedTransitionLayout {
            Box(modifier = Modifier.padding(innerPadding)) {
                AnimatedContent(
                    uiState,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                    },
                    modifier =
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {},
                    label = "Items Content",
                ) { targetState ->
                    when (targetState) {
                        is BillsUiState.Loading -> {
                            PosLoadingWheel(
                                modifier =
                                Modifier
                                    .fillMaxSize()
                                    .wrapContentSize(Alignment.Center),
                                contentDesc = "LoadingBills",
                            )
                        }
                        is BillsUiState.Success -> {
                            BillsContent(
                                filterScreenVisible = filtersVisible,
                                bills = targetState.supplierInvoices,
                                suppliers = targetState.suppliers,
                                selectedSuppliers = filterUiState.selectedSuppliers,
                                onBillsUiEvent = onBillsUiEvent,
                                onShowFilters = { filtersVisible = true },
                                sharedTransitionScope = this@SharedTransitionLayout,
                                onBillClick = { onBillsEffect(BillsEffect.NavigateToDetail(it)) },
                            )
                            FilterBillsScreenOverlay(
                                isVisible = filtersVisible,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                filterUiState,
                                suppliers = targetState.suppliers,
                                onBillsUiEvent = onBillsUiEvent,
                                onDismiss = { filtersVisible = false },
                            )
                        }

                        is BillsUiState.Error, is BillsUiState.Empty -> {
                            PosEmptyScreen(
                                Modifier.align(Alignment.Center),
                                uiR.drawable.core_ui_ic_outline_inventory_120,
                                R.string.feature_bill_empty_title,
                                R.string.feature_bill_empty_message,
                            )
                        }
                    }
                }
            }
        }
    }
    userMessage?.let { message ->
        val snackbarText = stringResource(message)
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(snackbarText)
            onBillsUiEvent(BillsUiEvent.MessageShown)
        }
    }
}

@Composable
@DevicePreviews
private fun BillsScreenPreview(
    @PreviewParameter(SupplierInvoiceParameterProvider::class)
    supplierInvoices: List<SupplierInvoice>,
) {
    val supplierInvoiceMap = supplierInvoices.associateBy { it.invoiceId }
    POSTheme {
        PosBackground {
            BillsScreen(
                uiState = BillsUiState.Success(supplierInvoiceMap),
                searchWidgetState = SearchWidgetState.CLOSED,
                searchQuery = "",
                filterUiState = BillsFilterUiState(),
                userMessage = null,
                onBillsEffect = {},
                onBillsUiEvent = {},
            )
        }
    }
}