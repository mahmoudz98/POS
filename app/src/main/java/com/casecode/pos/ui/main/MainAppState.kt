package com.casecode.pos.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.casecode.data.utils.NetworkMonitor
import com.casecode.pos.R
import com.casecode.pos.navigation.TopLevelDestination
import com.casecode.pos.navigation.TopLevelDestination.EMPLOYEES
import com.casecode.pos.navigation.TopLevelDestination.INVOICES
import com.casecode.pos.navigation.TopLevelDestination.ITEMS
import com.casecode.pos.navigation.TopLevelDestination.POS
import com.casecode.pos.navigation.TopLevelDestination.REPORTS
import com.casecode.pos.navigation.TopLevelDestination.SETTINGS
import com.casecode.pos.navigation.TopLevelDestination.SIGN_OUT
import com.casecode.pos.ui.employee.EMPLOYEES_ROUTE
import com.casecode.pos.ui.employee.navigateToEmployees
import com.casecode.pos.ui.invoices.INVOICES_ROUTE
import com.casecode.pos.ui.invoices.navigateToInvoices
import com.casecode.pos.ui.item.ITEMS_ROUTE
import com.casecode.pos.ui.item.ITEM_DIALOG_ROUTE
import com.casecode.pos.ui.item.ITEM_update_DIALOG_ROUTE
import com.casecode.pos.ui.item.QR_PRINT_ITEM_DIALOG_ROUTE
import com.casecode.pos.ui.item.navigateToItemsGraph
import com.casecode.pos.ui.pos.POS_ROUTE
import com.casecode.pos.ui.pos.navigateToPos
import com.casecode.pos.ui.profile.navigateToProfile
import com.casecode.pos.ui.settings.SETTINGS_ROUTE
import com.casecode.pos.ui.settings.navigateToSettings
import com.casecode.pos.ui.signout.SIGN_OUT_ROUTE
import com.casecode.pos.ui.signout.navigateToSignOut
import com.casecode.pos.ui.statistics.REPORTS_ROUTE
import com.casecode.pos.ui.statistics.navigateToReports
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun rememberMainAppState(
    networkMonitor: NetworkMonitor? = null,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
): MainAppState {
    return remember(
        navController,
        coroutineScope,
        networkMonitor,
    ) {
        MainAppState(
            navController = navController,
            coroutineScope = coroutineScope,
            networkMonitor = networkMonitor!!,
            drawerState = drawerState,
        )
    }
}


class TopAppBarAction(
    val icon: ImageVector = Icons.Default.Person,
    val actionIconContent: Int = R.string.profile_title,
    val onClick: () -> Unit = { Timber.e("onClick") },

    )


@Stable
class MainAppState(
    val navController: NavHostController,
    val drawerState: DrawerState,
    val coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
) {

    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination
    val currentTopAppBarAction: TopAppBarAction?
        @Composable get() = when (currentDestination?.route) {
            POS_ROUTE, REPORTS_ROUTE, SETTINGS_ROUTE -> TopAppBarAction(
                icon = Icons.Default.Person, R.string.profile_title,
                onClick = {
                    navController.navigateToProfile(
                        navOptions {
                            launchSingleTop = true
                            restoreState = true
                        },
                    )
                },
            )
            else -> null
        }


    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            POS_ROUTE -> POS
            REPORTS_ROUTE -> REPORTS
            INVOICES_ROUTE -> INVOICES
            ITEMS_ROUTE, ITEM_DIALOG_ROUTE, ITEM_update_DIALOG_ROUTE, QR_PRINT_ITEM_DIALOG_ROUTE -> ITEMS
            EMPLOYEES_ROUTE -> EMPLOYEES
            SETTINGS_ROUTE -> SETTINGS
            SIGN_OUT_ROUTE -> SIGN_OUT
            else -> {
                null
            }
        }

    fun openOrClosed() {
        coroutineScope.launch {
            if (drawerState.isOpen) {
                drawerState.close()
            } else {
                drawerState.open()
            }
        }
    }

    val isOffline = networkMonitor.isOnline.map(Boolean::not).stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false,
    )

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries


    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }

        when (topLevelDestination) {
            POS -> navController.navigateToPos(topLevelNavOptions)
            REPORTS -> navController.navigateToReports(topLevelNavOptions)
            INVOICES -> navController.navigateToInvoices(topLevelNavOptions)
            ITEMS -> navController.navigateToItemsGraph(topLevelNavOptions)
            EMPLOYEES -> navController.navigateToEmployees(topLevelNavOptions)
            SETTINGS -> navController.navigateToSettings(topLevelNavOptions)
            SIGN_OUT -> navController.navigateToSignOut(topLevelNavOptions)
        }
    }


}