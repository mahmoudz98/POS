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

import com.casecode.pos.core.firebase.EMPLOYEE_NAME_FIELD
import com.casecode.pos.core.firebase.datasource.FirebaseBusinessDataSourceImpl.Companion.getBusinessDocument
import com.casecode.pos.core.firebase.model.NetworkEmployee
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface EmployeeNetworkDataSource {

    /**
     * Finds a specific employee within a given business by their login identifier.
     * @return The `NetworkEmployee` DTO or null if not found.
     */
    suspend fun findEmployeeByIdentifier(
        businessId: String,
        employeeIdentifier: String,
    ): NetworkEmployee?

    suspend fun addEmployee(businessId: String, employee: NetworkEmployee)
    suspend fun updateEmployee(businessId: String, employee: NetworkEmployee)
    suspend fun deleteEmployee(businessId: String, employee: NetworkEmployee)

    suspend fun getEmployees(businessId: String): List<NetworkEmployee>
}

class FirebaseEmployeeDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore,
) : EmployeeNetworkDataSource {

    override suspend fun findEmployeeByIdentifier(
        businessId: String,
        employeeIdentifier: String,
    ): NetworkEmployee? {
        return db.getBusinessDocument(businessId)
            .collection(EMPLOYEES_SUBCOLLECTION_PATH)
            .where(
                Filter.and(
                    Filter.or(
                        Filter.equalTo(EMPLOYEE_ID_FIELD, employeeIdentifier),
                        Filter.equalTo(EMPLOYEE_NAME_FIELD, employeeIdentifier),
                    ),
                    Filter.equalTo(EMPLOYEES_IS_ACTIVE_FIELD, NOT_ACTIVE_EMPLOYEE_VALUE),
                ),
            ).limit(1).get().await()
            .documents.firstOrNull()?.toObject(NetworkEmployee::class.java)
    }

    override suspend fun addEmployee(
        businessId: String,
        employee: NetworkEmployee,
    ) {
        db.getBusinessDocument(businessId)
            .collection(EMPLOYEES_SUBCOLLECTION_PATH).document(employee.id)
            .set(employee).await()
    }

    /**
     * Updates an existing employee's data in Firestore.
     * Uses `SetOptions.merge()` to update the document with the provided `NetworkEmployee` object,
     * merging with any existing fields.
     *
     * @param businessId The ID of the business the employee belongs to.
     * @param employee The `NetworkEmployee` object containing the updated data.
     */
    override suspend fun updateEmployee(
        businessId: String,
        employee: NetworkEmployee,
    ) {
        db.getBusinessDocument(businessId)
            .collection(EMPLOYEES_SUBCOLLECTION_PATH).document(employee.id)
            .set(employee, SetOptions.merge()).await()
    }

    override suspend fun deleteEmployee(
        businessId: String,
        employee: NetworkEmployee,
    ) {
        db.getBusinessDocument(businessId)
            .collection(EMPLOYEES_SUBCOLLECTION_PATH).document(employee.id)
            .update(EMPLOYEES_IS_ACTIVE_FIELD, ACTIVE_EMPLOYEE_VALUE).await()
    }

    override suspend fun getEmployees(businessId: String): List<NetworkEmployee> {
        return db.getBusinessDocument(businessId)
            .collection(EMPLOYEES_SUBCOLLECTION_PATH)
            .whereEqualTo(EMPLOYEES_IS_ACTIVE_FIELD, NOT_ACTIVE_EMPLOYEE_VALUE)
            .get().await().toObjects<NetworkEmployee>()
    }

    internal companion object {
        const val EMPLOYEES_SUBCOLLECTION_PATH = "employees"
        const val EMPLOYEES_IS_ACTIVE_FIELD = "is_active"
        const val NOT_ACTIVE_EMPLOYEE_VALUE = 1
        const val ACTIVE_EMPLOYEE_VALUE = 0
        const val EMPLOYEE_ID_FIELD = "id"
    }
}
