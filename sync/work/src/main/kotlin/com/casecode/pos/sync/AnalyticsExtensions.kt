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
package com.casecode.pos.sync

import com.casecode.pos.core.analytics.AnalyticsEvent
import com.casecode.pos.core.analytics.AnalyticsHelper

internal fun AnalyticsHelper.logSyncSupplierInvoicesOverdueStarted() = logEvent(
    AnalyticsEvent(type = "overdue_sync_started"),
)

internal fun AnalyticsHelper.logSyncSupplierInvoicesOverdueFinished(hasOverdue: Boolean) {
    val eventType = if (hasOverdue) "hasOverdue" else "hasn't Overdue"
    logEvent(
        AnalyticsEvent(type = eventType),
    )
}