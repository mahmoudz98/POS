package com.casecode.pos.core.data.repository.business

import com.casecode.pos.core.datastore.PosPreferencesDataSource
import com.casecode.pos.core.domain.repository.business.SessionRepository
import com.casecode.pos.core.model.data.LoginStateResult
import com.casecode.pos.core.model.data.business.Employee
import com.casecode.pos.core.model.data.users.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val prefs: PosPreferencesDataSource,
) : SessionRepository {
    override val loginState: Flow<LoginStateResult> = prefs.loginData

    override suspend fun startOwnerSession(user: User, isCompleteSetupBusiness:Boolean, businessId: String,
                                           branchId: String,) {
        prefs.saveNewLoginSession(
            isOwner = true,
            isCompleteSetupBusiness = isCompleteSetupBusiness,
            userId = user.uid,
            userName = user.name ?: "Owner",
            businessId = businessId,
            activeBranchId = branchId,
            role = "OWNER"
        )
    }

    override suspend fun startEmployeeSession(businessId: String, employee: Employee, activeBranchId: String) {
        prefs.saveNewLoginSession(
            isOwner = false,
            userId = employee.employeeId,
            userName = employee.name,
            businessId = businessId,
            activeBranchId = activeBranchId,
            role = employee.role
        )
    }

    override suspend fun clearSession() {
        prefs.clearLoginSession()
    }
}
