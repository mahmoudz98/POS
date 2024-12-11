package com.casecode.pos.core.domain.usecase.supplierinvoice

import com.casecode.pos.core.domain.usecase.GetSupplierInvoiceDetailsUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.testing.repository.TestSupplierInvoicesRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class GetSupplierInvoiceDetailsUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private val testRepo = TestSupplierInvoicesRepository()
    private val getSupplierInvoiceDetailsUseCase = GetSupplierInvoiceDetailsUseCase(testRepo)

    @Test
    fun `whenHasSupplierId_returnsSupplierInvoice`() = runTest {
        val supplierInvoiceResult = getSupplierInvoiceDetailsUseCase("INV001")
        backgroundScope.launch(UnconfinedTestDispatcher()){supplierInvoiceResult.collect{}}
        assertEquals(
            Resource.Success(testRepo.supplierInvoicesTest[0]),
            supplierInvoiceResult.last(),
        )
    }
}