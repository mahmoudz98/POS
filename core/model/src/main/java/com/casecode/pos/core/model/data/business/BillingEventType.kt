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
package com.casecode.pos.core.model.data.business

enum class BillingEventType(val value: Int) {
    CHARGE_SUCCESSFUL(0),
    CHARGE_FAILED(1),
    REFUND_ISSUED(2),
    PLAN_UPGRADED(3),
    PLAN_DOWNGRADED(4),
    PLAN_CANCELED(5),
    TRIAL_STARTED(6),
    TRIAL_ENDED(7),
    CREDIT_APPLIED(8),
    CREDIT_ADJUSTMENT(9),
    PAYMENT_METHOD_UPDATED(10),
    ;

    companion object {
        fun fromValue(value: Int): Vertical = Vertical.entries.first { it.value == value }
    }
}