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
package com.casecode.pos.core.designsystem.component

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.Alignment

@OptIn(ExperimentalAnimationApi::class)
fun scaleAndExpandVertically(): EnterTransition =
    scaleIn() + expandVertically(expandFrom = Alignment.Top)

@OptIn(ExperimentalAnimationApi::class)
fun scaleAndShrinkVertically(): ExitTransition = scaleOut() + shrinkVertically()

@OptIn(ExperimentalAnimationApi::class)
fun slideInVerticallyAndScale(): EnterTransition = slideInHorizontally(
    animationSpec = tween(300),
) + scaleIn(
    initialScale = 0.8f,
    animationSpec = tween(300),
)

@OptIn(ExperimentalAnimationApi::class)
fun slideOutVerticallyAndFade(): ExitTransition = slideOutVertically(
    targetOffsetY = { it },
    animationSpec = tween(300),
) + fadeOut(
    animationSpec = tween(3000),
)

enum class ScaleTransitionDirection {
    INWARDS,
    OUTWARDS,
}

fun scaleIntoContainer(
    direction: ScaleTransitionDirection = ScaleTransitionDirection.INWARDS,
    initialScale: Float = if (direction == ScaleTransitionDirection.OUTWARDS) 0.9f else 1.1f,
): EnterTransition = scaleIn(
    animationSpec = tween(3000, delayMillis = 900),
    initialScale = initialScale,
) + fadeIn(animationSpec = tween(3000, delayMillis = 90))

fun scaleOutOfContainer(
    direction: ScaleTransitionDirection = ScaleTransitionDirection.OUTWARDS,
    targetScale: Float = if (direction == ScaleTransitionDirection.INWARDS) 0.9f else 1.1f,
): ExitTransition = scaleOut(
    animationSpec = tween(
        durationMillis = 3000,
        delayMillis = 90,
    ),
    targetScale = targetScale,
) + fadeOut(tween(delayMillis = 900))