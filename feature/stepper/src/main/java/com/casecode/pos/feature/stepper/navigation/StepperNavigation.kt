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
package com.casecode.pos.feature.stepper.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.casecode.pos.feature.stepper.StepperScreen
import kotlinx.serialization.Serializable

@Serializable
data object StepperRoute

fun NavGraphBuilder.stepperScreen(
    onStepperCompleteToHome: () -> Unit,
    onBackToSignIn: () -> Unit,
) {
    composable<StepperRoute> {
        StepperScreen(
            onStepperCompleteToHome = onStepperCompleteToHome,
            onBackToSignIn = onBackToSignIn,
        )
    }
}

fun NavController.navigateToStepper(navOptions: NavOptions) = navigate(StepperRoute, navOptions)