package com.casecode.pos.feature.bills

import com.casecode.pos.core.domain.usecase.AddSupplierInvoiceUseCase
import com.casecode.pos.core.domain.usecase.GetItemsUseCase
import com.casecode.pos.core.domain.usecase.GetSupplierInvoicesUseCase
import com.casecode.pos.core.domain.usecase.GetSuppliersUseCase
import com.casecode.pos.core.domain.usecase.UpdateSupplierInvoiceUseCase
import com.casecode.pos.core.testing.repository.TestItemRepository
import com.casecode.pos.core.testing.repository.TestSupplierInvoicesRepository
import com.casecode.pos.core.testing.repository.TestSupplierRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import com.casecode.pos.core.testing.util.TestNetworkMonitor
import org.junit.Before
import org.junit.Rule


class BillViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()
    private val itemRepository = TestItemRepository()
    private val supplierRepository = TestSupplierRepository()
    private val invoiceSuppliers = TestSupplierInvoicesRepository()

    private val getItemsUseCase: GetItemsUseCase = GetItemsUseCase(itemRepository)
    private val getSuppliers = GetSuppliersUseCase(supplierRepository)
    private val getSupplierInvoices = GetSupplierInvoicesUseCase(invoiceSuppliers)
    private val addSupplierInvoice = AddSupplierInvoiceUseCase(invoiceSuppliers)
    private val updateSupplierInvoice = UpdateSupplierInvoiceUseCase(invoiceSuppliers)
    private val networkMonitor = TestNetworkMonitor()
    private lateinit var viewModel: BillViewModel

@Before
fun setup(){
    viewModel = BillViewModel(
        networkMonitor,
        getItemsUseCase,
        getSuppliers,
        getSupplierInvoices,
        addSupplierInvoice,
        updateSupplierInvoice
    )

}
}