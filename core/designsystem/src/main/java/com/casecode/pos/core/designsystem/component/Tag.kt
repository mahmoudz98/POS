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

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.casecode.pos.core.designsystem.theme.POSTheme

@Composable
fun PosTopicTag(
    modifier: Modifier = Modifier,
    followed: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        val containerColor =
            if (followed) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = PosTagDefaults.UNFOLLOWED_TOPIC_TAG_CONTAINER_ALPHA,
                )
            }
        TextButton(
            onClick = onClick,
            enabled = enabled,
            colors =
            ButtonDefaults.textButtonColors(
                containerColor = containerColor,
                contentColor = contentColorFor(backgroundColor = containerColor),
                disabledContainerColor =
                MaterialTheme.colorScheme.onSurface.copy(
                    alpha = PosTagDefaults.DISABLED_TOPIC_TAG_CONTAINER_ALPHA,
                ),
            ),
        ) {
            ProvideTextStyle(value = MaterialTheme.typography.labelSmall) {
                text()
            }
        }
    }
}

@ThemePreviews
@Composable
fun TagPreview() {
    POSTheme {
        PosTopicTag(followed = true, onClick = {}) {
            Text("Topic".uppercase())
        }
    }
}

/**
 * Now in Android tag default values.
 */
object PosTagDefaults {
    const val UNFOLLOWED_TOPIC_TAG_CONTAINER_ALPHA = 0.5f

    // TODO: File bug
    // Button disabled container alpha value not exposed by ButtonDefaults
    const val DISABLED_TOPIC_TAG_CONTAINER_ALPHA = 0.12f
}