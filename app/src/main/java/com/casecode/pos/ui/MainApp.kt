/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casecode.pos.ui

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.casecode.pos.MainAuthUiState
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosGradientBackground
import com.casecode.pos.core.designsystem.component.PosNavigationSuiteScaffold
import com.casecode.pos.core.designsystem.component.PosNavigationSuiteScope
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.theme.GradientColors
import com.casecode.pos.core.designsystem.theme.LocalGradientColors
import com.casecode.pos.core.ui.moveToSignInActivity
import com.casecode.pos.feature.sale.SaleRoute
import com.casecode.pos.navigation.PosMainNavHost
import com.casecode.pos.navigation.PosSaleNavHost
import com.casecode.pos.navigation.TopLevelDestination
import timber.log.Timber
import kotlin.reflect.KClass
import com.casecode.pos.core.ui.R.string as uiString

@Composable
fun MainScreen(
    appState: MainAppState,
    modifier: Modifier = Modifier,
) {
    val shouldShowGradientBackground =
        appState.currentDestination?.hasRoute(SaleRoute::class) == true
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
internal fun MainApp(
    appState: MainAppState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    // TODO: Change to use [PosScaffoldNavigation], when have custom layout with custom
    val currentDestination = appState.currentDestination
    val context = LocalContext.current

    val mainAuthUiState = appState.mainAuthUiState
    when (mainAuthUiState) {
        MainAuthUiState.Loading -> {}

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
fun AdminScreens(
    appState: MainAppState,
    currentDestination: NavDestination?,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier,
    context: Context,
) {
    val windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()

    PosNavigationSuiteScaffold(
        navigationSuiteItems = {
            navigationSuiteItems(
                destinations = appState.topLevelDestinations,
                currentDestination = currentDestination,
                onDestinationClick = { appState.navigateToTopLevelDestination(it) },
            )
        },
        windowAdaptiveInfo = windowAdaptiveInfo,
    ) {
        ScreenContent(
            appState = appState,
            modifier = modifier,
            snackbarHostState = snackbarHostState,
            currentDestination = appState.currentAdminTopLevelDestination,
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

@Composable
fun SaleEmployeeScreens(
    appState: MainAppState,
    currentDestination: NavDestination?,
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    context: Context,
) {
    val windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()

    PosNavigationSuiteScaffold(
        navigationSuiteItems = {
            navigationSuiteItems(
                destinations = appState.saleTopLevelDestinations,
                currentDestination = currentDestination,
                onDestinationClick = { appState.navigateToTopLevelDestination(it) },
            )
        },
        windowAdaptiveInfo = windowAdaptiveInfo,
    ) {
        ScreenContent(
            appState = appState,
            modifier = modifier,
            snackbarHostState = snackbarHostState,
            currentDestination = appState.currentSaleTopLevelDestination,
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

fun PosNavigationSuiteScope.navigationSuiteItems(
    destinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onDestinationClick: (TopLevelDestination) -> Unit,
) {
    destinations.forEach { destination ->
        val selected = currentDestination.isRouteInHierarchy(destination.route)
        item(
            selected = selected,
            onClick = { onDestinationClick(destination) },
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
            modifier = Modifier.testTag(destination.toString()),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenContent(
    appState: MainAppState,
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    currentDestination: TopLevelDestination?,
    content: @Composable () -> Unit,
) {
    Scaffold(
        modifier = modifier.semantics { testTagsAsResourceId = true },
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                .onConsumedWindowInsetsChanged {
                    Timber.e("onConsumedWindowInsetsChanged:  $it")
                },
        ) {
            // Show the top app bar on top level destinations.
            val destination = currentDestination

            val currentActionBar = appState.currentTopAppBarAction
            Timber.e("currentActionBar: $currentActionBar destination: $destination")
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
                modifier = Modifier
                    .weight(1f)
                    .consumeWindowInsets(
                        if (currentActionBar != null) {
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                        } else {
                            WindowInsets(0, 0, 0, 0)
                        },
                    ),

                ) {
                content()
            }
        }
    }
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any {
        it.hasRoute(route)
    } == true