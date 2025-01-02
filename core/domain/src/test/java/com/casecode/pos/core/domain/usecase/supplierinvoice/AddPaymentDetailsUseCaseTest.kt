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
package com.casecode.pos.core.domain.usecase.supplierinvoice

import com.casecode.pos.core.domain.usecase.AddPaymentDetailsUseCase
import com.casecode.pos.core.domain.utils.OperationResult
import com.casecode.pos.core.model.data.users.PaymentDetails
import com.casecode.pos.core.model.data.users.PaymentMethod
import com.casecode.pos.core.model.data.users.PaymentStatus
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.testing.repository.TestSupplierInvoicesRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class AddPaymentDetailsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private val testRepo = TestSupplierInvoicesRepository()
    private val addPaymentDetailsUseCase = AddPaymentDetailsUseCase(testRepo)

    @Test
    fun `when payment details added successfully, returns success`() = runTest {
        val paymentDetails = createPaymentDetails(amountPaid = 400.0)
        val result = addPaymentDetailsUseCase(
            testRepo.supplierInvoicesTest[0],
            paymentDetails,
        )
        assertEquals(OperationResult.Success, result)
    }

    @Test
    fun `when repository returns error, returns failure`() = runTest {
        val paymentDetails = createPaymentDetails(amountPaid = 400.0)
        testRepo.setReturnError(true)
        val result = addPaymentDetailsUseCase(
            testRepo.supplierInvoicesTest[0],
            paymentDetails,
        )
        assertTrue(result is OperationResult.Failure)
    }

    @Test
    fun `when payment fully covers invoice, sets status to PAID`() = runTest {
        val invoice = getInvoice()
        val paymentDetails = createPaymentDetails(amountPaid = 1000.0)

        val result = addPaymentDetailsUseCase(invoice, paymentDetails)

        assertEquals(OperationResult.Success, result)
        assertEquals(PaymentStatus.PAID, testRepo.supplierInvoicesTest[0].paymentStatus)
    }

    @Test
    fun `when payment is partial and overdue, sets status to overdue`() = runTest {
        val invoice = getInvoice()
        val paymentDetails = createPaymentDetails(amountPaid = 500.0)

        val result = addPaymentDetailsUseCase(invoice, paymentDetails)

        assertEquals(OperationResult.Success, result)
        assertEquals(PaymentStatus.OVERDUE, testRepo.supplierInvoicesTest[0].paymentStatus)
    }

    @Test
    fun `when payment is partial and not overdue, sets status to PARTIALLY_PAID`() = runTest {
        val invoice = getInvoice(2)
        val paymentDetails = createPaymentDetails(amountPaid = 300.0)
        val result = addPaymentDetailsUseCase(invoice, paymentDetails)
        assertEquals(OperationResult.Success, result)
        assertEquals(PaymentStatus.PARTIALLY_PAID, testRepo.supplierInvoicesTest[2].paymentStatus)
    }

    @Test
    fun `when payment is after due date, sets status to OVERDUE`() = runTest {
        val invoice = getInvoice()
        val paymentDetails = createPaymentDetails(amountPaid = 500.0)

        val result = addPaymentDetailsUseCase(invoice, paymentDetails)

        assertEquals(OperationResult.Success, result)
        assertEquals(PaymentStatus.OVERDUE, testRepo.supplierInvoicesTest[0].paymentStatus)
    }

    private fun createPaymentDetails(amountPaid: Double): PaymentDetails = PaymentDetails(
        paymentId = "test-payment-id",
        paymentDate = Clock.System.now(),
        createdBy = "TestUser",
        paymentMethod = PaymentMethod.CASH,
        amountPaid = amountPaid,
    )

    private fun getInvoice(index: Int = 0): SupplierInvoice = testRepo.supplierInvoicesTest[index]
}