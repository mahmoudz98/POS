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

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * PosElevatedCard is a composable function that creates an elevated card with default styling for Point of Sale (POS) applications.
 *
 * This function wraps the standard `ElevatedCard` composable and provides pre-defined values for shape, colors, and elevation,
 * which are commonly used in POS systems.
 *
 * @param modifier The modifier to be applied to the card.
 * @param shape The shape of the card. Defaults to [CardDefaults.elevatedShape].
 * @param colors The colors of the card. Defaults to [CardDefaults.elevatedCardColors].
 * @param elevation The elevation of the card. Defaults to [CardDefaults.cardElevation] with a default elevation of 6.dp.
 * @param content The content to be displayed inside the card. This should be a composable function that takes a [ColumnScope] as a receiver.
 */
@Composable
fun PosElevatedCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.elevatedShape,
    colors: CardColors = CardDefaults.elevatedCardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(
        defaultElevation = 6.dp,
    ),
    content: @Composable ColumnScope.() -> Unit,
) {
    ElevatedCard(
        modifier = modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        content = content,
    )
}