package com.casecode.pos.feature.employee

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val EMPLOYEES_ROUTE = "employees_route"

fun NavGraphBuilder.employeesScreen() {
    composable(
        route = EMPLOYEES_ROUTE,

    ) {
        EmployeesRoute()
    }
}

fun NavController.navigateToEmployees(navOptions: NavOptions) = navigate(
    EMPLOYEES_ROUTE,
    navOptions,
)