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
package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.repository.business.BranchRepository
import com.casecode.pos.core.domain.repository.business.SessionRepository
import com.casecode.pos.core.model.SessionStateResult
import com.casecode.pos.core.model.users.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * A use case that provides a stream of the current user's login state.
 */
class GetCurrentSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
) {
    operator fun invoke(): Flow<SessionStateResult> = sessionRepository.sessionInfo
}

/**
 * A use case responsible for starting a new owner session immediately after
 * the successful completion of the onboarding process.
 */
class StartOwnerSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val branchRepository: BranchRepository,
) {
    suspend operator fun invoke(user: User, businessId: String): Result<Unit> {
        return try {
            val branches = branchRepository.getBranches(businessId).first()
            val initialBranch = branches.firstOrNull()
                ?: return Result.failure(IllegalStateException("No branches found for new business."))

            sessionRepository.startOwnerSession(
                user = user,
                isCompleteSetupBusiness = true,
                branchId = initialBranch.id,
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
