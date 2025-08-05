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
package com.casecode.pos.core.data.repository.business

import com.casecode.pos.core.common.AppDispatchers
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.data.model.asExternalModel
import com.casecode.pos.core.domain.repository.business.EmployeeRepository
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.firebase.datasource.EmployeeNetworkDataSource
import com.casecode.pos.core.model.business.Employee
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EmployeeRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val logService: LogService,
    private val network: EmployeeNetworkDataSource,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : EmployeeRepository {

    override suspend fun authenticateEmployee(
        companyCode: String,
        employeeIdentifier: String,
        password: String,
    ): Result<Pair<String, Employee>?> = withContext(ioDispatcher) {
        runCatching {
            val networkBusiness = network.findBusinessByCompanyCode(companyCode)
            if (networkBusiness == null) {
                logService.log("EmployeeRepo: No employee found with ID: $employeeIdentifier")
                return@runCatching null
            }
            logService.log("EmployeeRepo: Finding employee '$employeeIdentifier' in business ${networkBusiness.id}")
            val networkEmployee =
                network.findEmployeeByIdentifier(networkBusiness.id, employeeIdentifier)

            if (networkEmployee == null) {
                logService.log("EmployeeRepo: No employee found with ID: $employeeIdentifier")
                return@runCatching null
            }
            Pair(networkBusiness.id, networkEmployee.asExternalModel(networkBusiness.id))
        }.onFailure { e ->
            logService.logNonFatalCrash(e)
        }
    }
}
