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
import com.casecode.pos.core.analytics.AnalyticsEvent
import com.casecode.pos.core.analytics.AnalyticsEvent.Param
import com.casecode.pos.core.analytics.AnalyticsHelper
import com.casecode.pos.core.common.AppDispatchers
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.data.model.OutboxPayload
import com.casecode.pos.core.data.model.asEntity
import com.casecode.pos.core.data.model.asNetworkModel
import com.casecode.pos.core.data.utils.PasswordUtils
import com.casecode.pos.core.database.dao.EmployeeDao
import com.casecode.pos.core.database.dao.LocalSignalDao
import com.casecode.pos.core.database.dao.OutboxCommandDao
import com.casecode.pos.core.database.model.EmployeeEntity
import com.casecode.pos.core.database.model.LocalSignalStatus
import com.casecode.pos.core.database.model.OutboxCommandEntity
import com.casecode.pos.core.database.model.OutboxCommandStatus
import com.casecode.pos.core.database.model.OutboxEventType
import com.casecode.pos.core.database.model.asEntity
import com.casecode.pos.core.database.model.asExternalModel
import com.casecode.pos.core.database.util.DatabaseTransactionRunner
import com.casecode.pos.core.domain.exceptions.EmployeeIdCollisionException
import com.casecode.pos.core.domain.exceptions.EmployeeNotFoundException
import com.casecode.pos.core.domain.exceptions.InvalidPasswordException
import com.casecode.pos.core.domain.repository.business.EmployeeRepository
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.firebase.datasource.EmployeeNetworkDataSource
import com.casecode.pos.core.firebase.datasource.InboxNetworkDataSource
import com.casecode.pos.core.firebase.model.NetworkInboxSignal
import com.casecode.pos.core.firebase.model.OperationType
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

/**
 * Implementation of [EmployeeRepository] that provides offline-first access to employee data.
 * This repository follows a Just-In-Time (JIT) caching strategy for fetching individual employee records,
 * prioritizing local data and fetching from the network only on a cache miss.
 * Changes are synchronized with a remote data source (Firestore) via the Outbox Pattern.
 */
class EmployeeRepositoryImpl
@Inject
constructor(
    private val employeeDao: EmployeeDao,
    private val localSignalDao: LocalSignalDao,
    private val employeeNetwork: EmployeeNetworkDataSource,
    private val outboxCommandDao: OutboxCommandDao,
    private val transaction: DatabaseTransactionRunner,
    private val json: Json,
    private val logService: LogService,
    private val analyticsHelper: AnalyticsHelper,
    private val inboxNetworkDataSource: InboxNetworkDataSource,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : EmployeeRepository {
    /**
     * Retrieves all non-deleted employees from the local database as a reactive [kotlinx.coroutines.flow.Flow].
     * This flow emits the latest employee data from the local database.
     *
     * @return A [kotlinx.coroutines.flow.Flow] emitting a list of [Employee] domain models.
     */
    override fun getEmployees() =
        employeeDao.getEmployees().map { list ->
            list.map { it.asExternalModel() }
        }.flowOn(ioDispatcher)

    /**
     * Implements the Just-In-Time (JIT) caching strategy to find an employee.
     * It first attempts to retrieve the employee from the local database.
     * If not found locally, it fetches from the network, saves it to the local cache, and then returns it.
     *
     * @param employeeIdentifier The ID or name of the employee to find.
     * @param businessId The ID of the business the employee belongs to.
     * @return The [EmployeeEntity] if found, or `null` otherwise.
     */
    private suspend fun findEmployee(
        employeeIdentifier: String,
        businessId: String,
    ): EmployeeEntity? {
        val localEmployee = employeeDao.getEmployeeById(employeeIdentifier, businessId)
        if (localEmployee != null) {
            return localEmployee
        }
        val networkEmployee =
            employeeNetwork.findEmployeeByIdentifier(businessId, employeeIdentifier)
                ?: return null
        val entity = networkEmployee.asEntity(businessId)
        employeeDao.insertOrReplaceEmployee(entity)
        return entity
    }

    /**
     * Authenticates an employee against the stored credentials.
     * This method first attempts to find the employee using the JIT caching strategy.
     * If the employee is found, it verifies the provided password against the stored hashed password.
     *
     * @param businessId The ID of the business the employee belongs to.
     * @param employeeIdentifier The ID or name of the employee.
     * @param password The plain-text password provided by the user.
     * @return A [Result] containing the [Employee] domain model if authentication is successful.
     * @throws EmployeeNotFoundException if the employee is not found.
     * @throws InvalidPasswordException if the password is incorrect.
     */
    override suspend fun authenticateEmployee(
        businessId: String,
        employeeIdentifier: String,
        password: String,
    ): Result<Employee> {
        return withContext(ioDispatcher) {
            runCatching {
                val employeeEntity = findEmployee(employeeIdentifier, businessId)
                    ?: throw EmployeeNotFoundException(
                        "Employee with identifier '$employeeIdentifier' not found in business '$businessId'",
                    )

                // Verify the provided password against the stored hashed password
                if (PasswordUtils.verifyPassword(password, employeeEntity.password)) {
                    employeeEntity.asExternalModel()
                } else {
                    throw InvalidPasswordException(
                        "Invalid password for employee '$employeeIdentifier'",
                    )
                }
            }
        }
    }
    override suspend fun getNextIdSuggestion(): Result<String> {
        return withContext(ioDispatcher) {
            runCatching {
                val maxId = employeeDao.getHighestEmployeeId() ?: 1000
                (maxId + 1).toString()
            }
        }
    }

    /**
     * Creates a new employee record.
     * This operation is performed within a database transaction to ensure atomicity:
     * 1. The employee is saved to the local database (hashed password).
     * 2. An outbox command is queued to synchronize this creation with the remote data source.
     *
     * @param employee The [Employee] domain model to create.
     * @param plainTextPassword The plain-text password for the new employee.
     * @param businessId The ID of the business this employee belongs to.
     * @return A [Result] indicating success or failure.
     *         Returns [EmployeeIdCollisionException] if an employee with the same name already exists in the business.
     */
    override suspend fun createEmployee(
        employee: Employee,
        plainTextPassword: String,
        businessId: String,
    ): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                val hashedPassword = PasswordUtils.hashPassword(plainTextPassword)
                transaction.run {
                    val entity = employee.asEntity(hashedPassword, businessId)
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
                        throw EmployeeIdCollisionException("Employee with name ${employee.name} already exists")
                    }

                    else -> throw e
                }
            }
        }

    /**
     * Updates an existing employee record.
     * This operation is performed within a database transaction to ensure atomicity:
     * 1. The employee is updated in the local database.
     * 2. An outbox command is queued to synchronize this update with the remote data source.
     *
     * @param employee The [Employee] domain model with updated information.
     * @param plainTextPassword The new plain-text password for the employee. If blank, the old password is retained.
     * @param businessId The ID of the business this employee belongs to.
     * @return A [Result] indicating success or failure.
     *         Returns [NoSuchElementException] if the employee is not found.
     *         Returns [EmployeeIdCollisionException] if the updated name conflicts with an existing employee.
     */
    override suspend fun updateEmployee(
        employee: Employee,
        plainTextPassword: String,
        businessId: String,
    ): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                val existingEntity =
                    employeeDao.getEmployeeById(employee.id, businessId)
                        ?: throw NoSuchElementException("Employee with ID: ${employee.id} not found")
                val hashedPassword =
                    if (plainTextPassword.isNotBlank()) {
                        PasswordUtils.hashPassword(plainTextPassword)
                    } else {
                        existingEntity.password
                    }
                val updateEntity = employee.asEntity(hashedPassword, businessId)

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
            }
        }

    /**
     * Soft-deletes an employee record.
     * This operation is performed within a database transaction to ensure atomicity:
     * 1. The employee is marked as deleted in the local database.
     * 2. An outbox command is queued to synchronize this deletion with the remote data source.
     *
     * @param id The ID of the employee to delete.
     * @param businessId The ID of the business this employee belongs to.
     * @return A [Result] indicating success or failure.
     *         Returns [NoSuchElementException] if the employee is not found.
     */
    override suspend fun deleteEmployee(
        id: String,
        businessId: String,
    ): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                val existingEntity =
                    employeeDao.getEmployeeById(id, businessId)
                        ?: throw NoSuchElementException("Employee with ID: $id not found")

                transaction.run {
                    employeeDao.deleteEmployee(id, businessId)
                    val networkEmployee = existingEntity.copy(isActive = 1).asNetworkModel()
                    val outboxPayload =
                        json.encodeToString(
                            OutboxPayload.CreateEmployeePayload(
                                // Reusing CreateEmployeePayload for simplicity, could be DeleteEmployeePayload
                                businessId = businessId,
                                employee = networkEmployee,
                            ),
                        )

                    outboxCommandDao.insertCommand(
                        // Queue outbox command for remote sync
                        OutboxCommandEntity(
                            type = OutboxEventType.Employee.DELETED,
                            payload = outboxPayload,
                        ),
                    )
                }
            }
        }

    /**
     * Synchronizes local outbox commands for employees with the remote data source.
     * This method processes pending employee-related commands (CREATED, UPDATED, DELETED),
     * sends them to the network, and posts signals to the inbox for other devices.
     *
     * @return `true` if all processed commands were successfully synced, `false` otherwise.
     */
    override suspend fun syncUp(): Boolean =
        withContext(ioDispatcher) {
            val startTime = Clock.System.now()
            analyticsHelper.logEvent(AnalyticsEvent("employee_sync_up_start"))
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
                            command.attemptCount,
                            Clock.System.now(),
                        )
                        val payload =
                            json.decodeFromString<OutboxPayload.CreateEmployeePayload>(
                                command.payload,
                            )
                        when (command.type) {
                            OutboxEventType.Employee.CREATED -> employeeNetwork.addEmployee(
                                payload.businessId,
                                payload.employee,
                            )

                            OutboxEventType.Employee.UPDATED -> {
                                employeeNetwork.updateEmployee(payload.businessId, payload.employee)
                            }

                            OutboxEventType.Employee.DELETED -> {
                                employeeNetwork.deleteEmployee(payload.businessId, payload.employee)
                            }

                            else -> logService.log("EmployeeRepository(syncUp): Unknown employee command type: ${command.type}")
                        }

                        val operationType = when (command.type) {
                            OutboxEventType.Employee.CREATED -> OperationType.CREATED
                            OutboxEventType.Employee.UPDATED -> OperationType.UPDATED
                            OutboxEventType.Employee.DELETED -> OperationType.DELETED
                            else -> OperationType.UPDATED
                        }
                        inboxNetworkDataSource.postSignal(
                            payload.businessId,
                            NetworkInboxSignal(
                                entityType = SyncableEntityType.EMPLOYEE.name,
                                entityId = payload.employee.id,
                                operationType = operationType,
                                lastUpdated = Clock.System.now().toEpochMilliseconds(),
                            ),
                        )
                        outboxCommandDao.updateCommandStatus(
                            command.id,
                            OutboxCommandStatus.COMPLETED,
                            command.attemptCount,
                            Clock.System.now(),
                        )
                    } catch (e: Exception) {
                        logService.log("EmployeeRepository(syncUp): Failed to process command ${command.id}. Error: ${e.message}")
                        outboxCommandDao.updateCommandStatus(
                            command.id,
                            OutboxCommandStatus.FAILED,
                            command.attemptCount,
                            Clock.System.now(),
                            e.message,
                        )
                    }
                }
            } catch (e: Exception) {
                logService.log("EmployeeRepository(syncUp): Failed with exception: ${e.message}")
                success = false
            }
            val duration = Clock.System.now() - startTime
            analyticsHelper.logEvent(
                AnalyticsEvent(
                    "employee_sync_up_complete",
                    listOf(Param("duration_ms", duration.inWholeMilliseconds.toString())),
                ),
            )
            success
        }
    /**
     * Synchronizes remote changes for employees with the local data source.
     * This method processes pending local signals for employees, fetches the latest data from the network,
     * and updates the local database.
     *
     * @return `true` if the sync down was successful, `false` otherwise.
     */

    /**
     * Synchronizes remote changes for employees with the local data source.
     * This method processes pending local signals for employees, fetches the latest data from the network,
     * and updates the local database.
     *
     * @return `true` if the sync down was successful, `false` otherwise.
     */
    override suspend fun syncDown(): Boolean = withContext(ioDispatcher) {
        val startTime = Clock.System.now()
        analyticsHelper.logEvent(AnalyticsEvent("employee_sync_down_start"))
        var success = true
        try {
            val pendingSignals = localSignalDao.getPendingSignals().first()
            val employeeSignals =
                pendingSignals.filter { it.entityType == SyncableEntityType.EMPLOYEE }

            employeeSignals.forEach { signal ->
                try {
                    val employeeId = signal.entityId
                    println(signal.operationType)
                    when (signal.operationType) {
                        OperationType.CREATED -> {
                            val networkEmployee =
                                employeeNetwork.findEmployeeByIdentifier(signal.businessId, employeeId)

                            if (networkEmployee != null) {
                                val employeeEntity = networkEmployee.asEntity(signal.businessId)
                                employeeDao.insertOrReplaceEmployee(employeeEntity)
                                logService.log("EmployeeRepository(syncDown): Synced employee ${employeeEntity.id} from network.")
                            } else {
                                logService.log("EmployeeRepository(syncDown): Employee $employeeId not found on network.")
                            }
                        }
                        OperationType.UPDATED -> {
                            val networkEmployee =
                                employeeNetwork.findEmployeeByIdentifier(signal.businessId, employeeId)
                            networkEmployee?.asEntity(signal.businessId)?.run {
                                employeeDao.updateEmployees(*arrayOf(this))
                                logService.log("EmployeeRepository(syncDown): Employee $employeeId not found on network.")
                            }
                        }
                        OperationType.DELETED -> {
                            employeeDao.deleteEmployee(employeeId, signal.businessId)
                            logService.log("EmployeeRepository(syncDown): Deleted employee $employeeId locally.")
                        }
                        else -> {
                            logService.log("EmployeeRepository(syncDown): Unknown operation type: ${signal.operationType}")
                        }
                    }
                    localSignalDao.updateSignalStatus(signal.id, LocalSignalStatus.PROCESSED)
                } catch (e: Exception) {
                    logService.log("EmployeeRepository(syncDown): Failed to process signal ${signal.id}. Error: $e")
                    localSignalDao.updateSignalStatus(signal.id, LocalSignalStatus.FAILED)
                    success = false
                }
            }
        } catch (e: Exception) {
            logService.log("EmployeeRepository(syncDown): Failed with exception: ${e.message}")
            success = false
        }
        val duration = Clock.System.now() - startTime
        analyticsHelper.logEvent(
            AnalyticsEvent(
                "employee_sync_down_complete",
                listOf(Param("duration_ms", duration.inWholeMilliseconds.toString())),
            ),
        )
        success
    }
}
