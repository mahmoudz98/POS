package com.casecode.pos.ui.employee

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val EMPLOYEES_ROUTE = "employees_route"

fun NavGraphBuilder.employeesScreen(onTopicClick: (String) -> Unit) {
    composable(
        route = EMPLOYEES_ROUTE,

    ) {
        EmployeesScreen("POS")
    }
}

fun NavController.navigateToEmployees(navOptions: NavOptions) = navigate(
    EMPLOYEES_ROUTE,
    navOptions,
)