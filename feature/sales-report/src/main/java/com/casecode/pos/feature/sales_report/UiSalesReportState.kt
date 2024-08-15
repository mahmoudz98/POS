package com.casecode.pos.feature.sales_report

import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.InvoiceGroup

data class UiSalesReportState(
    var resourceInvoiceGroups: Resource<List<InvoiceGroup>> = Resource.loading(),
    val dateInvoiceSelected: Long? = null,
)