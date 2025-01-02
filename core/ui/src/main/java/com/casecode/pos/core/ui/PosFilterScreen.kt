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
package com.casecode.pos.core.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.icon.PosIcons

/**
 * Composable function that displays a filter screen as a modal overlay.
 *
 * This screen provides a UI for users to apply filters. It features a semi-transparent
 * background that can be clicked to dismiss the screen, a header with close and reset buttons,
 * and a content area for filter options.
 *
 * @param modifier Modifier used to adjust the layout of the screen.
 * @param sharedTransitionScope Scope for shared element transitions.
 * @param animatedVisibilityScope Scope for animated visibility of the filter screen.
 * @param enableFilter Indicates whether the reset filter button should be enabled.
 * @param onRestFilterClick Callback function executed when the reset filter button is clicked.
 * @param onDismiss Callback function executed when the screen is dismissed.
 * @param contentFilter Composable function that provides the content of the filter screen.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PosFilterScreen(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    enableFilter: Boolean,
    onRestFilterClick: () -> Unit,
    onDismiss: () -> Unit,
    contentFilter: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) { },
    ) {
        Spacer(
            modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    onDismiss()
                },
        )
        with(sharedTransitionScope) {
            Column(
                Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)
                    .clip(MaterialTheme.shapes.medium)
                    .sharedBounds(
                        rememberSharedContentState(FilterSharedElementKey),
                        animatedVisibilityScope = animatedVisibilityScope,
                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                        clipInOverlayDuringTransition = OverlayClip(MaterialTheme.shapes.medium),
                    )
                    .wrapContentSize()
                    .heightIn(max = 450.dp)
                    .verticalScroll(rememberScrollState())
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) { }
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .skipToLookaheadSize(),
            ) {
                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = PosIcons.Close,
                            contentDescription = null,
                        )
                    }
                    Box(
                        modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(id = R.string.core_ui_filter_label),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                    IconButton(
                        onClick = onRestFilterClick,
                        enabled = enableFilter,
                    ) {
                        Icon(
                            imageVector = PosIcons.FilterClear,
                            contentDescription = null,
                        )
                    }
                }
                contentFilter()
            }
        }
    }
}

object FilterSharedElementKey