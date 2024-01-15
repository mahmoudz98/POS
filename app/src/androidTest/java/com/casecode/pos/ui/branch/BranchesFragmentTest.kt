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
import com.casecode.di.data.RepositoryModule
import com.casecode.domain.model.users.Branch
import com.casecode.pos.R
import com.casecode.pos.utils.launchFragmentInHiltContainer
import com.casecode.pos.viewmodel.BusinessViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
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
   @Rule
   @JvmField
   val instantTaskExecutorRule = InstantTaskExecutorRule()
   
   @get:Rule(order = 0)
   var hiltRule = HiltAndroidRule(this)
   
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
   fun givenUserHasAddedBranches_whenUserNavigatesToSubscriptionScreen_thenShouldShowBranchesInRecyclerView() {
   
      // Given
      givenUserHasAddedBranches()
      
      // When
      whenUserNavigatesToSubscriptionScreen()
      
      // Then
      thenUserShouldSeeBranchesRecyclerView()
   }
   
   @Test
   fun whenUserNavigatesToSubscriptionScreen_thenShouldShowAddBranchesButton() {
      
      // When
      whenUserNavigatesToSubscriptionScreen()
      
      // Then
      thenUserShouldSeeAddBranchesButton()
   }
   
   @Test
   fun givenUserHasAddedBranches_whenUserNavigatesToSubscriptionScreen_thenShouldShowSubscriptionButton() {
   
      // Given
      givenUserHasAddedBranches()
      
      // When
      whenUserNavigatesToSubscriptionScreen()
      
      // Then
      thenUserShouldSeeSubscriptionButton()
   }
   @Test
   fun givenNetworkIsUnavailable_whenUserNavigatesToSubscriptionScreen_thenReturnFalse(){
      // Given
      businessViewModel.setConnected(false)
      
      // When
      whenUserNavigatesToSubscriptionScreen()
      
      // Then
      assertThat( businessViewModel.isOnline.value, `is`(false))
      
   }
   
   @Test
   fun givenUserHasAddedBranches_whenUserNavigatesToSubscriptionScreen_thenShouldShowInfoButton() {
   
      // Given
      givenUserHasAddedBranches()
      
      // When
      whenUserNavigatesToSubscriptionScreen()
      
      // Then
      thenUserShouldSeeInfoButton()
   }
   
   
   @Test
   fun whenUserClicksAddBranchesButton_thenShouldShowAddBranchesDialog() {
   
      // When
      whenUserNavigatesToSubscriptionScreen()
      whenUserClicksAddBranchesButton()
      
      // Then
      thenUserShouldSeeAddBranchesDialog()
   }
   
   private fun givenUserHasAddedBranches()
   {
      val branches =
         arrayListOf(Branch(1, "Branch 1", "1234567890"),
            Branch(2, "Branch 2", "9876543210"))
      businessViewModel.branches.value = branches
      
      
   }
   
   private fun whenUserNavigatesToSubscriptionScreen()
   {
      // Navigate to the BranchesFragment
      onView(withId(R.id.btn_branches_subscription)).perform(click())
   }
   
   private fun thenUserShouldSeeBranchesRecyclerView()
   {
      
      onView(withId(R.id.rv_branches)).check(matches(isDisplayed()))
      
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