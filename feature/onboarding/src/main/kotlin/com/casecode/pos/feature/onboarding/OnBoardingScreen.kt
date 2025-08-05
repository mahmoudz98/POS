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
package com.casecode.pos.feature.onboarding

import android.content.Context
import android.telephony.TelephonyManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.recalculateWindowInsets
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosButton
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.Stepper
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.ui.DevicePreviews
import com.casecode.pos.core.ui.PosDialog
import com.casecode.pos.feature.onboarding.steps.BranchSetupStep
import com.casecode.pos.feature.onboarding.steps.BusinessInfoStep
import com.casecode.pos.feature.onboarding.steps.FinancialsStep
import com.casecode.pos.feature.onboarding.steps.SubscriptionStep

@Composable
internal fun OnBoardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String) -> Boolean,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val countryIsoCode = telephonyManager.networkCountryIso.uppercase()
    OnBoardingScreen(
        uiState = uiState,
        countryIsoCode = countryIsoCode,
        onEvent = viewModel::onEvent,
    )

    uiState.userMessage?.let {
        val snackbarText = stringResource(it)
        LaunchedEffect(Unit) {
            onShowSnackbar(snackbarText)
            viewModel.onEvent(OnboardingEvent.UserMessageShown)
        }
    }
}

@Composable
internal fun OnBoardingScreen(
    uiState: OnboardingUiState,
    countryIsoCode: String = "",
    onEvent: (OnboardingEvent) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { OnboardingStep.entries.size })
    var showClosingDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Stepper(
                modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                numberOfSteps = OnboardingStep.entries.size,
                currentStep = pagerState.currentPage + 1,
                stepDescriptionList =
                OnboardingStep.entries
                    .map { stringResource(id = it.titleRes) },
            )
        },
        bottomBar = {
            ButtonRow(
                currentStep = uiState.currentStep,
                isNextEnabled = uiState.isNextEnabled,
                onEvent = onEvent,
            )
        },
    ) { innerPadding ->
        Box(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .consumeWindowInsets(innerPadding)
                .recalculateWindowInsets(),
        ) {
            if (uiState.isLoading) {
                PosLoadingWheel(
                    "onBoardingLoading",
                    modifier = Modifier.align(Alignment.TopCenter).zIndex(1f),
                )
            }
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                when (OnboardingStep.fromOrdinal(page)) {
                    OnboardingStep.BUSINESS_INFO ->
                        BusinessInfoStep(
                            uiState = uiState,
                            countryIsoCode = countryIsoCode,
                            onEvent = onEvent,
                        )

                    OnboardingStep.FINANCIALS ->
                        FinancialsStep(
                            uiState = uiState,
                            onEvent = onEvent,
                        )

                    OnboardingStep.BRANCHES ->
                        BranchSetupStep(
                            uiState = uiState,
                            onEvent = onEvent,
                        )

                    OnboardingStep.SUBSCRIPTION -> SubscriptionStep(uiState, onEvent)
                }
            }
        }
    }
    if (showClosingDialog) {
        PosDialog(
            titleRes = R.string.feature_onboarding_dialog_exit_title,
            messageRes = R.string.feature_onboarding_dialog_exit_message,
            confirmRes = R.string.feature_onboarding_dialog_exit_yes_button_text,
            onConfirm = {
                onEvent(OnboardingEvent.CloseOnboardingClicked)
            },
            onDismiss = {
                showClosingDialog = false
            },
        )
    }
    BackHandler {
        if (uiState.currentStep > OnboardingStep.BUSINESS_INFO) {
            onEvent(OnboardingEvent.BackClicked)
        } else {
            showClosingDialog = true
        }
    }
    LaunchedEffect(uiState.currentStep) {
        pagerState.animateScrollToPage(uiState.currentStep.ordinal)
    }
}

@Composable
private fun ButtonRow(
    currentStep: OnboardingStep,
    isNextEnabled: Boolean,
    onEvent: (OnboardingEvent) -> Unit,
) {
    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (currentStep.ordinal > 0) {
            PosButton(onClick = { onEvent(OnboardingEvent.BackClicked) }) {
                Text(stringResource(id = R.string.feature_onboarding_previous_button_text))
            }
        } else {
            Spacer(modifier = Modifier)
        }

        val isLastStep = currentStep.ordinal == OnboardingStep.entries.size - 1
        PosButton(
            onClick = {
                val event =
                    if (isLastStep) OnboardingEvent.CompleteOnboardingClicked else OnboardingEvent.NextClicked
                onEvent(event)
            },
            enabled = isNextEnabled,
        ) {
            val text =
                if (isLastStep) R.string.feature_onboarding_done_button_text else R.string.feature_onboarding_next_button_text
            Text(stringResource(id = text))
        }
    }
}

@DevicePreviews
@Composable
fun OnBoardingScreenPreview() {
    POSTheme {
        PosBackground {
            OnBoardingScreen(uiState = OnboardingUiState(), onEvent = {})
        }
    }
}
