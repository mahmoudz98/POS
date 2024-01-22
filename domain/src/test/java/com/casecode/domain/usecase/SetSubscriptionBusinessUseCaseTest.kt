package com.casecode.domain.usecase

import com.casecode.domain.model.users.SubscriptionBusiness
import com.casecode.domain.utils.EmptyType
import com.casecode.domain.utils.Resource
import com.casecode.pos.domain.R
import com.casecode.testing.repository.TestSubscriptionsBusinessRepository
import com.casecode.testing.util.CoroutinesTestRule
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

class SetSubscriptionBusinessUseCaseTest
{
   @get:Rule
   val coroutineTestRule = CoroutinesTestRule()
   // Given uid and subscription
   private val uid = "test"
   private val subscription: SubscriptionBusiness =
      SubscriptionBusiness(type = "Pro", cost = 20L, duration = 60, listOf("admin"))
   
   // subject under test
   private val testSubscriptionsBusinessRepository: TestSubscriptionsBusinessRepository =
      TestSubscriptionsBusinessRepository()
   private val setSubscriptionBusinessUseCase: SetSubscriptionBusinessUseCase =
      SetSubscriptionBusinessUseCase(testSubscriptionsBusinessRepository)
   
   
   @Test
   fun setSubscriptionBusinessUseCase_shouldAddNewSubscriptionBusiness_returnTrue() = runTest {
      
      
      // When
      val resultIsAddSubscriptionBusiness = setSubscriptionBusinessUseCase(subscription, uid)
      
      // Then
      val isAddSubscriptionBusiness = Resource.success(true)
      assertThat(isAddSubscriptionBusiness, `is`(resultIsAddSubscriptionBusiness))
   }
   
   @Test
   fun setSubscriptionBusinessUseCase_emptyUid_returnEmptyUid() = runTest {
      // When uid is empty
      val resultEmptyUidSubscriptionBusiness =
         setSubscriptionBusinessUseCase(subscription, "")
      
      // Then - return Resource of empty uid.
      assertThat(resultEmptyUidSubscriptionBusiness,
         `is`(Resource.empty(EmptyType.DATA, R.string.uid_empty)))
      
      
   }
   
   @Test
   fun setSubscriptionBusinessUseCase_emptyBusiness_returnEmptyTypeOfSubscription() = runTest {
      // When subscription business fields is empty
      val resultEmptySubscriptionBusiness =
         setSubscriptionBusinessUseCase(SubscriptionBusiness(), uid)
      
      // Then - return Resource of empty data
      assertThat(resultEmptySubscriptionBusiness,
         `is`(Resource.empty(EmptyType.DATA, R.string.add_subscription_business_empty)))
   }
   
}