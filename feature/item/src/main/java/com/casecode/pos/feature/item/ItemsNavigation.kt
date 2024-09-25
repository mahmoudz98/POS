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
package com.casecode.pos.feature.item

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation

const val ITEMS_GRAPH = "items_graph"
const val ITEMS_ROUTE = "items_route"
const val ADD_ITEM_ROUTE = "add_item_route"
const val UPDATE_ITEM_ROUTE = "update_item_route"
const val QR_PRINT_ITEM_DIALOG_ROUTE = "qr_print_item_dialog_route"

fun NavGraphBuilder.itemsGraph(navController: NavController) {
    navigation(startDestination = ITEMS_ROUTE, route = ITEMS_GRAPH) {
        itemsScreen(navController)
        addItemScreen(navController, onNavigateBack = { navController.popBackStack() })
        updateItemScreen(navController)
        qrCodePrintItemDialog(navController)
    }
}

fun NavGraphBuilder.itemsScreen(navController: NavController) {
    composable(route = ITEMS_ROUTE) {
        val parentEntry =
            remember(it) {
                navController.getBackStackEntry(ITEMS_GRAPH)
            }
        val viewModel: ItemsViewModel = hiltViewModel(parentEntry)

        ItemsRoute(
            viewModel = viewModel,
            onAddItemClick = navController::navigateToAddItem,
            onItemClick = navController::navigateToUpdateUpdateItem,
            onPrintItemClick = navController::navigateToQRCodePrintItemDialog,
        )
    }
}

private fun NavGraphBuilder.addItemScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
) {
    composable(route = ADD_ITEM_ROUTE) {
        val parentEntry =
            remember(it) {
                navController.getBackStackEntry(ITEMS_GRAPH)
            }
        val parentViewModel: ItemsViewModel = hiltViewModel(parentEntry)
        AddOrUpdateItemRoute(
            viewModel = parentViewModel,
            onNavigateBack = onNavigateBack,
        )
    }
}

private fun NavGraphBuilder.updateItemScreen(navController: NavController) {
    composable(route = UPDATE_ITEM_ROUTE) {
        val parentEntry =
            remember(it) {
                navController.getBackStackEntry(ITEMS_GRAPH)
            }
        val parentViewModel: ItemsViewModel = hiltViewModel(parentEntry)
        AddOrUpdateItemRoute(
            viewModel = parentViewModel,
            isUpdate = true,
            onNavigateBack = {
                navController.popBackStack()
            },
        )
    }
}

private fun NavGraphBuilder.qrCodePrintItemDialog(navController: NavController) {
    dialog(route = QR_PRINT_ITEM_DIALOG_ROUTE) {
        val parentEntry =
            remember(it) {
                navController.getBackStackEntry(ITEMS_GRAPH)
            }
        val parentViewModel: ItemsViewModel = hiltViewModel(parentEntry)
        QRCodePrintItemDialog(
            viewModel = parentViewModel,
            onDismiss = {
                navController.popBackStack()
            },
        )
    }
}

fun NavController.navigateToItemsGraph(navOptions: NavOptions) = navigate(ITEMS_GRAPH, navOptions)

fun NavController.navigateToItems() = navigate(ITEMS_ROUTE)

private fun NavController.navigateToAddItem() = navigate(ADD_ITEM_ROUTE)

fun NavController.navigateToUpdateUpdateItem() = navigate(UPDATE_ITEM_ROUTE)

fun NavController.navigateToQRCodePrintItemDialog() = navigate(QR_PRINT_ITEM_DIALOG_ROUTE)