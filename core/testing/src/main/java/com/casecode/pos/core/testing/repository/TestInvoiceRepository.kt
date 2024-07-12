package com.casecode.pos.core.testing.repository

import com.casecode.pos.core.data.R
import com.casecode.pos.core.domain.repository.InvoiceRepository
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Invoice
import com.casecode.pos.core.model.data.users.InvoiceGroup
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.testing.base.BaseTestRepository
import javax.inject.Inject

class TestInvoiceRepository @Inject constructor() : InvoiceRepository, BaseTestRepository() {
    override suspend fun addInvoice(invoice: Invoice): Resource<Int> {
        if (shouldReturnError) {
            return Resource.error(R.string.add_invoice_failure)
        }
        return Resource.success(R.string.add_invoice_successfully)
    }

    override suspend fun getInvoices(): Resource<List<InvoiceGroup>> {
        if (shouldReturnError) {
            return Resource.error(R.string.get_invoice_failure)
        }
        if (shouldReturnEmpty) {
            return Resource.empty()
        }
        return Resource.success(fakeInvoiceGroup)
    }

    val fakeInvoiceGroup =
        listOf(
            InvoiceGroup(
                "22-12-2023",
                listOf(
                    Invoice(
                        items = arrayListOf(
                            Item("item #1", 1.0, 23.0, "1234567899090", "EA", "www.image1.png"),
                            Item("item #2", 3.0, 4.0, "1555567899090", "EA", "www.image2.png"),
                            Item("item #2", 3.0, 0.0, "1200", "EA", "www.image2.png"),
                        ),
                    ),
                ),
            ),
            InvoiceGroup(
                "2-11-2023",
                listOf(
                    Invoice(
                        items = arrayListOf(
                            Item("item #1", 1.0, 23.0, "1234567899090", "EA", "www.image1.png"),
                            Item("item #2", 3.0, 312.0, "1555567899090", "EA", "www.image2.png"),
                            Item("item #2", 3.0, 0.0, "1200", "EA", "www.image2.png"),
                        ),
                    ),
                ),
            ),
            InvoiceGroup(
                "2-2-2024",
                listOf(
                    Invoice(
                        items = arrayListOf(
                            Item("item #1", 1.0, 23.0, "1234567899090", "EA", "www.image1.png"),
                            Item("item #2", 3.0, 12.0, "1555567899090", "EA", "www.image2.png"),
                            Item("item #2", 3.0, 0.0, "1200", "EA", "www.image2.png"),
                        ),
                    ),
                ),
            ),
            InvoiceGroup(
                "22-4-2024",
                listOf(
                    Invoice(
                        items = arrayListOf(
                            Item("item #1", 1.0, 26.0, "1234567899090", "EA", "www.image1.png"),
                            Item("item #2", 3.0, 12.0, "1555567899090", "EA", "www.image2.png"),
                            Item("item #2", 3.0, 0.0, "1200", "EA", "www.image2.png"),
                        ),
                    ),
                ),
            ),
        )

    override fun init() = Unit
}