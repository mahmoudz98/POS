package com.casecode.pos.feature.item.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import com.casecode.pos.feature.item.ItemsSaleScreen
import com.casecode.pos.feature.item.ItemsViewModel
import com.casecode.pos.feature.item.print.QRCodePrintItemDialog
import kotlinx.serialization.Serializable

@Serializable
data object ItemsSaleGraph

@Serializable
data object ItemsSaleRoute

@Serializable
data object QRPrintItemDialogSaleRoute

fun NavGraphBuilder.itemsSaleGraph(navController: NavController) {
    navigation<ItemsSaleGraph>(startDestination = ItemsSaleRoute) {
        itemsSaleScreen(navController)
        qrCodePrintItemSaleDialog(navController)
    }
}

fun NavGraphBuilder.itemsSaleScreen(navController: NavController) {
    composable<ItemsSaleRoute> {
        val parentEntry =
            remember(it) {
                navController.getBackStackEntry(ItemsSaleGraph)
            }
        val parentViewModel: ItemsViewModel = hiltViewModel(parentEntry)

        ItemsSaleScreen(
            parentViewModel,
            onPrintItemClick = navController::navigateToQRCodePrintItemDialogSale,
        )
    }
}

fun NavGraphBuilder.qrCodePrintItemSaleDialog(navController: NavController) {
    dialog<QRPrintItemDialogSaleRoute> {
        val parentEntry =
            remember(it) {
                navController.getBackStackEntry(ItemsSaleGraph)
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

fun NavController.navigateToItemsSaleGraph(navOptions: NavOptions) = navigate(ItemsSaleGraph, navOptions)

fun NavController.navigateToItemsSale(navOptions: NavOptions) = navigate(ItemsSaleRoute, navOptions)

private fun NavController.navigateToQRCodePrintItemDialogSale() = navigate(QRPrintItemDialogSaleRoute)