package com.casecode.pos.ui.main

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.casecode.pos.R
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosGradientBackground
import com.casecode.pos.core.designsystem.component.PosNavigationSuiteScaffold
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.theme.GradientColors
import com.casecode.pos.core.designsystem.theme.LocalGradientColors
import com.casecode.pos.core.ui.moveToSignInActivity
import com.casecode.pos.navigation.PosMainNavHost
import com.casecode.pos.navigation.PosSaleNavHost
import com.casecode.pos.navigation.SaleTopLevelDestination
import com.casecode.pos.navigation.TopLevelDestination
import timber.log.Timber
import com.casecode.pos.core.ui.R.string as uiString

@Composable
fun MainScreen(
    appState: MainAppState,
    modifier: Modifier = Modifier,
) {
    val shouldShowGradientBackground =
        appState.currentAdminTopLevelDestination == TopLevelDestination.POS ||
            appState.currentSaleTopLevelDestination == SaleTopLevelDestination.POS
    PosBackground(modifier = modifier) {
        PosGradientBackground(
            gradientColors =
            if (shouldShowGradientBackground) {
                LocalGradientColors.current
            } else {
                GradientColors()
            },
        ) {
            val snackbarHostState = remember { SnackbarHostState() }
            val isOffline by appState.isOffline.collectAsStateWithLifecycle()

            val notConnectedMessage = stringResource(uiString.core_ui_error_network)
            LaunchedEffect(isOffline) {
                if (isOffline) {
                    snackbarHostState.showSnackbar(
                        message = notConnectedMessage,
                        duration = Indefinite,
                    )
                }
            }
            MainApp(
                appState = appState,
                snackbarHostState = snackbarHostState,
            )
        }
    }
}

@Composable
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
)
internal fun MainApp(
    appState: MainAppState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    // TODO: Change to use [PosScaffoldNavigation], when have custom layout with custom
    val currentDestination = appState.currentDestination
    val context = LocalContext.current

    val mainAuthUiState = appState.mainAuthUiState.collectAsStateWithLifecycle()
    when (mainAuthUiState.value) {
        MainAuthUiState.Loading -> {
            // TODO: add custom loading with wait for screen
            /*   PosLoadingWheel(
                   contentDesc = "LoadingMainApp",
                   modifier = Modifier.wrapContentSize(align = Alignment.Center),
               )*/
        }

        MainAuthUiState.ErrorLogin -> {
            // TODO: handle when error login to sign out and login again
            moveToSignInActivity(context = context)
        }

        MainAuthUiState.LoginByAdmin, MainAuthUiState.LoginByAdminEmployee -> {
            AdminScreens(appState, currentDestination, snackbarHostState, modifier, context)
        }

        MainAuthUiState.LoginBySaleEmployee -> {
            SaleEmployeeScreens(appState, currentDestination, modifier, snackbarHostState, context)
        }

        MainAuthUiState.LoginByNoneEmployee -> {
            // TODO:handle with not permission for employee
            Timber.e("LoginByNoneEmployee")
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AdminScreens(
    appState: MainAppState,
    currentDestination: NavDestination?,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier,
    context: Context,
) {
    val windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()

    PosNavigationSuiteScaffold(
        navigationSuiteItems = {
            appState.topLevelDestinations.forEach { destination ->
                val selected =
                    currentDestination.isTopLevelDestinationInHierarchy(destination)
                item(
                    selected = selected,
                    onClick = {
                        appState.navigateToTopLevelDestination(destination)
                    },
                    icon = {
                        Icon(
                            imageVector = destination.unselectedIcon,
                            contentDescription = null,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = destination.selectedIcon,
                            contentDescription = null,
                        )
                    },
                    label = { Text(stringResource(destination.titleTextId)) },
                    modifier =
                    Modifier
                        .testTag("PosSaleNavItem"),
                )
            }
        },
        windowAdaptiveInfo = windowAdaptiveInfo,
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackbarHostState) },
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
                val destination = appState.currentAdminTopLevelDestination

                val currentActionBar = appState.currentTopAppBarAction
                if (currentActionBar != null && destination != null) {
                    PosTopAppBar(
                        titleRes = destination.titleTextId,
                        onActionClick = currentActionBar.onClick,
                        actionIconContentDescription = stringResource(currentActionBar.actionIconContent),
                        actionIcon = currentActionBar.icon,
                        colors =
                        TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent,
                        ),
                    )
                }

                Box(
                    modifier =
                    Modifier.consumeWindowInsets(
                        if (currentActionBar != null) {
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                        } else {
                            WindowInsets(0, 0, 0, 0)
                        },
                    ),
                ) {
                    PosMainNavHost(
                        appState = appState,
                        onSignOutClick = {
                            moveToSignInActivity(context = context)
                        },
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
private fun SaleEmployeeScreens(
    appState: MainAppState,
    currentDestination: NavDestination?,
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    context: Context,
) {
    val windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()
    PosNavigationSuiteScaffold(
        navigationSuiteItems = {
            appState.saleTopLevelDestinations.forEach { destination ->
                val selected =
                    currentDestination
                        .isSaleTopLevelDestinationInHierarchy(destination)
                item(
                    selected = selected,
                    onClick = { appState.navigateToSaleTopLevelDestination(destination) },
                    icon = {
                        Icon(
                            imageVector = destination.unselectedIcon,
                            contentDescription = null,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = destination.selectedIcon,
                            contentDescription = null,
                        )
                    },
                    label = { Text(stringResource(destination.iconTextId)) },
                    modifier =
                    Modifier
                        .testTag("PosSaleNavItem"),
                )
            }
        },
        windowAdaptiveInfo = windowAdaptiveInfo,
    ) {
        Scaffold(
            modifier =
            modifier.semantics {
                testTagsAsResourceId = true
            },
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            Column(
                Modifier
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
                val destination = appState.currentSaleTopLevelDestination

                if (destination != null) {
                    PosTopAppBar(
                        titleRes =
                        appState.currentSaleTopLevelDestination?.titleTextId
                            ?: R.string.app_name,
                        colors =
                        TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent,
                        ),
                    )
                }

                Box(
                    modifier =
                    Modifier.consumeWindowInsets(
                        WindowInsets(0, 0, 0, 0),
                    ),
                ) {
                    PosSaleNavHost(
                        appState = appState,
                        onSignOutClick = {
                            moveToSignInActivity(context = context)
                        },
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

private fun NavDestination?.isSaleTopLevelDestinationInHierarchy(destination: SaleTopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false