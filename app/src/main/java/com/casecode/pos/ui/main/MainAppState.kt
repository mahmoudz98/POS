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
import com.casecode.pos.core.data.service.AuthService
import com.casecode.pos.core.data.utils.NetworkMonitor
import com.casecode.pos.core.model.data.LoginStateResult
import com.casecode.pos.core.model.data.permissions.Permission
import com.casecode.pos.feature.employee.EMPLOYEES_ROUTE
import com.casecode.pos.navigation.TopLevelDestination
import com.casecode.pos.navigation.TopLevelDestination.EMPLOYEES
import com.casecode.pos.navigation.SaleTopLevelDestination.POS as POSSale
import com.casecode.pos.navigation.SaleTopLevelDestination.REPORTS as ReportsSale
import com.casecode.pos.navigation.SaleTopLevelDestination.SETTING as SettingSale
import com.casecode.pos.navigation.TopLevelDestination.INVOICES
import com.casecode.pos.navigation.TopLevelDestination.ITEMS
import com.casecode.pos.navigation.TopLevelDestination.POS
import com.casecode.pos.navigation.TopLevelDestination.REPORTS
import com.casecode.pos.navigation.TopLevelDestination.SETTINGS
import com.casecode.pos.feature.employee.navigateToEmployees
import com.casecode.pos.feature.invoice.INVOICES_ROUTE
import com.casecode.pos.feature.invoice.navigateToInvoices
import com.casecode.pos.feature.item.ITEMS_ROUTE
import com.casecode.pos.feature.item.ITEM_DIALOG_ROUTE
import com.casecode.pos.feature.item.ITEM_update_DIALOG_ROUTE
import com.casecode.pos.feature.item.QR_PRINT_ITEM_DIALOG_ROUTE
import com.casecode.pos.feature.item.navigateToItemsGraph
import com.casecode.pos.feature.sale.navigateToPos
import com.casecode.pos.feature.profile.navigateToProfile
import com.casecode.pos.feature.sale.POS_ROUTE
import com.casecode.pos.feature.setting.SETTING_ROUTE
import com.casecode.pos.feature.setting.navigateToSettings
import com.casecode.pos.feature.signout.SIGN_OUT_ROUTE
import com.casecode.pos.feature.statistics.STATISTICS_ROUTE
import com.casecode.pos.feature.statistics.navigateToStatistics
import com.casecode.pos.navigation.SaleTopLevelDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun rememberMainAppState(
    networkMonitor: NetworkMonitor? = null,
    authService: AuthService,
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
            authService = authService
        )
    }
}

@Stable
class TopAppBarAction(
    val icon: ImageVector = Icons.Default.Person,
    val actionIconContent: Int = com.casecode.pos.feature.profile.R.string.feature_profile_title,
    val onClick: () -> Unit = {  },
)

sealed interface MainAuthUiState{
    object Loading : MainAuthUiState
    object LoginByAdmin : MainAuthUiState
    object LoginByAdminEmployee : MainAuthUiState
    object LoginBySaleEmployee : MainAuthUiState
    object LoginByNoneEmployee : MainAuthUiState
    object ErrorLogin:MainAuthUiState
}

@Stable
class MainAppState(
    val navController: NavHostController,
    val drawerState: DrawerState,
    val coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
    authService: AuthService
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentTopAppBarAction: TopAppBarAction?
        @Composable get() = when (currentDestination?.route) {
            POS_ROUTE, STATISTICS_ROUTE, EMPLOYEES_ROUTE, SETTING_ROUTE, SIGN_OUT_ROUTE -> TopAppBarAction(
                icon = Icons.Default.Person, com.casecode.pos.feature.profile.R.string.feature_profile_title,
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

    val currentSaleTopLevelDestination: SaleTopLevelDestination?
        @Composable get() = when(currentDestination?.route){
            POS_ROUTE -> POSSale
            STATISTICS_ROUTE -> ReportsSale
            SETTING_ROUTE -> SettingSale
            else -> null
        }

    val currentAdminTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            POS_ROUTE -> POS
            STATISTICS_ROUTE -> REPORTS
            INVOICES_ROUTE -> INVOICES
            ITEMS_ROUTE, ITEM_DIALOG_ROUTE, ITEM_update_DIALOG_ROUTE, QR_PRINT_ITEM_DIALOG_ROUTE -> ITEMS
            EMPLOYEES_ROUTE -> EMPLOYEES
            SETTING_ROUTE -> SETTINGS
            else -> null
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
    val mainAuthUiState = authService.loginData.map {
            Timber.e("loginData: $it")
        when(it){
            LoginStateResult.Loading -> MainAuthUiState.Loading
            is LoginStateResult.EmployeeLogin -> {
                when(it.employee.permission){
                    Permission.ADMIN -> MainAuthUiState.LoginByAdminEmployee
                    Permission.SALE -> MainAuthUiState.LoginBySaleEmployee
                    Permission.NONE -> MainAuthUiState.LoginByNoneEmployee
                }
            }
            LoginStateResult.Error , LoginStateResult.NotSignIn-> MainAuthUiState.ErrorLogin
            is LoginStateResult.NotCompleteBusiness -> MainAuthUiState.ErrorLogin
            is LoginStateResult.SuccessLoginAdmin -> MainAuthUiState.LoginByAdmin
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainAuthUiState.Loading,
    )


    /**
     * Map of top level destinations to be used in the TopBar. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    /**
     * Map of top level destinations to be used in the TopBar. The key is the
     * route.
     */
    val saleTopLevelDestinations: List<SaleTopLevelDestination> = SaleTopLevelDestination.entries


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
            REPORTS -> navController.navigateToStatistics(topLevelNavOptions)
            INVOICES -> navController.navigateToInvoices(topLevelNavOptions)
            ITEMS -> navController.navigateToItemsGraph(topLevelNavOptions)
            EMPLOYEES -> navController.navigateToEmployees(topLevelNavOptions)
            SETTINGS -> navController.navigateToSettings(topLevelNavOptions)
        }
    }
    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param saleTopLevelDestination: The destination the app needs to navigate to.
     */

    fun navigateToSaleTopLevelDestination(saleTopLevelDestination: SaleTopLevelDestination) {
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

        when (saleTopLevelDestination) {
            POSSale -> navController.navigateToPos(topLevelNavOptions)
            ReportsSale -> navController.navigateToStatistics(topLevelNavOptions)
            SettingSale -> navController.navigateToSettings(topLevelNavOptions)
        }
    }

}