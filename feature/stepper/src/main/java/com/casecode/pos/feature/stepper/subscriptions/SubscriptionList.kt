package com.casecode.pos.feature.stepper.subscriptions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.component.PosTextButton
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.ui.R.string as uiString
import com.casecode.pos.core.model.data.subscriptions.Subscription


@Composable
internal fun SubscriptionList(
    subscriptions: List<Subscription>,
    currentSubscription: Subscription,
    onSubscriptionClick: (Subscription) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
    ) {
        items(subscriptions) { subscription ->
            val isPayed = currentSubscription.type == subscription.type
            SubscriptionItem(
                subscription = subscription,
                isPayed = isPayed,
                onSubscriptionClick = onSubscriptionClick,
            )
        }
    }
}

@Composable
private fun SubscriptionItem(
    subscription: Subscription,
    isPayed: Boolean = false,
    onSubscriptionClick: (Subscription) -> Unit,
) {
    //TODO: analysis subscription type and add number of days and cost of transaction.
    ListItem(
        headlineContent = { Text(text = subscription.type) },
        supportingContent = {
            val costFormated = stringResource(uiString.core_ui_currency, subscription.cost.toString())
            val durationFormated =
                "${subscription.duration / 30} ${stringResource(uiString.core_ui_time_month_label)}"
            Text(
                text = "$costFormated / $durationFormated",
            )
        },
        trailingContent = {
            if (!isPayed) {
                PosTextButton(
                    onClick = { onSubscriptionClick(subscription) },
                    text = {
                        Text(text = stringResource(id = uiString.core_ui_subscribe_button_text))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Payment,
                            contentDescription = "subscription",
                        )
                    },
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            headlineColor = MaterialTheme.colorScheme.onSurfaceVariant,
            overlineColor = MaterialTheme.colorScheme.onSurfaceVariant,
            supportingColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )

}

@com.casecode.pos.core.ui.DevicePreviews
@Composable
private fun SubscriptionItemPreview() {
    POSTheme {
        SubscriptionItem(
            Subscription(
                cost = 7176,
                duration = 60,
                permissions = listOf(),
                type = "basic",
            ),
        ) {}
    }
}