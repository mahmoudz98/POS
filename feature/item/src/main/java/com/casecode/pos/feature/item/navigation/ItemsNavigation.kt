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
package com.casecode.pos.feature.item.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import com.casecode.pos.core.designsystem.component.scaleAndExpandVertically
import com.casecode.pos.core.designsystem.component.scaleAndShrinkVertically
import com.casecode.pos.feature.item.ItemsScreen
import com.casecode.pos.feature.item.ItemsViewModel
import com.casecode.pos.feature.item.details.AddOrUpdateItemScreen
import com.casecode.pos.feature.item.print.QRCodePrintItemDialog
import kotlinx.serialization.Serializable

@Serializable
data object ItemsGraph

@Serializable
data object ItemsRoute

@Serializable
data object AddItemRoute

@Serializable
data object UpdateItemRoute

@Serializable
data object QRPrintItemDialogRoute

fun NavGraphBuilder.itemsGraph(navController: NavController) {
    navigation<ItemsGraph>(startDestination = ItemsRoute) {
        itemsScreen(navController)
        addItemScreen(navController, onNavigateBack = { navController.popBackStack() })
        updateItemScreen(navController)
        qrCodePrintItemDialog(navController)
    }
}

fun NavGraphBuilder.itemsScreen(navController: NavController) {
    composable<ItemsRoute> {
        val parentEntry =
            remember(it) {
                navController.getBackStackEntry(ItemsGraph)
            }
        val viewModel: ItemsViewModel = hiltViewModel(parentEntry)

        ItemsScreen(
            viewModel = viewModel,
            onAddItemClick = navController::navigateToAddItem,
            onItemClick = navController::navigateToUpdateUpdateItem,
            onPrintItemClick = navController::navigateToQRCodePrintItemDialog,
        )
    }
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.defaultItemScreenTransition() =
    slideIntoContainer(
        animationSpec = tween(200, easing = EaseIn),
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
    ) +
        fadeIn(
            animationSpec =
                tween(
                    200,
                    easing = LinearEasing,
                ),
        )

fun AnimatedContentTransitionScope<NavBackStackEntry>.defaultItemScreenExitTransition() =
    slideOutOfContainer(
        animationSpec = tween(200, easing = EaseOut),
        towards = AnimatedContentTransitionScope.SlideDirection.End,
    ) +
            fadeOut(
            animationSpec =
                tween(
                    200,
                    easing = LinearEasing,
                ),
        )

private fun NavGraphBuilder.addItemScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
) {
    composable<AddItemRoute>(
        enterTransition = { defaultItemScreenTransition() },
        exitTransition = { defaultItemScreenExitTransition() },
    ) {
        val parentEntry =
            remember(it) {
                navController.getBackStackEntry(ItemsGraph)
            }
        val parentViewModel: ItemsViewModel = hiltViewModel(parentEntry)
        AddOrUpdateItemScreen(
            viewModel = parentViewModel,
            onNavigateBack = onNavigateBack,
        )
    }
}

private fun NavGraphBuilder.updateItemScreen(navController: NavController) {
    composable<UpdateItemRoute>(
        enterTransition = { scaleAndExpandVertically() },
        exitTransition = { scaleAndShrinkVertically() },
    ) {
        val parentEntry =
            remember(it) {
                navController.getBackStackEntry(ItemsGraph)
            }
        val parentViewModel: ItemsViewModel = hiltViewModel(parentEntry)
        AddOrUpdateItemScreen(
            viewModel = parentViewModel,
            isUpdate = true,
            onNavigateBack = {
                navController.popBackStack()
            },
        )
    }
}

private fun NavGraphBuilder.qrCodePrintItemDialog(navController: NavController) {
    dialog<QRPrintItemDialogRoute> {
        val parentEntry =
            remember(it) {
                navController.getBackStackEntry(ItemsGraph)
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

fun NavController.navigateToItemsGraph(navOptions: NavOptions) = navigate(ItemsGraph, navOptions)

private fun NavController.navigateToItems() = navigate(ItemsRoute)

private fun NavController.navigateToAddItem() = navigate(AddItemRoute)

private fun NavController.navigateToUpdateUpdateItem() = navigate(UpdateItemRoute)

private fun NavController.navigateToQRCodePrintItemDialog() = navigate(QRPrintItemDialogRoute)