package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.testing.repository.TestInvoiceRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.Rule
import org.junit.Test
import com.casecode.pos.core.data.R.string as stringData
import com.casecode.pos.core.domain.R.string as stringDomain

class InvoiceUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private val testInvoiceRepository = TestInvoiceRepository()
    private val addInvoiceUseCase = AddInvoiceUseCase(testInvoiceRepository)
    private val getInvoicesUseCase = GetInvoicesUseCase(testInvoiceRepository)
    var fakeItems =
        arrayListOf(
            Item("item #1", 1.0, 23.0, "1234567899090", "EA", "www.image1.png"),
            Item("item #2", 3.0, 421312.0, "1555567899090", "EA", "www.image2.png"),
            Item("item #2", 3.0, 0.0, "1200", "EA", "www.image2.png"),
        )

    @Test
    fun addInvoiceUseCase_whenHasInvoices_returnMessageAddedInvoice() =
        runTest {
            // Given
            val items = fakeItems
            // When
            val result = addInvoiceUseCase(items).last()
            // Then
            MatcherAssert.assertThat(
                result,
                `is`(Resource.success(stringData.core_data_add_invoice_successfully)),
            )
        }

    @Test
    fun addInvoiceUseCase_InputEmptyItems_returnMessageEmptyItems() =
        runTest {
            // Given
            val items = listOf<Item>()
            // When
            val result = addInvoiceUseCase(items).last()
            // Then
            MatcherAssert.assertThat(
                result,
                `is`(Resource.empty(message = stringDomain.invoice_items_empty)),
            )
        }

    @Test
    fun addInvoiceUseCase_hasError_returnMessageError() =
        runTest {
            // Given
            val items = fakeItems
            // When
            testInvoiceRepository setReturnError true
            val result = addInvoiceUseCase(items).last()
            // Then
            MatcherAssert.assertThat(
                result,
                `is`(Resource.error(stringData.core_data_add_invoice_failure)),
            )
        }

    @Test
    fun getInvoiceUseCase_hasInvoices_returnListOfInvoices() =
        runTest {
            // When
            val result = getInvoicesUseCase().last()
            // Then
            MatcherAssert.assertThat(
                result,
                `is`(Resource.success(testInvoiceRepository.fakeInvoiceGroup)),
            )
        }

    @Test
    fun getInvoiceUseCase_whenHasError_returnError() =
        runTest {
            // When
            testInvoiceRepository setReturnError true
            val result = getInvoicesUseCase().last()
            // Then
            MatcherAssert.assertThat(
                result,
                `is`(Resource.error(stringData.core_data_get_invoice_failure)),
            )
        }

    @Test
    fun getInvoiceUseCase_whenEmptyInvoices_returnEmpty() =
        runTest {
            // When
            testInvoiceRepository setReturnEmpty true
            val result = getInvoicesUseCase().last()
            // Then
            MatcherAssert.assertThat(result, `is`(Resource.empty()))
        }
}