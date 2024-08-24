package com.casecode.pos.feature.stepper.subscriptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casecode.pos.core.designsystem.component.PosBackground
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.subscriptions.Subscription
import com.casecode.pos.feature.stepper.R
import com.casecode.pos.feature.stepper.StepperBusinessUiState
import com.casecode.pos.feature.stepper.StepperBusinessViewModel

@Composable
internal fun BusinessSubscriptionScreen(viewModel: StepperBusinessViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    viewModel.getSubscriptionsBusiness()
    BusinessSubscriptionScreen(
        uiState,
        onSubscriptionClick = viewModel::addSubscriptionBusinessSelected,
        onNextClick = viewModel::checkNetworkThenSetSubscriptionBusinessSelected,
        onPreviousClick = viewModel::previousStep,
    )
    // TODO : Add Subscription Purchase with google console
    // https://medium.com/@arorasarthak54/in-app-subscription-for-android-apps-deb069dd93e6
}

@Composable
private fun BusinessSubscriptionScreen(
    uiState: StepperBusinessUiState,
    onSubscriptionClick: (Subscription) -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        if (uiState.isLoading) {
            PosLoadingWheel(
                "LoadingBusinessSubscription",
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            SubscriptionList(
                uiState.subscriptions,
                uiState.currentSubscription,
                onSubscriptionClick = onSubscriptionClick,
            )
        }
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            PosTextButton(
                onClick = onPreviousClick,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowLeft,
                        contentDescription = null,
                    )
                },
                text = { Text(stringResource(id = R.string.feature_stepper_previous_button_text)) },
            )
            PosTextButton(
                onClick = onNextClick,
                text = { Text(stringResource(id = R.string.feature_stepper_next_button_text)) },
                trainingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                        contentDescription = null,
                    )
                },
                modifier =
                    Modifier
                        .wrapContentSize(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BusinessSubscriptionLoadingScreenPreview() {
    POSTheme {
        BusinessSubscriptionScreen(
            StepperBusinessUiState(
                isLoading = true,
            ),
            onSubscriptionClick = {},
            onNextClick = {},
            onPreviousClick = {},
        )
    }
}

@com.casecode.pos.core.ui.DevicePreviews
@Composable
fun BusinessSubscriptionScreenPreview() {
    POSTheme {
        PosBackground {
            BusinessSubscriptionScreen(
                StepperBusinessUiState(
                    subscriptions =
                        listOf(
                            Subscription(
                                cost = 1150,
                                duration = 6946,
                                permissions = listOf(),
                                type = "maecenas",
                            ),
                            Subscription(
                                cost = 6456,
                                duration = 7802,
                                permissions = listOf(),
                                type = "quaerendum",
                        ),
                    ),
                    isLoading = false,
                ),
                onSubscriptionClick = {},
                onNextClick = {},
                onPreviousClick = {},
            )
        }
    }
}