package com.casecode.pos.ui.business

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.casecode.domain.repository.AddSubscriptionBusiness
import com.casecode.pos.R
import com.casecode.pos.utils.launchFragmentInHiltContainer
import com.casecode.pos.viewmodel.StepperBusinessViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test for the Subscription screen.
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class BusinessSubscriptionFragmentTest {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var businessViewModel: StepperBusinessViewModel

    @Before
    fun init() {
        hiltRule.inject()

        launchFragmentInHiltContainer<BusinessSubscriptionFragment> {
            this@BusinessSubscriptionFragmentTest.businessViewModel = businessViewModel
        }
    }

    @Test
    fun shouldShowImageError_whenNetworkUnAvailable() {
        businessViewModel.setConnected(false)

        onView(withId(R.id.imv_business_subscription_empty)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldShowSubscriptionsList_whenNetworkAvailable() {
        businessViewModel.setConnected(true)
        businessViewModel.getSubscriptionsBusiness()

        onView(withId(R.id.rv_business_subscription)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldAddSubscription_whenSubscriptionSelected() {
        // Given
        businessViewModel.setConnected(true)
        businessViewModel.getSubscriptionsBusiness()

        // When click item in list and click move to next step: Employees
        onView(withId(R.id.rv_business_subscription)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click(),
            ),
        )

        onView(withId(R.id.btn_business_subscription_employee)).perform(click())

        // Then
        assertThat(
            businessViewModel.userMessage.value?.peekContent(),
            `is`(R.string.add_subscription_success),
        )
        assertThat(
            businessViewModel.isAddSubscriptionBusiness.value,
            `is`(AddSubscriptionBusiness.success(true)),
        )
    }

    @Test
    fun shouldReturnNetworkUnavailable_whenAddBusinessSubscription() {
        // Given
        businessViewModel.setConnected(true)
        businessViewModel.getSubscriptionsBusiness()

        // When click item in list and click move to next step: Employees
        onView(withId(R.id.rv_business_subscription)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click(),
            ),
        )

        businessViewModel.setConnected(false)
        onView(withId(R.id.btn_business_subscription_employee)).perform(click())

        // Then
        assertThat(
            businessViewModel.userMessage.value?.peekContent(),
            `is`(R.string.network_error),
        )
    }
}