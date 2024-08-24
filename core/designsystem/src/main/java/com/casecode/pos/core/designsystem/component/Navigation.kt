package com.casecode.pos.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.designsystem.theme.PosTypography

/**
 * Pos navigation bar item with icon and label content slots. Wraps Material 3
 * [NavigationBarItem].
 *
 * @param selected Whether this item is selected.
 * @param onClick The callback to be invoked when this item is selected.
 * @param icon The item icon content.
 * @param modifier Modifier to be applied to this item.
 * @param selectedIcon The item icon content when selected.
 * @param enabled controls the enabled state of this item. When `false`, this item will not be
 * clickable and will appear disabled to accessibility services.
 * @param label The item text label content.
 * @param alwaysShowLabel Whether to always show the label for this item. If false, the label will
 * only be shown when this item is selected.
 */
@Composable
fun RowScope.PosNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
    label: @Composable (() -> Unit)? = null,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors =
            NavigationBarItemDefaults.colors(
                selectedIconColor = PosNavigationDefaults.navigationSelectedItemColor(),
                unselectedIconColor = PosNavigationDefaults.navigationContentColor(),
                selectedTextColor = PosNavigationDefaults.navigationContentColor(),
                unselectedTextColor = PosNavigationDefaults.navigationContentColor(),
                indicatorColor = PosNavigationDefaults.navigationIndicatorColor(),
        ),
    )
}

/**
 * POS navigation bar with content slot. Wraps Material 3 [NavigationBar].
 *
 * @param modifier Modifier to be applied to the navigation bar.
 * @param content Destinations inside the navigation bar. This should contain multiple
 * [NavigationBarItem]s.
 */
@Composable
fun PosNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        contentColor = PosNavigationDefaults.navigationContentColor(),
        tonalElevation = 0.dp,
        content = content,
    )
}

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
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    content: @Composable () -> Unit,
) {
    // TODO: issue when using adaptive navigation drawer in compact, have not custom navigation
    //  drawer
    val layoutType =
        NavigationSuiteScaffoldDefaults
            .calculateFromAdaptiveInfo(windowAdaptiveInfo)

    val navigationSuiteItemColors =
        NavigationSuiteItemColors(
            navigationBarItemColors =
            NavigationBarItemDefaults.colors(
                selectedIconColor = PosNavigationDefaults.navigationSelectedItemColor(),
                unselectedIconColor = PosNavigationDefaults.navigationContentColor(),
                selectedTextColor = PosNavigationDefaults.navigationContentColor(),
                unselectedTextColor = PosNavigationDefaults.navigationContentColor(),
                indicatorColor = PosNavigationDefaults.navigationIndicatorColor(),
            ),
            navigationRailItemColors =
            NavigationRailItemDefaults.colors(
                selectedIconColor = PosNavigationDefaults.navigationSelectedItemColor(),
                unselectedIconColor = PosNavigationDefaults.navigationContentColor(),
                selectedTextColor = PosNavigationDefaults.navigationContentColor(),
                unselectedTextColor = PosNavigationDefaults.navigationContentColor(),
                indicatorColor = PosNavigationDefaults.navigationIndicatorColor(),
            ),
            navigationDrawerItemColors =
            NavigationDrawerItemDefaults.colors(
                selectedIconColor = PosNavigationDefaults.navigationSelectedItemColor(),
                unselectedIconColor = PosNavigationDefaults.navigationContentColor(),
                selectedTextColor = PosNavigationDefaults.navigationContentColor(),
                unselectedTextColor = PosNavigationDefaults.navigationContentColor(),
            ),
        )

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            PosNavigationSuiteScope(
                navigationSuiteScope = this,
                navigationSuiteItemColors = navigationSuiteItemColors,
            ).run(navigationSuiteItems)
        },
        layoutType = layoutType,
        containerColor = Color.Transparent,
        navigationSuiteColors =
        NavigationSuiteDefaults.colors(
            navigationBarContentColor = PosNavigationDefaults.navigationContentColor(),
            navigationRailContainerColor = Color.Transparent,
        ),
        modifier = modifier,
    ) {
        content()
    }
}

/**
 * A wrapper around [NavigationSuiteScope] to declare navigation items.
 */
@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
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
fun PosNavigationDrawerHeader(textHeader: String) {
    Box(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.TopStart,
    ) {
        Text(text = textHeader, style = PosTypography.titleLarge)
    }
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
        colors =
        NavigationDrawerItemDefaults.colors(
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
    DismissibleNavigationDrawer(
        drawerState = drawerState,
        modifier = modifier,
        drawerContent = drawerContent,
        content = content,
    )
}

@ThemePreviews
@Composable
fun NiaNavigationBarPreview() {
    val items = listOf("Sale", "Reports", "Settings")
    val icons =
        listOf(
            PosIcons.Pos,
            PosIcons.Reports,
            PosIcons.Invoices,
        )
    val selectedIcons =
        listOf(
            PosIcons.Pos,
            PosIcons.Reports,
            PosIcons.Settings,
        )

    POSTheme {
        PosNavigationBar {
            items.forEachIndexed { index, item ->
                PosNavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = item,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = selectedIcons[index],
                            contentDescription = item,
                        )
                    },
                    label = { Text(item) },
                    selected = index == 0,
                    onClick = { },
                )
            }
        }
    }
}

@Composable
fun PosNavigationDrawerHeaderItems(
    @StringRes title: Int,
) {
    Text(
        text = stringResource(title),
        style = PosTypography.titleSmall,
        modifier =
        Modifier
            .padding(bottom = 8.dp, start = 16.dp)
            .fillMaxWidth(),
    )
}

@Composable
fun PosNavigationDrawerContent(onClick: () -> Unit) {
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
 * POS navigation default values.
 */
object PosNavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}