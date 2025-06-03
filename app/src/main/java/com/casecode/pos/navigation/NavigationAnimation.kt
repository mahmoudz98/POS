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
package com.casecode.pos.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry

const val ANIMATION_DURATION_MS = 3000
const val FAST_ANIMATION_DURATION_MS = 1500
const val SLOW_ANIMATION_DURATION_MS = 4000

val contextShiftEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideInHorizontally(initialOffsetX = { it }, animationSpec = slowTween()) +
        fadeIn(animationSpec = slowTween())
}

val contextShiftExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = slowTween()) +
        fadeOut(animationSpec = slowTween())
}

val flowTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideInVertically(initialOffsetY = { it / 2 }, animationSpec = defaultTween()) +
        fadeIn(animationSpec = defaultTween())
}

val flowExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    fadeOut(animationSpec = defaultTween())
}

fun <T> defaultTween(
    durationMillis: Int = ANIMATION_DURATION_MS,
    delayMillis: Int = 0,
): FiniteAnimationSpec<T> {
    return tween(durationMillis = durationMillis, delayMillis)
}

fun <T> fastTween(durationMillis: Int = FAST_ANIMATION_DURATION_MS): FiniteAnimationSpec<T> {
    return tween(durationMillis = durationMillis)
}

fun <T> slowTween(durationMillis: Int = SLOW_ANIMATION_DURATION_MS): FiniteAnimationSpec<T> {
    return tween(durationMillis = durationMillis)
}