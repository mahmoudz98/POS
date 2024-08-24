package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.R
import com.casecode.pos.core.domain.repository.AddBusiness
import com.casecode.pos.core.domain.utils.EmptyType
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.model.data.users.Business
import com.casecode.pos.core.model.data.users.StoreType
import com.casecode.pos.core.testing.repository.TestBusinessRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

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
    fun `invoke with valid data should add new business and return Resource of success true`() =
        runTest {
            // When add new business in use case
            val addBusiness = setBusinessUseCase(business)

            // Then check if result in  business repo and business use case is same.
            assertThat(addBusiness.last(), `is`(Resource.success(true)))
        }

    @Test
    fun `invoke with empty business branches should return Resource with BRANCHES_EMPTY error`() =
        runTest {
            // When add business and business branches are empty
            val emptyBranchBusiness = Business()

            // Then check if the result is empty business branches,
            val isAddBusiness: Flow<AddBusiness> = setBusinessUseCase(emptyBranchBusiness)
            assertThat(
                isAddBusiness.last(),
                `is`(Resource.empty(EmptyType.DATA, R.string.branches_empty)),
            )
        }

    @Test
    fun `invoke with empty phone should return Resource with PHONE_BUSINESS_EMPTY error`() =
        runTest {
            // When add business and phone is empty
            val businessWithEmptyPhone =
                Business(StoreType.Clothes, "mahmoud@gmail.com", "", false, listOf(Branch()))

            // Then check if the result is empty phone,
            val isAddBusiness: Flow<AddBusiness> = setBusinessUseCase(businessWithEmptyPhone)
            assertThat(
                isAddBusiness.last(),
                `is`(Resource.empty(EmptyType.DATA, R.string.phone_business_empty)),
            )
        }

    @Test
    fun `invoke with empty email should return Resource with EMAIL_BUSINESS_EMPTY error`() =
        runTest {
            // When add business and email is empty
            val businessWithEmptyEmail =
                Business(StoreType.Clothes, "", "1234", false, listOf(Branch()))

            // Then check if the result is empty email,
            val isAddBusiness: Flow<AddBusiness> = setBusinessUseCase(businessWithEmptyEmail)
            assertThat(
                isAddBusiness.last(),
                `is`(Resource.empty(EmptyType.DATA, R.string.email_business_empty)),
            )
        }
}