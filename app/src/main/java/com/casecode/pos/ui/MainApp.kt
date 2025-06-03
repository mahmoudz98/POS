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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosGradientBackground
import com.casecode.pos.core.designsystem.component.PosNavigationSuiteScaffold
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.theme.GradientColors
import com.casecode.pos.core.designsystem.theme.LocalGradientColors
import com.casecode.pos.feature.profile.R
import com.casecode.pos.feature.sale.navigation.SaleRoute
import com.casecode.pos.navigation.AdminHomeGraphRoute
import com.casecode.pos.navigation.PosMainNavHost
import com.casecode.pos.navigation.SaleHomeGraphRoute
import com.casecode.pos.navigation.determineStartGraph
import kotlin.reflect.KClass
import com.casecode.pos.core.ui.R.string as uiString

@Composable
fun MainApp(
    appState: MainAppState,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),

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
                windowAdaptiveInfo = windowAdaptiveInfo,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainApp(
    appState: MainAppState,
    snackbarHostState: SnackbarHostState,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    modifier: Modifier = Modifier,
) {
    val startGraphDestination = appState.initialDestinationState.determineStartGraph()
    val currentTopLevelDestination =
        if (startGraphDestination == AdminHomeGraphRoute) {
            appState.currentAdminTopLevelDestination
        } else {
            appState.currentSaleTopLevelDestination
        }
    val topLevelDestinations =
        if (startGraphDestination == AdminHomeGraphRoute) {
            appState.adminTopLevelDestinations
        } else {
            appState.saleTopLevelDestinations
        }

    val isMainRoleGraph =
        startGraphDestination == AdminHomeGraphRoute || startGraphDestination == SaleHomeGraphRoute
    val navSuiteState = rememberNavigationSuiteScaffoldState(
        if (isMainRoleGraph) {
            NavigationSuiteScaffoldValue.Visible
        } else {
            NavigationSuiteScaffoldValue.Hidden
        },
    )
    val currentDestination = appState.currentDestination

    PosNavigationSuiteScaffold(
        state = navSuiteState,
        navigationSuiteItems = {
            topLevelDestinations.forEach { destination ->
                val selected = currentDestination.isRouteInHierarchy(destination.route)
                item(
                    selected = selected,
                    onClick = { appState.navigateToTopLevelDestination(destination) },
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
        },
        windowAdaptiveInfo = windowAdaptiveInfo,
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
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
            ) {
                val hasProfileAction = appState.hasProfileActionBar(appState.currentDestination)
                if (hasProfileAction && currentTopLevelDestination != null) {
                    PosTopAppBar(
                        titleRes = currentTopLevelDestination.titleTextId,
                        onActionClick = { appState.navigateToProfile() },
                        actionIconContentDescription = stringResource(R.string.feature_profile_title),
                        actionIcon = Icons.Default.Person,
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    )
                }
                Box(
                    modifier =
                    Modifier
                        .weight(1f)
                        .consumeWindowInsets(
                            if (hasProfileAction) {
                                WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                            } else {
                                WindowInsets(0, 0, 0, 0)
                            },
                        ),
                ) {
                    PosMainNavHost(
                        appState = appState,
                        startGraphDestination = startGraphDestination,
                    )
                }
            }
        }
    }
    LaunchedEffect(isMainRoleGraph) {
        if (isMainRoleGraph) {
            navSuiteState.show()
        } else {
            navSuiteState.hide()
        }
    }
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) = this?.hierarchy?.any {
    it.hasRoute(route)
} == true