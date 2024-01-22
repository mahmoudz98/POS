package com.casecode.pos.ui.branch

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.casecode.domain.model.users.Branch
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.casecode.pos.utils.launchFragmentInHiltContainer
import com.casecode.pos.viewmodel.BusinessViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test for the  Branches screen.
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class BranchesFragmentTest
{
   
   
   @get:Rule(order = 0)
   var hiltRule = HiltAndroidRule(this)
   
   // Executes tasks in the Architecture Components in the same thread
   @get:Rule(order = 1)
   var instantTaskExecutorRule = InstantTaskExecutorRule()
   
   private lateinit var businessViewModel: BusinessViewModel
   
   @Before
   fun init()
   {
      hiltRule.inject()
      // Subject under test
      launchFragmentInHiltContainer<BranchesFragment> {
         this@BranchesFragmentTest.businessViewModel = businessViewModel
      }
   }
   
   @Test
   fun givenUserHasAddedBranches_whenUserClickButtonSubscription_thenShowMessageAddBusinessSuccess() {
      // Given
      givenUserHasAddedBranches()
      businessViewModel.setConnected(true)
      
      // When
      whenUserClickBranchSubscription()
      // Then
      assertThat(businessViewModel.userMessage.value?.peekContent(), `is`(R.string.add_business_success))
   }
   
   @Test
   fun shouldShowMessageBranchesAreEmpty_whenClickSubscription() {
      
      // When
      whenUserClickBranchSubscription()
      
      // Then
      thenUserShouldSeeAddBranchesButton()
   }
   
   @Test
   fun givenUserHasAddedBranches_whenUserNavigatesToSubscriptionScreen_thenShouldShowSubscriptionButton() {
   
      // Given
      givenUserHasAddedBranches()
      
      // When
      whenUserClickBranchSubscription()
      
      // Then
      thenUserShouldSeeSubscriptionButton()
   }
   @Test
   fun givenNetworkIsUnavailable_whenUserClickBranchSubscription_thenReturnFalse(){
      // Given
      businessViewModel.setConnected(false)
      
      // When
      whenUserClickBranchSubscription()
      
      // Then
      assertThat( businessViewModel.isOnline.value, `is`(false))
      
   }
   
   @Test
   fun givenUserHasAddedBranches_whenUserClickBranchSubscription_thenShouldShowInfoButton() {
   
      // Given
      givenUserHasAddedBranches()
      
      // When
      whenUserClickBranchSubscription()
      
      // Then
      thenUserShouldSeeInfoButton()
   }
   
   
   @Test
   fun whenUserClicksAddBranchesButton_thenShouldShowAddBranchesDialog() {
   
      // When
      whenUserClickBranchSubscription()
      whenUserClicksAddBranchesButton()
      
      // Then
      thenUserShouldSeeAddBranchesDialog()
   }
   
   private fun givenUserHasAddedBranches()
   {
      businessViewModel.setEmail("test@gmail.com")
      businessViewModel.setStoreType("Clothes")
      businessViewModel.setPhoneBusiness("123456777")
      val branches =
         arrayListOf(Branch(1, "Branch 1", "1234567890"),
            Branch(2, "Branch 2", "9876543210"))
      businessViewModel.branches.value = branches
      // Error when not find business storetype and email
      
      
   }
   
   private fun whenUserClickBranchSubscription()
   {
      // Navigate to the BranchesFragment
      onView(withId(R.id.btn_branches_subscription)).perform(click())
   }
   
   private fun thenUserShouldSeeAddBranchesButton()
   {
      onView(withId(R.id.btn_branches_add)).check(matches(isDisplayed()))
   }
   
   private fun thenUserShouldSeeSubscriptionButton()
   {
      onView(withId(R.id.btn_branches_subscription)).check(matches(isDisplayed()))
   }
   
   private fun thenUserShouldSeeInfoButton()
   {
      onView(withId(R.id.btn_branches_info)).check(matches(isDisplayed()))
   }
   
   private fun whenUserClicksAddBranchesButton()
   {
      onView(withId(R.id.btn_branches_add)).perform(click())
   }
   
   private fun thenUserShouldSeeAddBranchesDialog()
   {
      // Check that the AddBranchesDialogFragment is displayed
      onView(withId(R.id.et_add_branches_name)).check(matches(isDisplayed()))
   }
}