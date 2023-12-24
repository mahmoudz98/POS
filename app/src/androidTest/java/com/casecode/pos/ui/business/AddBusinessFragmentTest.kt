package com.casecode.pos.ui.business

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.casecode.pos.R
import com.casecode.pos.ui.stepper.StepperActivity
import com.casecode.pos.utils.withHint
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test for the Add Business screen.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
@HiltAndroidTest
class AddBusinessFragmentTest
{
   // Ensures that the Hilt component is initialized before running the ActivityScenarioRule
   @get:Rule(order = 0)
   var hiltRule = HiltAndroidRule(this)
   
   // subject under test
   @Rule(order = 1)
   @JvmField
   val activityRule = ActivityScenarioRule(StepperActivity::class.java)
   private lateinit var acitvity: StepperActivity
   
   private val validEmail = "validEmail@example.com"
   private val invalidEmail = "invalidEmail.com"
   private val validPhone = "1234567890"
   private val invalidPhone = "12"
   private val validStoreType = "Clothes"
   private val emptyStoreType = ""
   
   
   @Before
   fun init()
   {
      hiltRule.inject()
      
      activityRule.scenario.onActivity {
         acitvity = it
      }
   }
   
   @Test
   fun shouldNavigateToNextStepWhenAllFieldsAreValid()
   {
      // Given
      givenUserHasEnteredValidStoreType()
      givenUserHasEnteredValidEmail()
      givenUserHasEnteredValidPhone()
      
      // When
      whenUserClicksSubmitButton()
      
      // Then
      thenUserShouldBeDirectedToNextScreenWithBranches()
   }
   
   @Test
   fun shouldShowErrorWhenStoreTypeIsEmpty()
   {
      // Given - on the add business screen
      givenUserHasEnteredEmptyStoreType()
      
      // When
      whenUserClicksSubmitButton()
      
      // Then
      thenUserShouldSeeStoreTypeEmptyError()
      
   }
   
   @Test
   fun shouldShowErrorWhenEmailInvalid()
   {
      // Given
      givenUserHasEnteredInvalidEmail()
      
      // When
      whenUserClicksSubmitButton()
      
      // Then
      thenUserShouldSeeEmailInvalidError()
      
   }
   
   
   @Test
   fun shouldShowErrorWhenPhoneIsInValid()
   {
      // Given
      givenUserHasEnteredInvalidPhone()
      
      // When
      whenUserClicksSubmitButton()
      // Then
      thenUserShouldSeePhoneInvalidError()
   }
   
   @Test
   fun shouldShowStoreTypeEmptyErrorWhenStoreTypeIsEmpty()
   {
      // Given
      givenUserHasEnteredValidEmail()
      givenUserHasEnteredValidPhone()
      givenUserHasEnteredEmptyStoreType()
      
      // When
      whenUserClicksSubmitButton()
      
      // Then
      thenUserShouldSeeStoreTypeEmptyError()
   }
   
   @Test
   fun shouldShowEmailInvalidErrorWhenEmailIsInvalid()
   {
      // Given
      givenUserHasEnteredValidStoreType()
      givenUserHasEnteredValidPhone()
      givenUserHasEnteredInvalidEmail()
      
      // When
      whenUserClicksSubmitButton()
      
      // Then
      thenUserShouldSeeEmailInvalidError()
   }
   
   private fun givenUserHasEnteredValidEmail()
   {
      onView(withId(R.id.et_business_mail)).perform(typeText(validEmail), closeSoftKeyboard())
      
   }
   
   private fun givenUserHasEnteredInvalidEmail()
   {
      onView(withId(R.id.et_business_mail)).perform(typeText(invalidEmail), closeSoftKeyboard())
      
   }
   
   private fun givenUserHasEnteredValidPhone()
   {
      onView(withId(R.id.et_business_phone)).perform(typeText(validPhone), closeSoftKeyboard())
      
   }
   
   private fun givenUserHasEnteredInvalidPhone()
   {
      onView(withId(R.id.et_business_phone)).perform(typeText(invalidPhone), closeSoftKeyboard())
      
   }
   
   private fun givenUserHasEnteredEmptyStoreType()
   {
      onView(withId(R.id.actv_business)).perform(typeText(emptyStoreType))
      //  onData(anything()).atPosition(-1).inRoot(RootMatchers.isPlatformPopup()).perform(click())
      
   }
   
   private fun givenUserHasEnteredValidStoreType()
   {
      onView(withId(R.id.actv_business)).perform(typeText(validStoreType))
   }
   
   private fun whenUserClicksSubmitButton()
   {
      onView(withId(R.id.btn_add_business_branches)).perform(click())
   }
   
   private fun thenUserShouldSeeStoreTypeEmptyError()
   {
      onView(withId(R.id.til_business_type))
         .check(matches(withHint(acitvity.getString(R.string.add_business_store_type_empty))))
   }
   
   private fun thenUserShouldSeeEmailInvalidError()
   {
      onView(withId(R.id.til_business_mail))
         .check(matches(withHint(acitvity.getString(R.string.add_business_email_invalid))))
   }
   
   private fun thenUserShouldSeePhoneInvalidError()
   {
      onView(withId(R.id.til_business_phone))
         .check(matches(withHint(acitvity.getString(R.string.add_business_phone_invalid))))
   }
   
   private fun thenUserShouldBeDirectedToNextScreenWithBranches()
   {
      onView(withId(R.id.csl_branches_root)).check(matches(isDisplayed()))
   }
   
   
}