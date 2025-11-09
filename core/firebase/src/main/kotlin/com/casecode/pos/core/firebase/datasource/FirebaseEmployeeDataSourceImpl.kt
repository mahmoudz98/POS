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
package com.casecode.pos.core.firebase.datasource

import com.casecode.pos.core.firebase.BUSINESS_COMPANY_CODE_FIELD
import com.casecode.pos.core.firebase.datasource.FirebaseBusinessDataSourceImpl.Companion.BUSINESSES_COLLECTION_PATH
import com.casecode.pos.core.firebase.model.NetworkBusiness
import com.casecode.pos.core.firebase.model.NetworkEmployee
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface EmployeeNetworkDataSource {

    /**
     * Finds the parent business by its company code.
     * @return The `NetworkBusiness` DTO or null if not found.
     */
    suspend fun findBusinessByCompanyCode(companyCode: String): NetworkBusiness?

    /**
     * Finds a specific employee within a given business by their login identifier.
     * @return The `NetworkEmployee` DTO or null if not found.
     */
    suspend fun findEmployeeByIdentifier(
        businessId: String,
        employeeIdentifier: String,
    ): NetworkEmployee?

    suspend fun addEmployee(businessId: String, employee: NetworkEmployee)
    suspend fun getEmployees(businessId: String): List<NetworkEmployee>
}

class FirebaseEmployeeDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore,
) : EmployeeNetworkDataSource {
    override suspend fun findBusinessByCompanyCode(companyCode: String): NetworkBusiness? {
        return db.collection(BUSINESSES_COLLECTION_PATH)
            .whereEqualTo(BUSINESS_COMPANY_CODE_FIELD, companyCode.uppercase()).limit(1).get()
            .await()
            .documents.firstOrNull()?.toObject(NetworkBusiness::class.java)
    }

    override suspend fun findEmployeeByIdentifier(
        businessId: String,
        employeeIdentifier: String,
    ): NetworkEmployee? {
        return db.collection(BUSINESSES_COLLECTION_PATH).document(businessId)
            .collection(EMPLOYEES_SUBCOLLECTION_PATH)
            .whereEqualTo(EMPLOYEE_ID_FIELD, employeeIdentifier).limit(1).get().await()
            .documents.firstOrNull()?.toObject(NetworkEmployee::class.java)
    }
    override suspend fun addEmployee(
        businessId: String,
        employee: NetworkEmployee,
    ) {
        db.collection(BUSINESSES_COLLECTION_PATH).document(businessId)
            .collection(EMPLOYEES_SUBCOLLECTION_PATH).document()
            .set(employee).await()
    }

    override suspend fun getEmployees(businessId: String): List<NetworkEmployee> {
        TODO("Not yet implemented")
    }

    internal companion object {
        const val EMPLOYEES_SUBCOLLECTION_PATH = "employees"
        const val EMPLOYEE_ID_FIELD = "id"
    }
}
