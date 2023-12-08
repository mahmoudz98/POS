package com.casecode.pos.ui.branch

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.casecode.pos.R
import com.casecode.pos.utils.launchFragmentInHiltContainer
import com.casecode.pos.utils.withHint
import com.casecode.pos.viewmodel.BusinessViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test for the  add Branch screen.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
@HiltAndroidTest
class AddBranchesDialogFragmentTest
{
   @JvmField
   @Rule
   val instantTaskExecutorRule = InstantTaskExecutorRule()
   
   @get:Rule(order = 0)
   var hiltRule = HiltAndroidRule(this)
   
   
   private lateinit var businessViewModel: BusinessViewModel
   private lateinit var context: Context
   
   @Before
   fun init()
   {
      hiltRule.inject()
      // Subject under test
      launchFragmentInHiltContainer<BranchesFragment> {
         this@AddBranchesDialogFragmentTest.businessViewModel = businessViewModel
         this@AddBranchesDialogFragmentTest.context = context !!
      }
      
      
   }
   
   
   @Test
   fun shouldDisplayAddBranchesDialogWhenAddBranchesButtonClicked()
   {
      givenUserClicksAddBranchesButton()
      thenUserShouldSeeAddBranchesDialog()
   }
   
   @Test
   fun shouldShowErrorWhenBranchNameIsEmpty()
   {
      givenUserClicksAddBranchesButton()
      whenUserEntersEmptyBranchName()
      whenUserClicksAddBranchOrUpdateButton()
      thenUserShouldSeeErrorForBranchName()
   }
   
   @Test
   fun shouldShowErrorWhenBranchPhoneIsEmpty()
   {
      givenUserClicksAddBranchesButton()
      whenUserEntersEmptyBranchPhone()
      whenUserClicksAddBranchOrUpdateButton()
      thenUserShouldSeeErrorForBranchPhone()
   }
   
   @Test
   fun shouldShowErrorWhenBranchPhoneIsInvalid()
   {
      givenUserClicksAddBranchesButton()
      whenUserEntersInvalidBranchPhone()
      whenUserClicksAddBranchOrUpdateButton()
      thenUserShouldSeeErrorForBranchPhone()
   }
   
   
   @Test
   fun shouldSuccessfullyAddBranchWithValidData()
   {
      givenUserClicksAddBranchesButton()
      givenUserEntersValidBranchData()
      whenUserClicksAddBranchOrUpdateButton()
      thenUserShouldSeeAddBranchConfirmationMessage()
   }
   
   @Test
   fun shouldSuccessfullyUpdateExistingBranchWithoutChangingName()
   {
      // Given add branch in list of branch
      businessViewModel.setBranchName("Grocery Store")
      businessViewModel.setBranchPhone("1234567")
      businessViewModel.addBranch()
      
      // click on first item in recycle view
      onView(withId(R.id.rv_branches)).perform(
         RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
      
      onView(withId(R.id.et_add_branches_phone)).perform(replaceText("101886724"))
      
      
      // When
      whenUserClicksAddBranchOrUpdateButton()
      
      // Then
      thenUserShouldSeeIsUpdateBranchReturnTrue()
   }
   
   @Test
   fun shouldSuccessfullyUpdateExistingBranchWithoutChangingPhone()
   {
      // Given add branch in list of branch
      businessViewModel.setBranchName("Grocery Store")
      businessViewModel.setBranchPhone("1234567")
      businessViewModel.addBranch()
      onView(withId(R.id.rv_branches)).perform(
         RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
      givenUserEntersOnlyBranchName()
      
      whenUserClicksAddBranchOrUpdateButton()
      
      thenUserShouldSeeIsUpdateBranchReturnTrue()
   }
   
   private fun givenUserClicksAddBranchesButton()
   {
      onView(withId(R.id.btn_branches_add)).perform(click())
   }
   
   private fun thenUserShouldSeeAddBranchesDialog()
   {
      onView(withId(R.id.btn_branch_add)).check(matches(isDisplayed()))
   }
   
   private fun whenUserEntersEmptyBranchName()
   {
      onView(withId(R.id.et_add_branches_name)).perform(replaceText(""))
   }
   
   private fun whenUserEntersEmptyBranchPhone()
   {
      onView(withId(R.id.et_add_branches_phone)).perform(replaceText(""))
   }
   
   private fun whenUserClicksAddBranchOrUpdateButton()
   {
      onView(withId(R.id.btn_branch_add)).perform(click())
      
   }
   
   private fun whenUserEntersInvalidBranchPhone()
   {
      onView(withId(R.id.et_add_branches_phone)).perform(replaceText("abc"))
   }
   
   private fun givenUserEntersValidBranchData()
   {
      onView(withId(R.id.et_add_branches_name)).perform(replaceText("Test Branch"))
      onView(withId(R.id.et_add_branches_phone)).perform(replaceText("1234567890"))
   }
   
   
   private fun givenUserEntersOnlyBranchName()
   {
      onView(withId(R.id.et_add_branches_name)).perform(replaceText("Clothes1"))
   }
   
   private fun thenUserShouldSeeErrorForBranchName()
   {
      onView(withId(R.id.til_add_branches_name)).check(matches(withHint(context.getString(R.string.add_branch_name_empty))))
   }
   
   private fun thenUserShouldSeeErrorForBranchPhone()
   {
      onView(withId(R.id.til_add_branches_phone)).check(matches(withHint(context.getString(R.string.all_phone_invalid))))
   }
   
   private fun thenUserShouldSeeAddBranchConfirmationMessage()
   {
      
      assertThat(
         businessViewModel.isAddBranch.value?.peekContent(), `is`(true))
      
   }
   
   private fun thenUserShouldSeeIsUpdateBranchReturnTrue()
   {
      assertThat(businessViewModel.isUpdateBranch.value?.peekContent(),
         `is`(true))
   }
   
   
}