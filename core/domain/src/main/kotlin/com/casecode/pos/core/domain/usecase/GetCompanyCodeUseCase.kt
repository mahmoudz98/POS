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

import com.casecode.pos.core.domain.repository.business.BusinessRepository
import com.casecode.pos.core.model.SessionStateResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetCompanyCodeUseCase @Inject constructor(
    private val getCurrentSessionUseCase: GetCurrentSessionUseCase,
    private val businessRepository: BusinessRepository,
) {
    operator fun invoke(): Flow<String?> {
        return getCurrentSessionUseCase().flatMapLatest { session ->
            val businessId = when (session) {
                is SessionStateResult.OwnerLoggedIn -> session.businessId
                is SessionStateResult.EmployeeLoggedIn -> session.businessId
                else -> null
            }

            if (businessId != null) {
                flowOf(businessRepository.findBusinessByOwner(businessId).getOrNull()?.companyCode)
            } else {
                flowOf(null)
            }
        }
    }
}
