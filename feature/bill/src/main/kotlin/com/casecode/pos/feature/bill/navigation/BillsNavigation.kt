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
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.casecode.pos.core.notifications.DEEP_LINK_URI_PATTERN
import com.casecode.pos.feature.bill.BillsScreen
import com.casecode.pos.feature.bill.BillsViewModel
import com.casecode.pos.feature.bill.creation.AddBillScreen
import com.casecode.pos.feature.bill.detials.AddBillPaymentScreen
import com.casecode.pos.feature.bill.detials.BillDetailsViewModel
import com.casecode.pos.feature.bill.detials.BillScreen
import com.casecode.pos.feature.bill.detials.UpdateBillScreen
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
    data class BillDetailsRoute(val billId: String? = null) : BillsGraph()

    @Serializable
    data object BillPaymentRoute : BillsGraph()

    @Serializable
    data object BillCreationRoute : BillsGraph()

    @Serializable
    data object BillEditingRoute : BillsGraph()
}

fun NavGraphBuilder.billsGraph(navController: NavController) {
    navigation<BillsGraph.BillsNavigation>(startDestination = BillsRoute) {
        billsScreen(navController)
        billScreen(navController)
        billPayment(navController)
        addBillScreen(navController::popBackStack)
        updateBillScreen(navController)
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
    composable<BillsGraph.BillDetailsRoute>(
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(BillsGraph.BillsNavigation)
        }
        val billId = backStackEntry.arguments?.getString(BillsGraph.BillDetailsRoute::billId.name)
        Timber.d("id: $billId")
        val billDetailsViewModel: BillDetailsViewModel = hiltViewModel(parentEntry)
        billDetailsViewModel.onBillIdChange(billId)
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
        val billsViewModel: BillDetailsViewModel = hiltViewModel(parentEntry)

        AddBillPaymentScreen(
            billsViewModel,
            onNavigateBack = navController::popBackStack,
        )
    }
}

fun NavGraphBuilder.addBillScreen(onBackClick: () -> Unit) {
    composable<BillsGraph.BillCreationRoute> {
        AddBillScreen(
            onBackClick = onBackClick,
        )
    }
}

fun NavGraphBuilder.updateBillScreen(navController: NavController) {
    composable<BillsGraph.BillEditingRoute> { backStackEntry ->
        val billsGraphBackStackEntry =
            remember(backStackEntry) {
                navController.getBackStackEntry(BillsGraph.BillsNavigation)
            }
        val billsViewModel: BillDetailsViewModel = hiltViewModel(billsGraphBackStackEntry)

        UpdateBillScreen(
            viewModel = billsViewModel,
            onBackClick = navController::popBackStack,
        )
    }
}

fun NavController.navigateToBillsGraph() = navigate(BillsGraph.BillsNavigation)
private fun NavController.navigateToBills() = navigate(BillsGraph.BillsRoute)
private fun NavController.navigateToBillDetails(id: String?) = navigate(BillsGraph.BillDetailsRoute(id))

private fun NavController.navigateToBillPayment() = navigate(BillsGraph.BillPaymentRoute)
private fun NavController.navigateToBillCreation() = navigate(BillsGraph.BillCreationRoute)
private fun NavController.navigateToBillEditing() = navigate(BillsGraph.BillEditingRoute)