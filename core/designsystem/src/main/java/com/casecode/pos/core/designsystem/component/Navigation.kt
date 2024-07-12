package com.casecode.pos.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.casecode.pos.core.designsystem.theme.PosTypography


/**
 * Pos navigation suite scaffold with item and content slots.
 * Wraps Material 3 [NavigationSuiteScaffold].
 *
 * @param modifier Modifier to be applied to the navigation suite scaffold.
 * @param navigationSuiteItems A slot to display multiple items via [PosNavigationSuiteScope].
 * @param windowAdaptiveInfo The window adaptive info.
 * @param content The app content inside the scaffold.
 */

@Composable
fun PosNavigationSuiteScaffold(
    navigationSuiteItems: PosNavigationSuiteScope.() -> Unit,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    content: @Composable () -> Unit,
) {
    // TODO: issue when using adaptive navigation drawer in compact, have not custom navigation
    //  drawer
    /* val layoutType = NavigationSuiteScaffoldDefaults
         .calculateFromAdaptiveInfo(windowAdaptiveInfo)*/
    val customNavSuiteType = with(windowAdaptiveInfo) {
        if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
            NavigationSuiteType.NavigationDrawer
        } else {
            NavigationSuiteType.NavigationRail
        }
    }
    val navigationSuiteItemColors = NavigationSuiteItemColors(
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = PosNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = PosNavigationDefaults.navigationContentColor(),
            selectedTextColor = PosNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = PosNavigationDefaults.navigationContentColor(),
        ),
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = PosNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = PosNavigationDefaults.navigationContentColor(),
            selectedTextColor = PosNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = PosNavigationDefaults.navigationContentColor(),
            indicatorColor = PosNavigationDefaults.navigationIndicatorColor(),
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = PosNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = PosNavigationDefaults.navigationContentColor(),
            selectedTextColor = PosNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = PosNavigationDefaults.navigationContentColor(),
            indicatorColor = PosNavigationDefaults.navigationIndicatorColor(),
        ),

        )

    NavigationSuiteScaffold(

        navigationSuiteItems = {
            PosNavigationSuiteScope(
                navigationSuiteScope = this,
                navigationSuiteItemColors = navigationSuiteItemColors,
            ).run(navigationSuiteItems)
        },

        layoutType = customNavSuiteType,
        containerColor = Color.Transparent,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContentColor = PosNavigationDefaults.navigationContentColor(),
            navigationRailContainerColor = Color.Transparent,
        ),
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
    ) {
        content()
    }
}

/**
 * A wrapper around [NavigationSuiteScope] to declare navigation items.
 */
class PosNavigationSuiteScope internal constructor(
    private val navigationSuiteScope: NavigationSuiteScope,
    private val navigationSuiteItemColors: NavigationSuiteItemColors,
) {

    fun item(
        selected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        icon: @Composable () -> Unit,
        selectedIcon: @Composable () -> Unit = icon,
        label: @Composable (() -> Unit)? = null,
    ) = navigationSuiteScope.item(
        selected = selected,
        onClick = onClick,
        icon = {
            if (selected) {
                selectedIcon()
            } else {
                icon()
            }
        },
        label = label,
        colors = navigationSuiteItemColors,
        modifier = modifier,
    )

}


@Composable
fun PosNavigationDrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.TopStart,
    ) {
        Text(text = stringResource(android.R.string.untitled), style = PosTypography.titleLarge)
    }
}

@Composable
fun PosNavigationDrawerHeaderItems(@StringRes title: Int) {

    Text(
        text = stringResource(title), style = PosTypography.titleSmall,
        modifier = Modifier
            .padding(bottom = 8.dp, start = 16.dp)
            .fillMaxWidth(),
    )

}

@Composable
fun PosNavigationDrawerItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
    label: @Composable (() -> Unit),
) {
    NavigationDrawerItem(
        label = label,
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier.padding(end = 12.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = PosNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = PosNavigationDefaults.navigationContentColor(),
            selectedTextColor = PosNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = PosNavigationDefaults.navigationContentColor(),
        ),
    )
}

@Composable
fun PosNavigationDrawer(
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit = {},
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = modifier,
        drawerContent = drawerContent,
        content = content,
    )
}

@Composable
fun PosNavigationDrawerContent(
    onClick: () -> Unit,
) {
/*    ModalDrawerSheet(modifier = Modifier) {
        PosNavigationDrawerHeader()
        Spacer(modifier = Modifier.padding(12.dp))

        PosNavigationDrawerHeaderItems(title = R.string.pos)
        PosNavigationDrawerItem(
            label = { Text(stringResource(R.string.menu_reports)) },
            selected = false,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.baseline_trending_up_24),
                    contentDescription = null,
                )
            },
            onClick = { onClick() },
        )
        PosNavigationDrawerItem(
            label = { Text(stringResource(R.string.pos)) },
            selected = false,
            icon = { Icon(imageVector = Icons.Filled.PointOfSale, contentDescription = null) },
            onClick = { onClick() },
        )
        PosNavigationDrawerItem(
            label = { Text(stringResource(R.string.menu_items)) },
            selected = false,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.baseline_receipt_long_24),
                    contentDescription = null,
                )
            },
            onClick = { onClick() },
        )
        HorizontalDivider(Modifier.padding(16.dp))

        PosNavigationDrawerHeaderItems(title = R.string.menu_items)
        PosNavigationDrawerItem(
            label = { Text(stringResource(R.string.menu_items)) },
            selected = false,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.baseline_view_module_24),
                    contentDescription = null,
                )
            },
            onClick = { onClick() },
        )
        HorizontalDivider(Modifier.padding(16.dp))
        PosNavigationDrawerHeaderItems(title = R.string.menu_permissions)
        PosNavigationDrawerItem(
            label = { Text(stringResource(R.string.menu_employees)) },
            selected = false,
            icon = {
                Icon(
                    imageVector = Icons.Filled.SupervisorAccount,
                    contentDescription = null,
                )
            },
            onClick = { onClick() },
        )
        PosNavigationDrawerItem(
            label = { Text(stringResource(R.string.menu_settings)) },
            selected = false,
            icon = { Icon(imageVector = Icons.Filled.Settings, contentDescription = null) },
            onClick = { onClick() },
        )
        PosNavigationDrawerItem(
            label = { Text(stringResource(R.string.menu_sign_out)) },
            selected = false,
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                )
            },
            onClick = { onClick() },
        )
    }*/
}




/**
 * Now in Android navigation default values.
 */
object PosNavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}