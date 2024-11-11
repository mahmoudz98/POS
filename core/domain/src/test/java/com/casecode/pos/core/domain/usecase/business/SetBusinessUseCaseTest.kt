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
package com.casecode.pos.core.domain.usecase.business

import com.casecode.pos.core.domain.R
import com.casecode.pos.core.domain.repository.AddBusiness
import com.casecode.pos.core.domain.usecase.SetBusinessUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.model.data.users.Business
import com.casecode.pos.core.model.data.users.StoreType
import com.casecode.pos.core.testing.repository.TestBusinessRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class SetBusinessUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Given business and uid
    private val business =
        Business(
            StoreType.Clothes,
            "mahmoud@gmail.com",
            "1234",
            false,
            listOf(Branch(1, "22", "2323")),
        )

    private var testBusinessRepository: TestBusinessRepository = TestBusinessRepository()
    private val setBusinessUseCase: SetBusinessUseCase = SetBusinessUseCase(testBusinessRepository)

    @Test
    fun `when valid data should add new business and return Resource of success true`() =
        runTest {
            // When add new business in use case
            val addBusiness = setBusinessUseCase(business)

            // Then check if result in  business repo and business use case is same.
            assertEquals(addBusiness.last(), (Resource.success(true)))
        }

    @Test
    fun `when empty business branches should return Resource with BRANCHES_EMPTY error`() =
        runTest {
            // When add business and business branches are empty
            val emptyBranchBusiness = Business()

            // Then check if the result is empty business branches,
            val isAddBusiness: Flow<AddBusiness> = setBusinessUseCase(emptyBranchBusiness)
            assertEquals(
                isAddBusiness.last(),
                (Resource.empty(R.string.core_domain_branches_empty)),
            )
        }

    @Test
    fun `when empty phone should return Resource with PHONE_BUSINESS_EMPTY error`() =
        runTest {
            // When add business and phone is empty
            val businessWithEmptyPhone =
                Business(StoreType.Clothes, "mahmoud@gmail.com", "", false, listOf(Branch()))

            // Then check if the result is empty phone,
            val isAddBusiness: Flow<AddBusiness> = setBusinessUseCase(businessWithEmptyPhone)
            assertEquals(
                isAddBusiness.last(),
                (Resource.empty(R.string.core_domain_phone_business_empty)),
            )
        }

    @Test
    fun `when empty email should return Resource with EMAIL_BUSINESS_EMPTY error`() =
        runTest {
            // When add business and email is empty
            val businessWithEmptyEmail =
                Business(StoreType.Clothes, "", "1234", false, listOf(Branch()))

            // Then check if the result is empty email,
            val isAddBusiness: Flow<AddBusiness> = setBusinessUseCase(businessWithEmptyEmail)
            assertEquals(
                isAddBusiness.last(),
                (Resource.empty(R.string.core_domain_email_business_empty)),
            )
        }
}