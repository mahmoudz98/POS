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
package com.casecode.pos.feature.supplier

import com.casecode.pos.core.domain.usecase.AddSupplierUseCase
import com.casecode.pos.core.domain.usecase.DeleteSupplierUseCase
import com.casecode.pos.core.domain.usecase.GetSuppliersUseCase
import com.casecode.pos.core.domain.usecase.UpdateSupplierUseCase
import com.casecode.pos.core.testing.repository.TestSupplierRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import com.casecode.pos.core.testing.util.TestNetworkMonitor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import com.casecode.pos.core.data.R.string as dataString
import com.casecode.pos.core.ui.R.string as uiString

class SupplierViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private lateinit var viewModel: SupplierViewModel
    private val supplierRepository = TestSupplierRepository()
    private val networkMonitor = TestNetworkMonitor()
    private val getSuppliers = GetSuppliersUseCase(supplierRepository)
    private val addSupplier = AddSupplierUseCase(supplierRepository)
    private val updateSupplier = UpdateSupplierUseCase(supplierRepository)
    private val deleteSupplier = DeleteSupplierUseCase(supplierRepository)

    @Before
    fun setup() {
        viewModel = SupplierViewModel(
            networkMonitor,
            getSuppliers,
            addSupplier,
            updateSupplier,
            deleteSupplier,
        )
    }

    @Test
    fun supplierUiStateIsSuccessAfterLoadingState() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.suppliersUiState.collect() }
        assertEquals(SuppliersUiState.Loading, viewModel.suppliersUiState.value)

        val actualSuppliers = supplierRepository.suppliersTest
        supplierRepository.sendSuppliers()
        assertEquals(SuppliersUiState.Success(actualSuppliers), viewModel.suppliersUiState.value)
    }

    @Test
    fun supplierUiStateIsEmptyAfterLoadingState() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.suppliersUiState.collect() }
        assertEquals(SuppliersUiState.Loading, viewModel.suppliersUiState.value)
        supplierRepository.setReturnEmpty(true)
        assertEquals(SuppliersUiState.Empty, viewModel.suppliersUiState.value)
    }

    @Test
    fun supplierUiStateIsErrorAfterLoadingState() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.suppliersUiState.collect() }
        assertEquals(SuppliersUiState.Loading, viewModel.suppliersUiState.value)
        supplierRepository.setReturnError(true)
        assertEquals(SuppliersUiState.Error, viewModel.suppliersUiState.value)
    }

    @Test
    fun addSupplier_whenHasSupplierAndNetworkAvailable_returnsSameSupplier() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.suppliersUiState.collect()
        }
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.userMessage.collect() }
        val newSupplier = supplierRepository.suppliersTest[0]
        networkMonitor.setConnected(true)
        viewModel.addSupplier(newSupplier)
        assertEquals(
            viewModel.userMessage.value,
            R.string.feature_supplier_add_success_message,
        )
        assertEquals(
            (viewModel.suppliersUiState.value as SuppliersUiState.Success).suppliers.last(),
            newSupplier,
        )
    }

    @Test
    fun addSupplier_whenHasSupplierAndNetworkNotAvailable_returnsSameSupplier() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.userMessage.collect() }
        val newSupplier = supplierRepository.suppliersTest[0]
        networkMonitor.setConnected(false)
        viewModel.addSupplier(newSupplier)
        assertEquals(
            viewModel.userMessage.value,
            uiString.core_ui_error_network,
        )
    }

    @Test
    fun addSupplier_whenHasError_returnErrorMessage() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.userMessage.collect() }
        val newSupplier = supplierRepository.suppliersTest[0]
        supplierRepository.setReturnError(true)
        networkMonitor.setConnected(true)
        viewModel.addSupplier(newSupplier)
        assertEquals(
            viewModel.userMessage.value,
            dataString.core_data_add_supplier_failure_generic,
        )
    }

    @Test
    fun updateSupplier_whenChangeSupplierAndNetworkAvailable_returnSuccessMessage() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.supplierSelected.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.suppliersUiState.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.userMessage.collect() }
        supplierRepository.sendSuppliers()
        val oldSupplier = supplierRepository.suppliersTest[0]
        viewModel.onSelectSupplier(oldSupplier)

        val newSupplier = oldSupplier.copy(contactName = "new name")
        networkMonitor.setConnected(true)
        viewModel.updateSupplier(newSupplier)

        assertEquals(
            R.string.feature_supplier_update_success_message,
            viewModel.userMessage.value,
        )
        val actualSupplier =
            (viewModel.suppliersUiState.value as SuppliersUiState.Success).suppliers.last()
        assertEquals(
            newSupplier,
            actualSupplier,
        )
    }

    @Test
    fun updateSupplier_whenChangeSupplierAndNetworkNotAvailable_returnErrorMessage() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.supplierSelected.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.userMessage.collect() }
        val oldSupplier = supplierRepository.suppliersTest[0]
        viewModel.onSelectSupplier(oldSupplier)
        val newSupplier = oldSupplier.copy(contactName = "new supplier")
        networkMonitor.setConnected(false)
        viewModel.updateSupplier(newSupplier)
        assertEquals(
            uiString.core_ui_error_network,
            viewModel.userMessage.value,
        )
    }

    @Test
    fun updateSupplier_whenNotChangeSupplierAndNetworkAvailable_returnErrorMessage() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.supplierSelected.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.userMessage.collect() }
        supplierRepository.sendSuppliers()
        val oldSupplier = supplierRepository.suppliersTest[0]
        viewModel.onSelectSupplier(oldSupplier)
        networkMonitor.setConnected(true)
        viewModel.updateSupplier(oldSupplier.copy())
        assertEquals(
            R.string.feature_supplier_update_error_message,
            viewModel.userMessage.value,
        )
    }

    @Test
    fun deleteSupplier_whenHasSupplierAndNetworkAvailable_returnSuccessMessage() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.userMessage.collect() }
        val supplier = supplierRepository.suppliersTest[0]
        networkMonitor.setConnected(true)
        viewModel.deleteSupplier(supplier)
        assertEquals(
            R.string.feature_supplier_delete_success_message,
            viewModel.userMessage.value,
        )
    }

    @Test
    fun deleteSupplier_whenHasSupplierAndNetworkNotAvailable_returnErrorMessage() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.userMessage.collect() }
        val supplier = supplierRepository.suppliersTest[0]
        networkMonitor.setConnected(false)
        viewModel.deleteSupplier(supplier)
        assertEquals(
            uiString.core_ui_error_network,
            viewModel.userMessage.value,
        )
    }
}