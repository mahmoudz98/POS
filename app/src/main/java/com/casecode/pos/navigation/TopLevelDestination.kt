package com.casecode.pos.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.casecode.pos.R
import com.casecode.pos.design.icon.PosIcons

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val titleTextId: Int,

) {
    POS(PosIcons.Pos, PosIcons.Pos, R.string.pos), REPORTS(
        PosIcons.Reports,
        PosIcons.Reports,
        R.string.reports_title,

    ),
    INVOICES(PosIcons.Invoices, PosIcons.Invoices, R.string.invoices_title),

    ITEMS(PosIcons.Items,PosIcons.Items,
        R.string.title_items,
    ),
    EMPLOYEES(
        PosIcons.Employee,
        PosIcons.Employee,
        R.string.employees_title,
    ),
    SETTINGS(
        PosIcons.Settings,
        PosIcons.Settings,
        R.string.settings_title,
    ),
    SIGN_OUT(PosIcons.SignOut, PosIcons.SignOut, R.string.sign_out_title),

}