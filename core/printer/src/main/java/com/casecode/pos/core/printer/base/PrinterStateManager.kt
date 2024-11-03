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
package com.casecode.pos.core.printer.base

import com.casecode.pos.core.printer.R
import com.casecode.pos.core.printer.model.PrinterState
import com.casecode.pos.core.printer.model.PrinterStatus
import com.casecode.pos.core.printer.model.PrinterStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrinterStateManager
@Inject
constructor() {
    var printerState = MutableStateFlow<PrinterState>(PrinterState.None)
        internal set

    fun publishState(statusCode: PrinterStatusCode) {
        printerState.value =
            when (statusCode) {
                PrinterStatusCode.PROGRESS_CONNECTING -> {
                    PrinterState.Connecting(R.string.core_printer_state_message_connecting)
                }

                PrinterStatusCode.PROGRESS_CONNECTED -> {
                    PrinterState.Connected(R.string.core_printer_state_message_connected)
                }

                PrinterStatusCode.PROGRESS_PRINTING -> {
                    PrinterState.Printing(R.string.core_printer_state_message_printing)
                }

                PrinterStatusCode.PROGRESS_PRINTED -> {
                    PrinterState.Printed(R.string.core_printer_state_message_finished)
                }

                else -> {
                    PrinterState.Error(
                        R.string.core_printer_state_result_message_finish_unknown_error,
                    )
                }
            }
    }

    fun handleResult(result: PrinterStatus) {
        printerState.value =
            when (result.printerStatus) {
                PrinterStatusCode.FINISH_SUCCESS ->
                    PrinterState.Finished(R.string.core_printer_state_result_message_finish_success)

                PrinterStatusCode.FINISH_NO_PRINTER ->
                    PrinterState.Finished(
                        R.string.core_printer_state_result_message_finish_no_printer,
                    )

                PrinterStatusCode.FINISH_PRINTER_DISCONNECTED ->
                    PrinterState.Finished(
                        R.string.core_printer_state_result_message_finish_printer_disconnected,
                    )

                PrinterStatusCode.FINISH_PARSER_ERROR ->
                    PrinterState.Finished(
                        R.string.core_printer_state_result_message_finish_parser_error,
                    )

                PrinterStatusCode.FINISH_ENCODING_ERROR ->
                    PrinterState.Finished(
                        R.string.core_printer_state_result_message_finish_encoding_error,
                    )

                PrinterStatusCode.FINISH_BARCODE_ERROR ->
                    PrinterState.Finished(
                        R.string.core_printer_state_result_message_finish_barcode_error,
                    )
                else ->
                    PrinterState.Finished(
                        R.string.core_printer_state_result_message_finish_unknown_error,
                    )
            }
    }
}