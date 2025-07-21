package com.casecode.pos.core.testing.repository.business

import com.casecode.pos.core.domain.repository.business.BusinessRepository
import com.casecode.pos.core.model.business.BillingEvent
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.Business
import com.casecode.pos.core.model.business.Subscription
import com.casecode.pos.core.model.business.TaxRate
import com.casecode.pos.core.testing.base.FakeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestBusinessRepository @Inject constructor() : FakeRepository(), BusinessRepository {

    private val businesses = mutableMapOf<String, Business>()
    private val _businessFlow = MutableStateFlow<Business?>(null)

    override suspend fun createInitialBusiness(
        business: Business,
        initialBranches: List<Branch>,
        initialTaxes: List<TaxRate>,
        initialSubscription: Subscription,
        initialBillingEvent: BillingEvent,
    ): Result<String> {
        getFailureResult<String>()?.let { return it }

        if (businesses.values.any { it.ownerUid == business.ownerUid }) {
            return Result.failure(Exception("A business already exists for this owner."))
        }

        val newId = UUID.randomUUID().toString()
        val newBusiness = business.copy(id = newId)
        businesses[newId] = newBusiness
        _businessFlow.value = newBusiness // Update the flow when a business is created
        return Result.success(newId)
    }

    override fun findBusinessByOwner(ownerUid: String): Flow<Business?> {
        return _businessFlow.asStateFlow()
    }


    override suspend fun companyCodeExists(companyCode: String): Result<Boolean> {
        getFailureResult<Boolean>()?.let { return it }
        val exists = businesses.values.any { it.companyCode == companyCode }
        return Result.success(exists)
    }

    // --- Test Control Functions ---

    /**
     * Sets the business that will be emitted by the Flow.
     */
    fun setBusinessFlow(business: Business?) {
        _businessFlow.value = business
    }

    /**
     * Adds a business to the internal map, useful for simulating existing businesses.
     */
    fun addBusiness(business: Business) {
        businesses[business.id] = business
    }

    override   fun clear() {
        businesses.clear()
        _businessFlow.value = null
        returnSuccess()
    }
}
