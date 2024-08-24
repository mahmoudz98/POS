package com.casecode.pos.feature.sales_report

import com.casecode.pos.core.model.data.users.Invoice

sealed interface UISalesReportDetails {
    data object Loading : UISalesReportDetails

    data object Empty : UISalesReportDetails

    data class Success(
        val invoice: Invoice,
    ) : UISalesReportDetails
}