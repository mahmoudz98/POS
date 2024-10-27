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
package com.casecode.pos.core.printer.model

sealed class PrinterState(
    open val message: Int? = null,
) {
    object None : PrinterState()

    data class Connecting(
        override val message: Int,
    ) : PrinterState(message)

    data class Connected(
        override val message: Int,
    ) : PrinterState(message)

    data class Printing(
        override val message: Int,
    ) : PrinterState(message)

    data class Printed(
        override val message: Int,
    ) : PrinterState(message)

    data class Error(
        override val message: Int,
    ) : PrinterState(message)

    data class Finished(
        override val message: Int,
    ) : PrinterState(message)
}