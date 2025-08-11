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
package com.casecode.pos.core.model.utils

import java.math.RoundingMode

/**
 * Converts a nullable Double to a formatted String.
 *
 * This function converts the Double to a BigDecimal, strips any trailing zeros, and then converts it to a plain String.
 * If the Double is null, an empty String is returned.
 *
 * @return A formatted String representation of the Double, or an empty String if the Double is null.
 */
fun Double?.toFormattedString(): String = this?.toBigDecimal()?.stripTrailingZeros()?.toPlainString() ?: ""

/**
 * Formats a Double to a String with a specified number of decimal places.
 *
 * This function converts the Double to a BigDecimal, scales it to the desired number of decimal places,
 * removes trailing zeros, and returns the result as a plain String.
 *
 * @param decimalPlaces The number of decimal places to include in the formatted string. Defaults to 2.
 * @return The formatted string representation of the Double.
 */
fun Double.toBigDecimalFormatted(decimalPlaces: Int = 2): String = toBigDecimal()
    .setScale(decimalPlaces, RoundingMode.HALF_UP)
    .stripTrailingZeros()
    .toPlainString()