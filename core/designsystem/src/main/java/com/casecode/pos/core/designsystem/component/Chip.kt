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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme

/**
 * POS filter chip with included leading checked icon as well as text content slot.
 *
 * @param selected Whether the chip is currently checked.
 * @param onSelectedChange Called when the user clicks the chip and toggles checked.
 * @param modifier Modifier to be applied to the chip.
 * @param enabled Controls the enabled state of the chip. When `false`, this chip will not be
 * clickable and will appear disabled to accessibility services.
 * @param label The text label content.
 */
@Composable
fun PosFilterChip(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = { onSelectedChange(!selected) },
        label = {
            ProvideTextStyle(value = MaterialTheme.typography.labelSmall) {
                label()
            }
        },
        modifier = modifier,
        enabled = enabled,
        leadingIcon =
        if (selected) {
            {
                Icon(
                    imageVector = PosIcons.Check,
                    contentDescription = null,
                )
            }
        } else {
            null
        },
        shape = CircleShape,
        border =
        FilterChipDefaults.filterChipBorder(
            enabled = enabled,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.onBackground,
            selectedBorderColor = MaterialTheme.colorScheme.onBackground,
            disabledBorderColor =
            MaterialTheme.colorScheme.onBackground.copy(
                alpha = POSChipDefaults.DISABLED_CHIP_CONTENT_ALPHA,
            ),
            disabledSelectedBorderColor =
            MaterialTheme.colorScheme.onBackground.copy(
                alpha = POSChipDefaults.DISABLED_CHIP_CONTENT_ALPHA,
            ),
            selectedBorderWidth = POSChipDefaults.ChipBorderWidth,
        ),
        colors =
        FilterChipDefaults.filterChipColors(
            labelColor = MaterialTheme.colorScheme.onBackground,
            iconColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor =
            if (selected) {
                MaterialTheme.colorScheme.onBackground.copy(
                    alpha = POSChipDefaults.DISABLED_CHIP_CONTAINER_ALPHA,
                )
            } else {
                Color.Transparent
            },
            disabledLabelColor =
            MaterialTheme.colorScheme.onBackground.copy(
                alpha = POSChipDefaults.DISABLED_CHIP_CONTENT_ALPHA,
            ),
            disabledLeadingIconColor =
            MaterialTheme.colorScheme.onBackground.copy(
                alpha = POSChipDefaults.DISABLED_CHIP_CONTENT_ALPHA,
            ),
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    )
}

/**
 * POS Elevated filter chip with included leading checked icon as well as text content slot.
 * This composable provides a styled ElevatedFilterChip that adheres to the Point of Sale design system.
 * It is primarily used for filtering or selecting options in a POS context.
 *
 * @param selected Whether the chip is currently checked.
 * @param onSelectedChange Called when the user clicks the chip and toggles checked.
 * @param modifier Modifier to be applied to the chip.
 * @param enabled Controls the enabled state of the chip. When `false`, this chip will not be
 * clickable and will appear disabled to accessibility services.
 * @param selectedIcon The icon displayed when the chip is selected. Defaults to `PosIcons.Check`.
 * @param unSelectedIcon The icon displayed when the chip is not selected. Defaults to `null` (no icon).
 * @param label The text label content displayed within the chip. This should be a composable function.
 */
@Composable
fun PosElevatedFilterChip(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectedIcon: ImageVector = PosIcons.Check,
    unSelectedIcon: ImageVector? = null,
    label: @Composable () -> Unit,
) {
    ElevatedFilterChip(
        selected = selected,
        onClick = { onSelectedChange(!selected) },
        label = {
            ProvideTextStyle(value = MaterialTheme.typography.labelMedium) {
                label()
            }
        },
        modifier = modifier,
        enabled = enabled,
        trailingIcon = {
            if (selected) {
                Icon(
                    imageVector = selectedIcon,
                    contentDescription = null,
                )
            } else {
                if (unSelectedIcon != null) {
                    Icon(imageVector = unSelectedIcon, contentDescription = null)
                } else {
                    null
                }
            }
        },
        colors =
        FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            labelColor = MaterialTheme.colorScheme.onBackground,
            iconColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor =
            if (selected) {
                MaterialTheme.colorScheme.onBackground.copy(
                    alpha = POSChipDefaults.DISABLED_CHIP_CONTAINER_ALPHA,
                )
            } else {
                MaterialTheme.colorScheme.surface
            },
            disabledLabelColor =
            MaterialTheme.colorScheme.onBackground.copy(
                alpha = POSChipDefaults.DISABLED_CHIP_CONTENT_ALPHA,
            ),
            disabledTrailingIconColor =
            MaterialTheme.colorScheme.onBackground.copy(
                alpha = POSChipDefaults.DISABLED_CHIP_CONTENT_ALPHA,
            ),
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    )
}

/**
 * POS filter chip with included leading checked icon as well as text content slot.
 * This chip uses a circular shape and custom styling for a POS-specific look.
 *
 * @param selected Whether the chip is currently checked.
 * @param onSelectedChange Called when the user clicks the chip and toggles its checked state.
 * @param modifier Modifier to be applied to the chip.
 * @param selectedIcon The icon displayed when the chip is selected. Defaults to `PosIcons.Track`.
 * @param unSelectedIcon The icon displayed when the chip is not selected. If null, no icon is shown when unselected.
 * @param enabled Controls the enabled state of the chip. When `false`, the chip will not be
 * clickable and will appear disabled to accessibility services. Defaults to `true`.
 * @param label The text label content displayed within the chip.
 */
@Composable
fun PosInputChip(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    selectedIcon: ImageVector = PosIcons.Track,
    unSelectedIcon: ImageVector? = null,
    enabled: Boolean = true,
    label: @Composable () -> Unit,
) {
    InputChip(
        selected = selected,
        onClick = { onSelectedChange(!selected) },
        label = {
            ProvideTextStyle(value = MaterialTheme.typography.labelSmall) {
                label()
            }
        },
        modifier = modifier,
        enabled = enabled,
      /*  avatar = {
            Icon(imageVector = selectedIcon, contentDescription = null)
        },*/
        leadingIcon = {
            if (selected) {
                Icon(
                    imageVector = selectedIcon,
                    contentDescription = null,
                )
            } else {
                if (unSelectedIcon != null) {
                    Icon(imageVector = unSelectedIcon, contentDescription = null)
                } else {
                    null
                }
            }
        },
        shape = CircleShape,
        border =
        FilterChipDefaults.filterChipBorder(
            enabled = enabled,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.onBackground,
            selectedBorderColor = MaterialTheme.colorScheme.onBackground,
            disabledBorderColor =
            MaterialTheme.colorScheme.onBackground.copy(
                alpha = POSChipDefaults.DISABLED_CHIP_CONTENT_ALPHA,
            ),
            disabledSelectedBorderColor =
            MaterialTheme.colorScheme.onBackground.copy(
                alpha = POSChipDefaults.DISABLED_CHIP_CONTENT_ALPHA,
            ),
            selectedBorderWidth = POSChipDefaults.ChipBorderWidth,
        ),
        colors =
        FilterChipDefaults.filterChipColors(
            labelColor = MaterialTheme.colorScheme.onBackground,
            iconColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor =
            if (selected) {
                MaterialTheme.colorScheme.onBackground.copy(
                    alpha = POSChipDefaults.DISABLED_CHIP_CONTAINER_ALPHA,
                )
            } else {
                Color.Transparent
            },
            disabledLabelColor =
            MaterialTheme.colorScheme.onBackground.copy(
                alpha = POSChipDefaults.DISABLED_CHIP_CONTENT_ALPHA,
            ),
            disabledLeadingIconColor =
            MaterialTheme.colorScheme.onBackground.copy(
                alpha = POSChipDefaults.DISABLED_CHIP_CONTENT_ALPHA,
            ),
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    )
}

@ThemePreviews
@Composable
fun ChipPreview() {
    POSTheme {
        PosBackground(modifier = Modifier.size(300.dp, 400.dp)) {
            Column {
                PosFilterChip(selected = true, onSelectedChange = {}) {
                    Text("PosFilterChip")
                }
                PosInputChip(selected = true, onSelectedChange = {}) {
                    Text("PosInputChip")
                }

                PosElevatedFilterChip(selected = true, onSelectedChange = {}) {
                    Text("ElevatedFilterChip")
                }
                PosElevatedFilterChip(selected = false, onSelectedChange = {}) {
                    Text("ElevatedFilterChip")
                }
            }
        }
    }
}

/**
 * Now in Android chip default values.
 */
object POSChipDefaults {
    // TODO: File bug
    // FilterChip default values aren't exposed via FilterChipDefaults
    const val DISABLED_CHIP_CONTAINER_ALPHA = 0.12f
    const val DISABLED_CHIP_CONTENT_ALPHA = 0.38f
    val ChipBorderWidth = 1.dp
}