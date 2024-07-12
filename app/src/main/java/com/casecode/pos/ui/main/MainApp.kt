package com.casecode.pos.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.casecode.pos.R
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosGradientBackground
import com.casecode.pos.core.designsystem.component.PosNavigationDrawer
import com.casecode.pos.core.designsystem.component.PosNavigationDrawerHeader
import com.casecode.pos.core.designsystem.component.PosNavigationDrawerItem
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.GradientColors
import com.casecode.pos.navigation.PosMainNavHost
import com.casecode.pos.navigation.TopLevelDestination
import com.casecode.pos.feature.signout.SignOutDialog
import com.casecode.pos.utils.moveToSignInActivity


@Composable
fun MainScreen(
    appState: MainAppState,
    modifier: Modifier = Modifier,
) {
    PosBackground(modifier = modifier) {
        PosGradientBackground(gradientColors = GradientColors()) {
            val snackbarHostState = remember { SnackbarHostState() }
            val isOffline by appState.isOffline.collectAsStateWithLifecycle()

            val notConnectedMessage = stringResource(com.casecode.pos.core.ui.R.string.core_ui_error_network)
            LaunchedEffect(isOffline) {
                if (isOffline) {
                    snackbarHostState.showSnackbar(
                        message = notConnectedMessage,
                        duration = Indefinite,
                    )
                }
            }
            MainApp(appState)

        }
    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MainApp(
    appState: MainAppState,
    modifier: Modifier = Modifier,
) {
    // TODO: Change to use [PosScaffoldNavigation], when have custom layout with custom
    val currentDestination = appState.currentDestination
    val showSignOutDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showSignOutDialog.value) {
        SignOutDialog(
            onSignOut = {
                moveToSignInActivity(context = context)
            },
            onDismiss = { showSignOutDialog.value = false },
        )
    }
    PosNavigationDrawer(
        drawerState = appState.drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.sizeIn(minWidth = 200.dp, maxWidth = 300.dp),
            ) {
                PosNavigationDrawerHeader()
                appState.topLevelDestinations.forEach { destination ->
                    val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
                    PosNavigationDrawerItem(
                        selected = selected,
                        onClick = {
                            appState.openOrClosed()
                            appState.navigateToTopLevelDestination(destination)
                        },
                        icon = {
                            Icon(
                                imageVector = destination.selectedIcon,
                                contentDescription = null,
                            )
                        },
                        label = { Text(stringResource(destination.titleTextId)) },
                        modifier = Modifier,
                    )
                }
                PosNavigationDrawerItem(
                    selected = false,
                    onClick = {
                        showSignOutDialog.value = true
                    },
                    icon = {

                        Icon(
                            imageVector = PosIcons.SignOut,
                            contentDescription = null,
                        )
                    },
                    label = { Text(stringResource(R.string.menu_sign_out)) },
                    modifier = Modifier,
                )
            }
        },
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { padding ->
            Column(
                modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal,
                        ),
                    ),
            ) {
                // Show the top app bar on top level destinations.
                val destination = appState.currentTopLevelDestination

                val currentActionBar = appState.currentTopAppBarAction
                if (currentActionBar != null && destination != null) {
                    PosTopAppBar(
                        titleRes = destination.titleTextId,
                        navigationIcon = Icons.Default.Menu,
                        navigationIconContentDescription = "",
                        onActionClick = currentActionBar.onClick,
                        actionIconContentDescription = stringResource(currentActionBar.actionIconContent),
                        actionIcon = currentActionBar.icon,
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent,
                        ),
                        onNavigationClick = {
                            appState.openOrClosed()
                        },
                    )
                }

                Box(
                    modifier = Modifier.consumeWindowInsets(
                        if (currentActionBar != null) {
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                        } else {
                            WindowInsets(0, 0, 0, 0)
                        },
                    ),
                ) {
                    PosMainNavHost(
                        appState = appState,
                    )
                }
            }

        }
    }
}


private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false