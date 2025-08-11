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
package com.casecode.pos.feature.onboarding.steps

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosEmptyScreen
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.feature.onboarding.OnboardingEvent
import com.casecode.pos.feature.onboarding.OnboardingUiState
import com.casecode.pos.feature.onboarding.R

@Composable
internal fun SubscriptionStep(uiState: OnboardingUiState, onEvent: (OnboardingEvent) -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        if (uiState.availablePlans.isEmpty()) {
            PosEmptyScreen(
                icon = PosIcons.EmptyImage,
                titleRes = R.string.feature_onboarding_empty_plans_title,
                messageRes = R.string.feature_onboarding_empty_plans_message,
            )
        }
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(uiState.availablePlans) { plan ->
                PlanItem(
                    plan = plan,
                    currentCountry = uiState.onboardingData.selectedCurrency?.code,
                    isSelected = uiState.onboardingData.selectedPlan?.id == plan.id,
                    onClick = {
                        (context as? Activity)?.let {
                            onEvent(OnboardingEvent.PlanSelected(plan, it))
                        }
                    },
                )
            }
        }
    }
}

@Preview
@Composable
fun SubscriptionStepPreview() {
    POSTheme {
        PosBackground {
            SubscriptionStep(
                uiState = OnboardingUiState(),
                onEvent = {},
            )
        }
    }
}