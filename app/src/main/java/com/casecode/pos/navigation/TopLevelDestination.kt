package com.casecode.pos.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.casecode.pos.R
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.ui.R.string.core_ui_employees_title

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val titleTextId: Int,
) {
    POS(PosIcons.Pos, PosIcons.Pos, R.string.pos),
    REPORTS(PosIcons.Reports, PosIcons.Reports, R.string.reports_title),

    ITEMS(
        PosIcons.Items,
        PosIcons.Items,
        R.string.menu_items,
    ),
    EMPLOYEES(
        PosIcons.Employee,
        PosIcons.Employee,
        core_ui_employees_title,
    ),
    SETTINGS(
        PosIcons.Settings,
        PosIcons.Settings,
        R.string.settings_title,
    ),
}