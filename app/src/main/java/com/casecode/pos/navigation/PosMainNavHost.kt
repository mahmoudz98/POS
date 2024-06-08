package com.casecode.pos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.casecode.pos.ui.main.MainAppState
import com.casecode.pos.ui.employee.employeesScreen
import com.casecode.pos.ui.invoices.invoicesScreen
import com.casecode.pos.ui.item.itemsGraph
import com.casecode.pos.ui.pos.POS_ROUTE
import com.casecode.pos.ui.pos.posScreen
import com.casecode.pos.ui.profile.profileScreen
import com.casecode.pos.ui.settings.settingScreen
import com.casecode.pos.ui.signout.signOutScreen
import com.casecode.pos.ui.statistics.reportsScreen


@Composable
fun PosMainNavHost(
    appState: MainAppState,
    modifier: Modifier = Modifier,
    startDestination: String = POS_ROUTE,
) {
    NavHost(
        navController = appState.navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        posScreen()
        reportsScreen()
        invoicesScreen { }
        itemsGraph(appState.navController) {appState.openOrClosed()}
        employeesScreen { }
        settingScreen()
        signOutScreen {}
        profileScreen()
    }

}