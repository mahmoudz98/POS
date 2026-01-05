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

import com.casecode.pos.core.analytics.NoOpAnalyticsHelper
import com.casecode.pos.core.data.model.asNetworkModel
import com.casecode.pos.core.database.model.LocalSignalEntity
import com.casecode.pos.core.database.model.LocalSignalStatus
import com.casecode.pos.core.database.model.OutboxCommandEntity
import com.casecode.pos.core.database.model.OutboxEventType
import com.casecode.pos.core.firebase.TimestampSerializer
import com.casecode.pos.core.firebase.model.NetworkBranch
import com.casecode.pos.core.firebase.model.OperationType
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.data.SyncableEntityType
import com.casecode.pos.core.testing.dao.TestBranchDao
import com.casecode.pos.core.testing.dao.TestLocalSignalDao
import com.casecode.pos.core.testing.dao.TestOutboxCommandDao
import com.casecode.pos.core.testing.datasource.TestBusinessNetworkDataSource
import com.casecode.pos.core.testing.datasource.TestInboxNetworkDataSource
import com.casecode.pos.core.testing.services.TestLogService
import com.casecode.pos.core.testing.util.CoroutinesTestRule
import com.casecode.pos.core.testing.util.TestDatabaseTransactionRunner
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BranchRepositoryImplTest {

    @get:Rule
    val coroutinesRule = CoroutinesTestRule()

    private lateinit var network: TestBusinessNetworkDataSource
    private lateinit var branchDao: TestBranchDao
    private lateinit var outboxCommandDao: TestOutboxCommandDao
    private lateinit var localSignalDao: TestLocalSignalDao
    private lateinit var inboxNetworkDataSource: TestInboxNetworkDataSource
    private lateinit var repository: BranchRepositoryImpl
    private lateinit var json: Json

    private val testBusinessId = "biz-123"
    private val testBranch =
        Branch(businessId = testBusinessId, name = "Main Branch", phone = "123454555")

    @Before
    fun setup() {
        network = TestBusinessNetworkDataSource()
        branchDao = TestBranchDao()
        outboxCommandDao = TestOutboxCommandDao()
        localSignalDao = TestLocalSignalDao()
        inboxNetworkDataSource = TestInboxNetworkDataSource()
        json = Json {
            serializersModule = SerializersModule {
                contextual(
                    Timestamp::class,
                    TimestampSerializer,
                )
            }
        }
        repository = BranchRepositoryImpl(
            network = network,
            transaction = TestDatabaseTransactionRunner(),
            branchDao = branchDao,
            outboxCommandDao = outboxCommandDao,
            localSignalDao = localSignalDao,
            inboxNetworkDataSource = inboxNetworkDataSource,
            json = json,
            analyticsHelper = NoOpAnalyticsHelper(),
            logService = TestLogService(),
            ioDispatcher = UnconfinedTestDispatcher(),
        )
    }

    @Test
    fun getBranches_whenLocalIsEmpty_fetchesAndCachesFromNetwork() = runTest {
        // Given
        val networkBranch = testBranch.asNetworkModel()
        network.setBranches(testBusinessId, listOf(networkBranch))

        // When
        val branches = repository.getBranches(testBusinessId).first()

        // Then
        assertEquals(1, branches.size)
        assertEquals(testBranch.name, branches.first().name)
        assertTrue(
            branchDao.getBranches(testBusinessId).first().any { it.branchId == testBranch.id },
        )
    }

    @Test
    fun addBranch_withValidInput_savesToDaoAndQueuesOutbox() = runTest {
        // When
        val result = repository.addBranch(testBusinessId, testBranch)
        val id = result.getOrNull()
        // Then
        assertTrue(branchDao.getBranches(testBusinessId).first().any { it.branchId == id })
        val command = outboxCommandDao.insertedCommands.first()
        assertEquals(OutboxEventType.Branch.CREATED, command.type)
    }

    @Test
    fun syncUp_withPendingCreateCommand_addsBranchToNetwork() = runTest {
        // Given
        val payload = json.encodeToString(
            com.casecode.pos.core.data.model.OutboxPayload.AddBranchPayload(
                testBusinessId,
                testBranch.asNetworkModel(),
            ),
        )
        outboxCommandDao.insertCommand(
            OutboxCommandEntity(type = OutboxEventType.Branch.CREATED, payload = payload),
        )

        // When
        repository.syncUp()

        // Then
        assertTrue(network.getBranches(testBusinessId).any { it.id == testBranch.id })
        assertTrue(inboxNetworkDataSource.postedSignals.any { it.entityId == testBranch.id })
    }

    @Test
    fun syncDown_withUpdateSignal_fetchesAndUpdatesLocalBranch() = runTest {
        // Given
        val networkBranch =
            NetworkBranch(id = "br-001", name = "Updated Branch", phone = "123 Street")
        network.setBranches(testBusinessId, listOf(networkBranch))
        localSignalDao.insertSignal(
            LocalSignalEntity(
                id = "sig-1",
                businessId = testBusinessId,
                entityType = SyncableEntityType.BRANCH,
                entityId = "br-001",
                operationType = OperationType.UPDATED,
                status = LocalSignalStatus.PENDING,
            ),
        )

        // When
        repository.syncDown()

        // Then
        val localBranch =
            branchDao.getBranches(testBusinessId).first().find { it.branchId == "br-001" }
        assertNotNull(localBranch)
        assertEquals("Updated Branch", localBranch.name)
    }
}
