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
@file:OptIn(ExperimentalMaterial3Api::class)

package com.casecode.pos.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme


/**
 * A Composable function that provides a transition between a default TopAppBar and a search-focused TopAppBar.
 *
 * This function uses `AnimatedContent` to smoothly animate between two states:
 * - `SearchWidgetState.CLOSED`: Displays the `defaultTopAppBar` content.
 * - `SearchWidgetState.OPENED`: Displays the `searchTopAppBar` content.
 *
 * The transition is controlled by the `searchWidgetState` parameter.
 *
 * @param searchWidgetState The current state of the search widget, determining which TopAppBar to display.
 * @param defaultTopAppBar A composable function that provides the content of the default TopAppBar.
 * @param searchTopAppBar A composable function that provides the content of the search-focused TopAppBar.
 */
@Composable
fun SearchTopAppBar(
    searchWidgetState: SearchWidgetState,
    defaultTopAppBar: @Composable () -> Unit,
    searchTopAppBar: @Composable () -> Unit,
) {
    AnimatedContent(
        targetState = searchWidgetState,
        transitionSpec = {
            if (targetState == SearchWidgetState.OPENED) {
                slideInHorizontally(
                    initialOffsetX = { it / 2 },
                    animationSpec = tween(
                        durationMillis = 150,
                        easing = LinearOutSlowInEasing,
                    ),
                ) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { 0 },
                        animationSpec = tween(
                            durationMillis = 200,
                            easing = LinearOutSlowInEasing,
                        ),
                    )
            } else {
                slideInHorizontally(
                    initialOffsetX = { 0 },
                    animationSpec = tween(
                        durationMillis = 150,
                        easing = LinearOutSlowInEasing,
                    ),
                ) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { it / 2 },
                        animationSpec = tween(
                            durationMillis = 200,
                            easing = LinearOutSlowInEasing,
                        ),
                    )
            }
        },
    ) { targetState ->
        when (targetState) {
            SearchWidgetState.CLOSED -> {
                defaultTopAppBar()
            }
            SearchWidgetState.OPENED -> {
                searchTopAppBar()
            }
        }
    }
}

/** Represents the current state of the Search Widget. */
enum class SearchWidgetState {
    OPENED,
    CLOSED,
}

/**
 * A custom TopAppBar for the Point of Sale (POS) application.
 *
 * This composable provides a standardized TopAppBar with a centered title, optional navigation and action icons,
 * and customizable colors and behavior.
 *
 * @param modifier Modifier to be applied to the TopAppBar.
 * @param titleRes The string resource ID for the title text.
 * @param navigationIcon The ImageVector to be used for the navigation icon. Defaults to null.
 * @param navigationIconContentDescription The content description for the navigation icon. Defaults to null.
 * @param actionIcon The ImageVector to be used for the action icon. Defaults to null.
 * @param actionIconContentDescription The content description for the action icon. Defaults to null.
 * @param scrollBehavior The scroll behavior of the TopAppBar. Defaults to null.
 * @param colors The colors to be used for the TopAppBar. Defaults to TopAppBarDefaults.centerAlignedTopAppBarColors().
 * @param onNavigationClick The callback to be invoked when the navigation icon is clicked. Defaults to an empty lambda.
 * @param onActionClick The callback to be invoked when the action icon is clicked. Defaults to an empty lambda.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosTopAppBar(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int,
    navigationIcon: ImageVector? = null,
    navigationIconContentDescription: String? = null,
    actionIcon: ImageVector? = null,
    actionIconContentDescription: String? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    onNavigationClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(id = titleRes)) },
        navigationIcon = {
            if (navigationIcon != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationIconContentDescription,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        actions = {
            IconButton(onClick = onActionClick) {
                if (actionIcon != null) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = actionIconContentDescription,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        colors = colors,
        modifier = modifier.testTag("posTopAppBar"),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosTopAppBar(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int,
    navigationIcon: ImageVector? = null,
    navigationIconContentDescription: String? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    onNavigationClick: () -> Unit = {},
    action: @Composable RowScope.() -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(id = titleRes)) },
        navigationIcon = {
            if (navigationIcon != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationIconContentDescription,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        actions = action,
        colors = colors,
        modifier = modifier.testTag("posTopAppBar"),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Top App Bar")
@Composable
private fun PosTopAppBarPreview() {
    POSTheme {
        PosTopAppBar(
            titleRes = android.R.string.untitled,
            navigationIcon = Icons.Filled.Menu,
            navigationIconContentDescription = "Navigation icon",
            onActionClick = {},
            actionIcon = PosIcons.MoreVert,
            actionIconContentDescription = "Action icon",
        )
    }
}
@Preview
@Composable
fun SearchTopAppBarPreview() {
    SearchTopAppBar(
        searchWidgetState = SearchWidgetState.OPENED,
        defaultTopAppBar = {
          PosTopAppBar(
            titleRes = android.R.string.untitled,
            navigationIcon = PosIcons.ArrowBack,
            navigationIconContentDescription = "Navigation icon",
            onActionClick = {},
            actionIcon = PosIcons.MoreVert,
            actionIconContentDescription = "Action icon",
          )
        },
        searchTopAppBar = {
          SearchToolbar(
            searchQuery = "",
            onSearchQueryChanged = {},
            onCloseClicked = {},
          )
        },
    )
}