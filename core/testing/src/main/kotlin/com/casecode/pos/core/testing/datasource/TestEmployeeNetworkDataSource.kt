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
package com.casecode.pos.core.testing.datasource

import com.casecode.pos.core.firebase.datasource.EmployeeNetworkDataSource
import com.casecode.pos.core.firebase.model.NetworkBusiness
import com.casecode.pos.core.firebase.model.NetworkEmployee

class TestEmployeeNetworkDataSource : EmployeeNetworkDataSource {
    var createdEmployee: NetworkEmployee? = null
        private set

    override suspend fun findBusinessByCompanyCode(companyCode: String): NetworkBusiness? {
        TODO("Not yet implemented")
    }

    override suspend fun findEmployeeByIdentifier(
        businessId: String,
        employeeIdentifier: String,
    ): NetworkEmployee? {
        TODO("Not yet implemented")
    }

    override suspend fun addEmployee(
        businessId: String,
        employee: NetworkEmployee,
    ) {
        createdEmployee = employee
    }

    override suspend fun getEmployees(businessId: String): List<NetworkEmployee> {
        TODO("Not yet implemented")
    }
}
