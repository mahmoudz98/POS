package com.casecode.pos.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.casecode.pos.R
import com.casecode.pos.core.designsystem.icon.PosIcons

enum class SaleTopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val titleTextId: Int,
    val iconTextId: Int,

    ) {
    POS(
        selectedIcon = PosIcons.Pos,
        unselectedIcon = PosIcons.PosBorder,
        titleTextId = R.string.app_name,
        iconTextId = R.string.pos,
    ),
    REPORTS(
        selectedIcon = PosIcons.Reports, unselectedIcon = PosIcons.ReportsBorder,
       titleTextId = R.string.reports_title,iconTextId = R.string.reports_title,
    ),

    SETTING(
        selectedIcon= PosIcons.Settings,
        unselectedIcon=PosIcons.SettingsBorder,
       titleTextId = R.string.settings_title,
        iconTextId = R.string.settings_title,
    ),

}