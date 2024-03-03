package com.casecode.pos.ui.business

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.casecode.pos.R
import com.casecode.pos.utils.launchFragmentInHiltContainer
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
@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AddBusinessFragmentTest {
    // Ensures that the Hilt component is initialized before running the ActivityScenarioRule
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val validEmail = "validEmail@example.com"
    private val invalidEmail = "invalidEmail.com"
    private val validPhone = "1234567890"
    private val invalidPhone = "12"
    private val validStoreType = "Clothes"
    private val emptyStoreType = ""

    @Before
    fun init() {
        hiltRule.inject()
        // Subject under test
        launchFragmentInHiltContainer<AddBusinessFragment> {
        }
    }

    @Test
    fun shouldShowError_WhenStoreTypeIsEmpty() {
        // Given - on the add business screen
        givenUserHasEnteredEmptyStoreType()

        // When
        whenUserClicksSubmitButton()

        // Then
        thenUserShouldSeeStoreTypeEmptyError()
    }

    @Test
    fun shouldShowError_WhenEmailInvalid() {
        // Given
        givenUserHasEnteredInvalidEmail()

        // When
        whenUserClicksSubmitButton()

        // Then
        thenUserShouldSeeEmailInvalidError()
    }

    @Test
    fun shouldShowError_WhenPhoneIsInValid() {
        // Given
        givenUserHasEnteredInvalidPhone()

        // When
        whenUserClicksSubmitButton()
        // Then
        thenUserShouldSeePhoneInvalidError()
    }

    @Test
    fun shouldShowStoreType_EmptyError_WhenStoreTypeIsEmpty() {
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
    fun shouldShowEmailInvalid_WhenEmailIsInvalid() {
        // Given
        givenUserHasEnteredValidStoreType()
        givenUserHasEnteredValidPhone()
        givenUserHasEnteredInvalidEmail()

        // When
        whenUserClicksSubmitButton()

        // Then
        thenUserShouldSeeEmailInvalidError()
    }

    private fun givenUserHasEnteredValidEmail() {
        onView(withId(R.id.et_business_mail)).perform(typeText(validEmail), closeSoftKeyboard())
    }

    private fun givenUserHasEnteredInvalidEmail() {
        onView(withId(R.id.et_business_mail)).perform(typeText(invalidEmail), closeSoftKeyboard())
    }

    private fun givenUserHasEnteredValidPhone() {
        onView(withId(R.id.et_business_phone)).perform(typeText(validPhone), closeSoftKeyboard())
    }

    private fun givenUserHasEnteredInvalidPhone() {
        onView(withId(R.id.et_business_phone)).perform(typeText(invalidPhone), closeSoftKeyboard())
    }

    private fun givenUserHasEnteredEmptyStoreType() {
        onView(withId(R.id.actv_business)).perform(typeText(emptyStoreType))
    }

    private fun givenUserHasEnteredValidStoreType() {
        onView(withId(R.id.actv_business)).perform(typeText(validStoreType))
    }

    private fun whenUserClicksSubmitButton() {
        onView(withId(R.id.btn_add_business_branches)).perform(click())
    }

    private fun thenUserShouldSeeStoreTypeEmptyError() {
        onView(withId(R.id.til_business_type))
            .check(matches(withHint(context.getString(R.string.add_business_store_type_empty))))
    }

    private fun thenUserShouldSeeEmailInvalidError() {
        onView(withId(R.id.til_business_mail))
            .check(matches(withHint(context.getString(R.string.add_business_email_invalid))))
    }

    private fun thenUserShouldSeePhoneInvalidError() {
        onView(withId(R.id.til_business_phone))
            .check(matches(withHint(context.getString(R.string.add_business_phone_invalid))))
    }
}