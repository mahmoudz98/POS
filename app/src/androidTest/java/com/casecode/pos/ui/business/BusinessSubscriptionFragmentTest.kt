package com.casecode.pos.ui.business

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.casecode.pos.R
import com.casecode.pos.utils.DataBindingIdlingRes
import com.casecode.testing.util.EspressoIdlingResource
import com.casecode.pos.utils.launchFragmentInHiltContainer
import com.casecode.pos.utils.monitorFragment
import com.casecode.pos.viewmodel.BusinessViewModel
import com.casecode.testing.repository.TestSubscriptionsRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
@HiltAndroidTest
class BusinessSubscriptionFragmentTest
{
   @Rule
   @JvmField
   val instantTaskExecutorRule = InstantTaskExecutorRule()
   
   @get:Rule(order = 0)
   var hiltRule = HiltAndroidRule(this)
   
   private lateinit var businessViewModel: BusinessViewModel
   
   @Inject
   lateinit var testSubscriptionsRepository: TestSubscriptionsRepository
   
   // An idling resource that waits for Data Binding to have no pending bindings.
   private val dataBindingIdlingResource = DataBindingIdlingRes()
   
   @Before
   fun init()
   {
      hiltRule.inject()
      // Subject under test
      launchFragmentInHiltContainer<BusinessSubscriptionFragment> {
         this@BusinessSubscriptionFragmentTest.businessViewModel = this.businessViewModel
         dataBindingIdlingResource.monitorFragment(this)
         
      }
      
   }
   
   @After
   fun cleanUp() = runTest {
   
   }
   
   @Before
   fun registerIdlingResource()
   {
      IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
      IdlingRegistry.getInstance().register(dataBindingIdlingResource)
   }
   
   @After
   fun unregisterIdlingResource()
   {
      IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
      IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
      
   }
   
   @Test
   fun shouldAddSubscription_whenSubscriptionSelected() = runTest {
      businessViewModel.setNetworkMonitor()
      businessViewModel.setConnected(true)
      advanceUntilIdle()
      testSubscriptionsRepository.sendSubscriptions(testSubscriptionsRepository.subscriptionsFake())
      
      // Given
      /*  Espresso.onView(ViewMatchers.withId(R.id.rv_business_subscription)).perform(
          RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
             ViewActions.click())) */
      // When
      Espresso.onView(ViewMatchers.withId(R.id.btn_business_subscription_user))
         .perform(ViewActions.click())
      
      // Then
      MatcherAssert.assertThat(businessViewModel.userMessage.value,
         `is`(R.string.add_subscription_success))
      Espresso.onView(ViewMatchers.withId(R.id.rv_employees))
         .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
   }
}
