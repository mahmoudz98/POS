package com.casecode.pos.ui.item

import androidx.compose.runtime.Composable
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
 const val ITEM_DIALOG_ROUTE = "item_dialog_route"
 const val ITEM_update_DIALOG_ROUTE = "item_update_dialog_route"
 const val QR_PRINT_ITEM_DIALOG_ROUTE = "qr_print_item_dialog_route"

fun NavGraphBuilder.itemsGraph(navController: NavController,  onMenuClick:()->Unit  ) {
    navigation(startDestination = ITEMS_ROUTE, route = ITEMS_GRAPH) {
        itemsScreen(navController, onMenuClick)
        itemDialog(navController, onDismiss = { navController.popBackStack() })
        itemUpdateDialog(navController)
        qrCodePrintItemDialog(navController)
    }

}

fun NavGraphBuilder.itemsScreen(navController: NavController, onMenuClick: () -> Unit) {
    composable(route = ITEMS_ROUTE) {
        val parentEntry = remember(it) {
            navController.getBackStackEntry(ITEMS_GRAPH)
        }
        val viewModel: ItemsViewModel = hiltViewModel(parentEntry)

        ItemsRoute(
            viewModel = viewModel,
            onMenuClick = {onMenuClick()  },
            onAddItemClick = navController::navigateToItemDialog,
            onItemClick = navController::navigateToUpdateItemDialog,
            onPrintItemClick = navController::navigateToQRCodePrintItemDialog,
        )
    }
}


private fun NavGraphBuilder.itemDialog(navController: NavController, onDismiss: () -> Unit) {
    dialog(route = ITEM_DIALOG_ROUTE) {
        val parentEntry = remember(it) {
            navController.getBackStackEntry(ITEMS_GRAPH)
        }
        val parentViewModel: ItemsViewModel = hiltViewModel(parentEntry)
        ItemDialog(
            viewModel = parentViewModel,
            onDismiss = onDismiss
            ,
        )
    }
}

private fun NavGraphBuilder.itemUpdateDialog(navController: NavController) {
    dialog(route = ITEM_update_DIALOG_ROUTE) {
        val parentEntry = remember(it) {
            navController.getBackStackEntry(ITEMS_GRAPH)
        }
        val parentViewModel: ItemsViewModel = hiltViewModel(parentEntry)
        ItemDialog(
            viewModel = parentViewModel, isUpdate = true,
            onDismiss = {
                navController.popBackStack()
            },
        )
    }
}
private fun NavGraphBuilder.qrCodePrintItemDialog(navController: NavController) {
    dialog(route = QR_PRINT_ITEM_DIALOG_ROUTE) {
        val parentEntry = remember(it) {
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
fun NavController.navigateToItems(navOptions: NavOptions) = navigate(ITEMS_ROUTE, navOptions)
private fun NavController.navigateToItemDialog() = navigate(ITEM_DIALOG_ROUTE)
fun NavController.navigateToUpdateItemDialog() = navigate(ITEM_update_DIALOG_ROUTE)
fun NavController.navigateToQRCodePrintItemDialog() = navigate(QR_PRINT_ITEM_DIALOG_ROUTE)