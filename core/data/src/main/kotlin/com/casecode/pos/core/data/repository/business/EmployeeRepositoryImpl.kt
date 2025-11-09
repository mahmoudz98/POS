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

import android.database.sqlite.SQLiteConstraintException
import com.casecode.pos.core.common.AppDispatchers
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.data.model.OutboxPayload
import com.casecode.pos.core.data.model.asNetworkModel
import com.casecode.pos.core.data.utils.PasswordUtils
import com.casecode.pos.core.database.dao.BusinessDao
import com.casecode.pos.core.database.dao.EmployeeDao
import com.casecode.pos.core.database.dao.OutboxCommandDao
import com.casecode.pos.core.database.model.OutboxCommandEntity
import com.casecode.pos.core.database.model.OutboxCommandStatus
import com.casecode.pos.core.database.model.OutboxEventType
import com.casecode.pos.core.database.model.asEntity
import com.casecode.pos.core.database.model.asExternalModel
import com.casecode.pos.core.database.util.DatabaseTransactionRunner
import com.casecode.pos.core.domain.exceptions.EmployeeNameCollisionException
import com.casecode.pos.core.domain.repository.business.EmployeeRepository
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.firebase.datasource.BusinessNetworkDataSource
import com.casecode.pos.core.firebase.datasource.EmployeeNetworkDataSource
import com.casecode.pos.core.firebase.datasource.InboxNetworkDataSource
import com.casecode.pos.core.firebase.model.NetworkInboxSignal
import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.model.data.SyncableEntityType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.time.Clock

class EmployeeRepositoryImpl
    @Inject
    constructor(
        private val employeeDao: EmployeeDao,
        private val businessDao: BusinessDao,
        private val outboxCommandDao: OutboxCommandDao,
        private val transaction: DatabaseTransactionRunner,
        private val json: Json,
        private val logService: LogService,
        private val employeeNetwork: EmployeeNetworkDataSource,
        private val businessNetwork: BusinessNetworkDataSource,
        private val inboxNetworkDataSource: InboxNetworkDataSource,
        @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    ) : EmployeeRepository {
        override fun getEmployees() =
            employeeDao.getEmployees().map { list ->
                list.map { it.asExternalModel() }
            }.flowOn(ioDispatcher)

        override suspend fun authenticateEmployee(
            companyCode: String,
            employeeIdentifier: String,
            password: String,
        ): Result<Pair<String, Employee>?> =
            withContext(ioDispatcher) {
                runCatching {
                    val employeeEntity = employeeDao.getEmployeeByName(employeeIdentifier)
                    if (employeeEntity == null) {
                        null // Employee not found
                    } else {
                        val isPasswordCorrect =
                            PasswordUtils.verifyPassword(password, employeeEntity.password)
                        if (isPasswordCorrect) {
                            Pair(companyCode, employeeEntity.asExternalModel())
                        } else {
                            null // Incorrect password
                        }
                    }
                }
            }

        override suspend fun createEmployee(
            employee: Employee,
            plainTextPassword: String,
            businessId: String,
        ): Result<Unit> =
            withContext(ioDispatcher) {
                runCatching {
                    val hashedPassword = PasswordUtils.hashPassword(plainTextPassword)
                    transaction.run {
                        val entity = employee.asEntity(hashedPassword)
                        employeeDao.insertOrReplaceEmployee(entity)

                        val networkEmployee = entity.asNetworkModel()
                        val outboxPayload =
                            json.encodeToString(
                                OutboxPayload.CreateEmployeePayload(
                                    businessId = businessId,
                                    employee = networkEmployee,
                                ),
                            )

                        outboxCommandDao.insertCommand(
                            OutboxCommandEntity(
                                type = OutboxEventType.Employee.CREATED,
                                payload = outboxPayload,
                            ),
                        )
                    }
                }.recoverCatching { e ->
                    logService.logNonFatalCrash(e)
                    when (e) {
                        is SQLiteConstraintException -> {
                            throw EmployeeNameCollisionException("Employee with name ${employee.name} already exists")
                        }

                        else -> throw e
                    }
                }
            }

        override suspend fun updateEmployee(
            employee: Employee,
            plainTextPassword: String,
            businessId: String,
        ): Result<Unit> =
            withContext(ioDispatcher) {
                runCatching {
                    val existingEntity =
                        employeeDao.getEmployeeById(employee.id)
                            ?: throw NoSuchElementException("Employee with ID: ${employee.id} not found")
                    val hashedPassword =
                        if (plainTextPassword.isNotBlank()) {
                            PasswordUtils.hashPassword(plainTextPassword)
                        } else {
                            existingEntity.password
                        }
                    val updateEntity = employee.asEntity(hashedPassword)

                    transaction.run {
                        employeeDao.updateEmployees(updateEntity)

                        val networkEmployee = updateEntity.asNetworkModel()
                        val outboxPayload =
                            json.encodeToString(
                                OutboxPayload.CreateEmployeePayload(
                                    businessId = businessId,
                                    employee = networkEmployee,
                                ),
                            )

                        outboxCommandDao.insertCommand(
                            OutboxCommandEntity(
                                type = OutboxEventType.Employee.UPDATED,
                                payload = outboxPayload,
                            ),
                        )
                    }
                }.recoverCatching { e ->
                    logService.logNonFatalCrash(e)
                    when (e) {
                        is SQLiteConstraintException -> {
                            throw EmployeeNameCollisionException("Employee with name ${employee.name} already exists")
                        }

                        else -> throw e
                    }
                }
            }

        override suspend fun deleteEmployee(
            id: String,
            businessId: String,
        ): Result<Unit> =
            withContext(ioDispatcher) {
                runCatching {
                    val existingEntity =
                        employeeDao.getEmployeeById(id)
                            ?: throw NoSuchElementException("Employee with ID: $id not found")

                    transaction.run {
                        employeeDao.deleteEmployee(id)
                        val networkEmployee = existingEntity.copy(isDeleted = 1).asNetworkModel()
                        val outboxPayload =
                            json.encodeToString(
                                OutboxPayload.CreateEmployeePayload(
                                    businessId = businessId,
                                    employee = networkEmployee,
                                ),
                            )

                        outboxCommandDao.insertCommand(
                            OutboxCommandEntity(
                                type = OutboxEventType.Employee.DELETED,
                                payload = outboxPayload,
                            ),
                        )
                    }
                }
            }

        override suspend fun syncUp(): Boolean =
            withContext(ioDispatcher) {
                var success = true
                try {
                    val pendingCommands = outboxCommandDao.getPendingCommands().first()
                    val employeeCommands =
                        pendingCommands.filter { it.type is OutboxEventType.Employee }

                    employeeCommands.forEach { command ->
                        try {
                            outboxCommandDao.updateCommandStatus(
                                command.id,
                                OutboxCommandStatus.PROCESSING,
                                1,
                                Clock.System.now(),
                            )
                            val payload =
                                json.decodeFromString<OutboxPayload.CreateEmployeePayload>(
                                    command.payload,
                                )
                            employeeNetwork.addEmployee(payload.businessId, payload.employee)
                            inboxNetworkDataSource.postSignal(
                                payload.businessId,
                                NetworkInboxSignal(
                                    entityType = SyncableEntityType.EMPLOYEE.name,
                                    entityId = payload.employee.id,
                                    lastUpdated = Clock.System.now().toEpochMilliseconds(),
                                ),
                            )
                            outboxCommandDao.updateCommandStatus(
                                command.id,
                                OutboxCommandStatus.COMPLETED,
                                1,
                                Clock.System.now(),
                            )
                        } catch (e: Exception) {
                            logService.log("EmployeeRepository(syncUp): Failed to process command ${command.id}. Error: ${e.message}")
                            outboxCommandDao.updateCommandStatus(
                                command.id,
                                OutboxCommandStatus.FAILED,
                                1,
                                Clock.System.now(),
                                e.message,
                            )
                            success = false
                        }
                    }
                } catch (e: Exception) {
                    logService.log("EmployeeRepository(syncUp): Failed with exception: ${e.message}")
                    success = false
                }
                success
            }

        override suspend fun syncDown(): Boolean {
            // TODO: Implement syncDown logic for employees
            return true
        }
    }
