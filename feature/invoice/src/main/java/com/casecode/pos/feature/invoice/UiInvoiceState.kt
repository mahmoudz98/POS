package com.casecode.pos.feature.invoice

import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.InvoiceGroup

data class UiInvoiceState(
    var resourceInvoiceGroups: Resource<List<InvoiceGroup>> = Resource.loading(),
    val dateInvoiceSelected: Long? = null,
)