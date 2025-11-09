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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosEmptyScreen
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.ui.BranchItem
import com.casecode.pos.feature.onboarding.OnboardingData
import com.casecode.pos.feature.onboarding.OnboardingEvent
import com.casecode.pos.feature.onboarding.OnboardingUiState
import com.casecode.pos.feature.onboarding.R
import com.casecode.pos.core.ui.R as uiR

@Composable
internal fun BranchSetupStep(uiState: OnboardingUiState, onEvent: (OnboardingEvent) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            if (uiState.onboardingData.branches.isEmpty()) {
                PosEmptyScreen(
                    modifier = Modifier,
                    imageRes = uiR.drawable.core_ui_ic_outline_inventory_120,
                    titleRes = R.string.feature_onboarding_branch_setup_empty_title,
                    messageRes = R.string.feature_onboarding_branch_setup_empty_message,
                )
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(uiState.onboardingData.branches) {
                    BranchItem(branch = it)
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { onEvent(OnboardingEvent.SetAddBranchDialogVisibility(true)) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 8.dp),
        ) {
            Text(stringResource(id = uiR.string.core_ui_add_branch_button_text))
        }
    }
    if (uiState.isAddBranchDialogOpen) {
        BranchDialog(
            isoCode = uiState.countrySelected?.countryCode ?: "",
            onDismissRequest = { onEvent(OnboardingEvent.SetAddBranchDialogVisibility(false)) },
            onAddBranch = { branchName, phoneNumber ->
                onEvent(OnboardingEvent.AddBranch(branchName, phoneNumber))
            },
        )
    }
}

@Preview
@Composable
fun BranchSetupStepPreview() {
    POSTheme {
        PosBackground {
            BranchSetupStep(
                uiState = OnboardingUiState(
                    onboardingData = OnboardingData(
                        branches = listOf(
                            Branch("1", "branch 1", "123455"),
                        ),
                    ),
                ),
                onEvent = {},
            )
        }
    }
}

@Preview
@Composable
fun BranchSetupStepWithEmptyPreview() {
    POSTheme {
        PosBackground {
            BranchSetupStep(
                uiState = OnboardingUiState(onboardingData = OnboardingData(branches = emptyList())),
                onEvent = {},
            )
        }
    }
}
