package com.casecode.pos.core.testing.repository.business

import com.casecode.pos.core.domain.repository.business.BranchRepository
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.testing.base.FakeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestBranchRepository @Inject constructor() : FakeRepository(), BranchRepository {

    // An in-memory store for our fake data
    private val branchesByBusiness = mutableMapOf<String, MutableList<Branch>>()
    private val _branchesFlow = MutableStateFlow<List<Branch>>(emptyList())

    override fun getBranches(businessId: String): Flow<List<Branch>> {
        return _branchesFlow.asStateFlow()
    }

    override suspend fun addBranch(businessId: String, branch: Branch): Result<String> {
        // Pattern: Check for failure first.
        getFailureResult<String>()?.let { return it }

        // If no failure, perform the success logic.
        val branchList = branchesByBusiness.getOrPut(businessId) { mutableListOf() }
        val newId = branch.id.ifEmpty { UUID.randomUUID().toString() } // Simulate ID generation
        val branchWithId = branch.copy(id = newId)
        branchList.add(branchWithId)
        _branchesFlow.value = branchList // Update the flow when a branch is added

        return Result.success(newId)
    }

    // --- Test Control Functions ---

    /**
     * Pre-populates the fake repository with a list of branches for a specific business.
     * This also updates the Flow.
     */
    fun setBranchesForBusiness(businessId: String, branches: List<Branch>) {
        branchesByBusiness[businessId] = branches.toMutableList()
        _branchesFlow.value = branches
    }

    /**
     * Resets the fake repository to a clean state between tests.
     */
    override fun clear() {
        branchesByBusiness.clear()
        _branchesFlow.value = emptyList()
        returnSuccess() // From FakeRepository base class
    }
}
