package com.casecode.pos.sync

import com.casecode.pos.core.analytics.AnalyticsEvent
import com.casecode.pos.core.analytics.AnalyticsHelper

internal fun AnalyticsHelper.logSyncSupplierInvoicesOverdueStarted() =
    logEvent(
        AnalyticsEvent(type = "overdue_sync_started"),
    )

internal fun AnalyticsHelper.logSyncSupplierInvoicesOverdueFinished(hasOverdue: Boolean) {
    val eventType = if (hasOverdue) "hasOverdue" else "hasn't Overdue"
    logEvent(
        AnalyticsEvent(type = eventType),
    )
}