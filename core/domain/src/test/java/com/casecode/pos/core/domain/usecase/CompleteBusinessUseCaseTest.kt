package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.utils.EmptyType
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.data.R
import com.casecode.pos.core.testing.repository.TestBusinessRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

class CompleteBusinessUseCaseTest{
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private  val testBusinessRepository = TestBusinessRepository()
    private val completeBusinessUseCase = CompleteBusinessUseCase(testBusinessRepository)

    @Test
    fun `invoke with valid UID return resource with success`() = runTest {
        val isCompleteBusiness = completeBusinessUseCase()

        assertThat(isCompleteBusiness, `is`(Resource.success(true)))
    }
    @Test
    fun `invoke with empty UID return resource with UID empty`() = runTest {

        // When set completed business and uid is empty
        val isCompleteBusiness = completeBusinessUseCase( )

        // Then check if result is empty uid ,
        assertThat(isCompleteBusiness, `is`(Resource.empty(EmptyType.DATA, R.string.core_data_uid_empty)))
    }
}