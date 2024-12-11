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
package com.casecode.pos.feature.bill.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.casecode.pos.feature.bill.BillsScreen
import com.casecode.pos.feature.bill.BillsViewModel
import com.casecode.pos.feature.bill.creation.AddBillScreen
import com.casecode.pos.feature.bill.creation.BillCreationViewModel
import com.casecode.pos.feature.bill.creation.BillItemFormScreen
import com.casecode.pos.feature.bill.creation.UpdateBillScreen
import com.casecode.pos.feature.bill.detials.AddBillPaymentScreen
import com.casecode.pos.feature.bill.detials.BillScreen
import com.casecode.pos.feature.bill.navigation.BillsGraph.BillsRoute
import kotlinx.serialization.Serializable
import timber.log.Timber

@Serializable
internal sealed class BillsGraph {
    @Serializable
    data object BillsNavigation : BillsGraph()

    @Serializable
    data object BillsRoute : BillsGraph()

    @Serializable
    data object BillDetailsRoute : BillsGraph()

    @Serializable
    data object BillPaymentRoute : BillsGraph()

    @Serializable
    data object BillCreationRoute : BillsGraph()

    @Serializable
    data object BillCreationItemRoute : BillsGraph()

    @Serializable
    data object BillEditingItemRoute : BillsGraph()

    @Serializable
    data class BillEditingRoute(val billId: String? = null) : BillsGraph()
}

fun NavGraphBuilder.billsGraph(navController: NavController) {
    navigation<BillsGraph.BillsNavigation>(startDestination = BillsRoute) {
        billsScreen(navController)
        billScreen(navController)
        billPayment(navController)
        addBillScreen(navController)
        updateBillScreen(navController)
        addBillItemScreen(navController)
        updateBillItemScreen(navController)
    }
}

fun NavGraphBuilder.billsScreen(navController: NavController) {
    composable<BillsRoute> {
        val billsGraphBackStackEntry =
            remember(it) {
                navController.getBackStackEntry(BillsGraph.BillsNavigation)
            }
        val billsViewModel: BillsViewModel = hiltViewModel(billsGraphBackStackEntry)

        BillsScreen(
            viewModel = billsViewModel,
            onBackClick = navController::popBackStack,
            onAddBillClick = navController::navigateToBillCreation,
            onBillClick = navController::navigateToBillDetails,
        )
    }
}

fun NavGraphBuilder.billScreen(navController: NavController) {
    composable<BillsGraph.BillDetailsRoute> { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(BillsGraph.BillsNavigation)
        }
        val billDetailsViewModel: BillsViewModel = hiltViewModel(parentEntry)
        BillScreen(
            viewModel = billDetailsViewModel,
            onNavigateBack = navController::popBackStack,
            onEditBill = navController::navigateToBillEditing,
            onPaymentClick = navController::navigateToBillPayment,
        )
    }
}

fun NavGraphBuilder.billPayment(navController: NavController) {
    composable<BillsGraph.BillPaymentRoute> { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(BillsGraph.BillsNavigation)
        }
        val billsViewModel: BillsViewModel = hiltViewModel(parentEntry)

        AddBillPaymentScreen(
            billsViewModel,
            onNavigateBack = navController::popBackStack,
        )
    }
}

fun NavGraphBuilder.addBillScreen(navController: NavController) {
    composable<BillsGraph.BillCreationRoute> {
        val billsGraphBackStackEntry =
            remember(it) {
                navController.getBackStackEntry(BillsGraph.BillsNavigation)
            }
        val billsViewModel: BillCreationViewModel = hiltViewModel(billsGraphBackStackEntry)
        AddBillScreen(
            viewModel = billsViewModel,
            onBackClick = navController::popBackStack,
            onAddBillItem = navController::navigateToBillCreationItem,
            onUpdateBillItem = navController::navigateToBillEditingItem,
        )
    }
}

fun NavGraphBuilder.addBillItemScreen(navController: NavController) {
    composable<BillsGraph.BillCreationItemRoute> {
        val billsGraphBackStackEntry =
            remember(it) {
                navController.getBackStackEntry(BillsGraph.BillsNavigation)
            }
        val billsViewModel: BillCreationViewModel = hiltViewModel(billsGraphBackStackEntry)
        BillItemFormScreen(
            viewModel = billsViewModel,
            onBackClick = navController::popBackStack,
        )
    }
}

fun NavGraphBuilder.updateBillItemScreen(navController: NavController) {
    composable<BillsGraph.BillEditingItemRoute> { backStackEntry ->
        val billsGraphBackStackEntry =
            remember(backStackEntry) {
                navController.getBackStackEntry(BillsGraph.BillsNavigation)
            }
        val billsViewModel: BillCreationViewModel = hiltViewModel(billsGraphBackStackEntry)
        BillItemFormScreen(
            viewModel = billsViewModel,
            isUpdate = true,
            onBackClick = navController::popBackStack,
        )
    }
}

fun NavGraphBuilder.updateBillScreen(navController: NavController) {
    composable<BillsGraph.BillEditingRoute> {backStackEntry->
        val billsGraphBackStackEntry =
            remember(backStackEntry) {
                navController.getBackStackEntry(BillsGraph.BillsNavigation)
            }
        val id = backStackEntry.arguments?.getString(BillsGraph.BillEditingRoute::billId.name)
        val billsViewModel: BillCreationViewModel = hiltViewModel(billsGraphBackStackEntry)
        billsViewModel.onBillIdChange(id)

        UpdateBillScreen(
            viewModel = billsViewModel,
            onBackClick = navController::popBackStack,
            onAddBillItem = navController::navigateToBillCreationItem,
            onUpdateBillItem = navController::navigateToBillEditingItem,
        )
    }
}

fun NavController.navigateToBillsGraph() = navigate(BillsGraph.BillsNavigation)
private fun NavController.navigateToBills() = navigate(BillsGraph.BillsRoute)
private fun NavController.navigateToBillDetails() = navigate(BillsGraph.BillDetailsRoute)

private fun NavController.navigateToBillPayment() = navigate(BillsGraph.BillPaymentRoute)
private fun NavController.navigateToBillCreation() = navigate(BillsGraph.BillCreationRoute)
private fun NavController.navigateToBillCreationItem() = navigate(BillsGraph.BillCreationItemRoute)
private fun NavController.navigateToBillEditingItem() = navigate(BillsGraph.BillEditingItemRoute)
private fun NavController.navigateToBillEditing(id: String?) =
    navigate(BillsGraph.BillEditingRoute(id))