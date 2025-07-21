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

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

/**
 * A composable function that displays an empty screen with an image, title, and message.
 *
 * This composable is typically used to indicate that there is no data to display in a particular section of the UI.
 * It provides a visual cue to the user that the screen is empty and may include instructions or information
 * on how to populate the screen with data.
 *
 * @param modifier Modifier used to adjust the layout or appearance of the empty screen.
 * @param imageRes Resource ID of the image to display.
 * @param titleRes Resource ID of the title string.
 * @param messageRes Resource ID of the message string.
 */
@Composable
fun PosEmptyScreen(
    modifier: Modifier = Modifier,
    @DrawableRes imageRes: Int,
    @StringRes titleRes: Int,
    @StringRes messageRes: Int,
) {
    Column(
        modifier =
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
        )

        Text(
            text = stringResource(id = titleRes),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp),
        )

        Text(
            text = stringResource(id = messageRes),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

/**
 * A composable function that displays an empty screen with an icon, title, and message.
 *
 * This composable is typically used to indicate that there is no data to display in a particular section of the UI.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param icon The icon to be displayed.
 * @param titleRes The resource ID of the title string.
 * @param messageRes The resource ID of the message string.
 */
@Composable
fun PosEmptyScreen(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    @StringRes titleRes: Int,
    @StringRes messageRes: Int,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primaryContainer),
        )

        Text(
            text = stringResource(id = titleRes),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            modifier = Modifier.padding(top = 16.dp),
        )

        Text(
            text = stringResource(id = messageRes),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}