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
package com.casecode.pos.core.model.data

import java.util.Date

data class CashUp(
    val openDate: Date,
    val closeDate: Date,
    val openCashAmount: Double,
    val transferCashAmount: Double,
    val closedAmountCash: Double,
    val closedAmountCard: Double,
    val closedAmountCheck: Double,
    val closedAmountTotal: Double,
    val branchName: String,
    val openEmployeeName: String,
    val closedEmployeeName: String,
    val closedAmountDue: Double,
)