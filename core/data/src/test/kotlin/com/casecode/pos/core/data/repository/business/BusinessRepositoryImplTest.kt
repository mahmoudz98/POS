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

import com.casecode.pos.core.data.model.asNetworkModel
import com.casecode.pos.core.database.model.LocalSignalEntity
import com.casecode.pos.core.database.model.LocalSignalStatus
import com.casecode.pos.core.database.model.OutboxCommandEntity
import com.casecode.pos.core.database.model.OutboxEventType
import com.casecode.pos.core.firebase.TimestampSerializer
import com.casecode.pos.core.model.SyncableEntityType
import com.casecode.pos.core.model.business.BillingEvent
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.Business
import com.casecode.pos.core.model.business.BusinessStatus
import com.casecode.pos.core.model.business.PlanLimits
import com.casecode.pos.core.model.business.Subscription
import com.casecode.pos.core.model.business.SubscriptionPlan
import com.casecode.pos.core.model.business.TaxRate
import com.casecode.pos.core.model.business.Vertical
import com.casecode.pos.core.testing.dao.TestBranchDao
import com.casecode.pos.core.testing.dao.TestBusinessDao
import com.casecode.pos.core.testing.dao.TestLocalSignalDao
import com.casecode.pos.core.testing.dao.TestOutboxCommandDao
import com.casecode.pos.core.testing.dao.TestSubscriptionDao
import com.casecode.pos.core.testing.dao.TestTaxRateDao
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
import kotlin.test.assertTrue
import kotlin.time.Clock

class BusinessRepositoryImplTest {

    @get:Rule
    val coroutinesRule = CoroutinesTestRule()

    private lateinit var businessDao: TestBusinessDao
    private lateinit var branchDao: TestBranchDao
    private lateinit var subscriptionDao: TestSubscriptionDao
    private lateinit var taxRateDao: TestTaxRateDao
    private lateinit var outboxCommandDao: TestOutboxCommandDao
    private lateinit var localSignalDao: TestLocalSignalDao
    private lateinit var network: TestBusinessNetworkDataSource
    private lateinit var inboxNetworkDataSource: TestInboxNetworkDataSource
    private lateinit var testDatabaseTransactionRunner: TestDatabaseTransactionRunner
    private lateinit var json: Json
    private lateinit var repository: BusinessRepositoryImpl

    @Before
    fun setup() {
        businessDao = TestBusinessDao()
        branchDao = TestBranchDao()
        subscriptionDao = TestSubscriptionDao()
        taxRateDao = TestTaxRateDao()
        outboxCommandDao = TestOutboxCommandDao()
        localSignalDao = TestLocalSignalDao()
        network = TestBusinessNetworkDataSource()
        inboxNetworkDataSource = TestInboxNetworkDataSource()
        testDatabaseTransactionRunner = TestDatabaseTransactionRunner()
        json = Json {
            serializersModule = SerializersModule {
                contextual(
                    Timestamp::class,
                    TimestampSerializer,
                )
            }
        }
        repository = BusinessRepositoryImpl(
            network = network,
            businessDao = businessDao,
            branchDao = branchDao,
            subscriptionDao = subscriptionDao,
            taxRateDao = taxRateDao,
            outboxCommandDao = outboxCommandDao,
            localSignalDao = localSignalDao,
            transaction = testDatabaseTransactionRunner,
            inboxNetworkDataSource = inboxNetworkDataSource,
            json = json,
            logService = TestLogService(),
            ioDispatcher = UnconfinedTestDispatcher(),
        )
    }

    @Test
    fun `createInitialBusiness saves all entities and queues commands atomically`() = runTest {
        // When
        val result = repository.createInitialBusiness(
            business,
            listOf(branch),
            listOf(tax),
            subscription,
            billingEvent,
        )

        // Then
        assertTrue(result.isSuccess)

        // Verify DAO insertions
        assertEquals(business.id, businessDao.insertedBusiness?.businessId)
        assertEquals(tax.id, taxRateDao.insertedTaxRates.first().id)
        assertEquals(subscription.planId, subscriptionDao.insertedSubscription?.planId)

        // Verify command queuing
        assertEquals(1, outboxCommandDao.insertedCommands.size)
        assertEquals(
            OutboxEventType.BUSINESS_CREATED.ordinal,
            outboxCommandDao.insertedCommands.first().type,
        )
    }

    @Test
    fun createInitialBusiness_whenTransactionFails_returnsFailure() = runTest {
        // Configure the transaction runner to fail
        testDatabaseTransactionRunner.setError()
        // When
        val result = repository.createInitialBusiness(
            business,
            listOf(branch),
            listOf(tax),
            subscription,
            billingEvent,
        )

        // Then
        assertTrue(result.isFailure)

        // Verify that no data was saved due to transaction failure
        assertEquals(null, businessDao.insertedBusiness)
        assertTrue(taxRateDao.insertedTaxRates.isEmpty())
        assertEquals(null, subscriptionDao.insertedSubscription)
        assertTrue(outboxCommandDao.insertedCommands.isEmpty())
    }

    @Test
    fun syncUp_withPendingBusinessCommand_callsNetworkAndPostsSignal() = runTest {
        // Given
        val business = Business(id = "biz1", name = "Test Cafe", ownerUid = "owner1", vertical = Vertical.CAFE, currencyCode = "USD", status = BusinessStatus.ACTIVE, email = "", phone = "", updatedAt = Clock.System.now(), createdAt = Clock.System.now())
        val branch = Branch(id = "branch1", name = "Main St", phone = "")
        val tax = TaxRate(id = "tax1", name = "VAT", rate = 10.0f)
        val subscriptionPlan = SubscriptionPlan("plan1", "Basic", "أساسي", emptyList(), true, emptyList(), emptyList(), PlanLimits(1, 1, 1, 1))
        val subscription = Subscription.fromPlan(subscriptionPlan)
        val billingEvent = BillingEvent.forPlanActivation(subscriptionPlan, "USD", "fake_id")
        val payload = json.encodeToString(
            com.casecode.pos.core.data.model.OutboxPayload.CreateBusinessPayload(
                business = business.asNetworkModel(),
                initialBranches = listOf(branch).map { it.asNetworkModel() },
                initialTaxes = listOf(tax).map { it.asNetworkModel() },
                initialSubscription = subscription.asNetworkModel(),
                initialBillingEvent = billingEvent.asNetworkModel(),
            ),
        )
        val command = OutboxCommandEntity(
            id = 1,
            type = OutboxEventType.BUSINESS_CREATED.ordinal,
            payload = payload,
        )
        outboxCommandDao.insertCommand(command)

        // When
        val result = repository.syncUp()

        // Then
        assertTrue(result)
        assertEquals(1, inboxNetworkDataSource.postedSignals.size)
    }

    @Test
    fun syncDown_withPendingLocalSignal_fetchesFromNetworkAndUpdatesLocalDb() = runTest {
        // Given
        val ownerId = "owner1"
        val signal = LocalSignalEntity(id = "22121", entityType = SyncableEntityType.BUSINESS, entityId = ownerId)
        localSignalDao.insertSignal(signal)

        network.createInitialBusiness(
            business.asNetworkModel(),
            listOf(branch.asNetworkModel()),
            listOf(tax.asNetworkModel()),
            subscription.asNetworkModel(),
            billingEvent.asNetworkModel(),
        )

        // When
        val result = repository.syncDown()

        // Then
        assertTrue(result)
        // Verify the local signal was processed
        assertEquals(LocalSignalStatus.PROCESSED, localSignalDao.getPendingSignals().first().first().status)
    }

    val business = Business(
        id = "biz1",
        name = "Test Cafe",
        ownerUid = "owner1",
        vertical = Vertical.CAFE,
        currencyCode = "USD",
        status = BusinessStatus.ACTIVE,
        email = "test@gg.com",
        phone = "1234454554",
        updatedAt = Clock.System.now(),
        createdAt = Clock.System.now(),
    )
    val branch = Branch(id = "branch1", name = "Main St", phone = "")
    val tax = TaxRate(id = "tax1", name = "VAT", rate = 10.0f)
    val subscriptionPlan = SubscriptionPlan(
        "plan1",
        "Basic",
        "أساسي",
        emptyList(),
        true,
        emptyList(),
        emptyList(),
        PlanLimits(1, 1, 1, 1),
    )
    val subscription = Subscription.fromPlan(subscriptionPlan)
    val billingEvent = BillingEvent.forPlanActivation(subscriptionPlan, "USD", "fake_id")
}
