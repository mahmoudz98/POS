package com.casecode.pos.ui.employee

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
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
 * Integration test for the  add Employee screen.
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AddEmployeeDialogFragmentTest {
    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun init() {
        hiltRule.inject()
        // Subject under test
        launchFragmentInHiltContainer<EmployeesFragment> {}
        onView(withId(R.id.btn_employees_add)).perform(click())
    }

    @Test
    fun whenEmployeeInputEmpty_ShowMessageInputEmpty() {
        onView(withId(R.id.et_add_employee_name)).perform(replaceText(""), closeSoftKeyboard())

        onView(withId(R.id.et_add_employee_phone)).perform(
            replaceText(""),
            closeSoftKeyboard(),
        )
        onView(withId(R.id.et_add_employee_password)).perform(
            replaceText(""),
            closeSoftKeyboard(),
        )
        onView(withId(R.id.actv_employee_branch)).perform(
            replaceText(""),
            closeSoftKeyboard(),
        )
        onView(withId(R.id.actv_employee_permission)).perform(
            replaceText(""),
            closeSoftKeyboard(),
        )

        onView(withId(R.id.btn_employee)).perform(click())

        onView(withId(R.id.til_add_employee_name)).check(
            matches(withHint(context.getString(R.string.add_employee_name_empty))),
        )
        onView(withId(R.id.til_add_employee_phone)).check(
            matches(withHint(context.getString(R.string.all_phone_empty))),
        )
        onView(withId(R.id.til_add_employee_password)).check(
            matches(withHint(context.getString(R.string.add_employee_password_empty))),
        )
        onView(withId(R.id.til_add_employee_branch)).check(
            matches(withHint(context.getString(R.string.add_employee_branch_empty))),
        )
        onView(withId(R.id.til_add_employee_permission)).check(
            matches(withHint(context.getString(R.string.add_employee_permission_empty))),
        )
    }

    @Test
    fun whenEmployeeInputValid_ShowMessageSuccess() {
        onView(withId(R.id.et_add_employee_name)).perform(replaceText("test"), closeSoftKeyboard())
        onView(withId(R.id.et_add_employee_phone)).perform(
            replaceText("08123456789"),
            closeSoftKeyboard(),
        )
        onView(withId(R.id.et_add_employee_password)).perform(
            replaceText("test232"),
            closeSoftKeyboard(),
        )
        onView(withId(R.id.actv_employee_branch)).perform(replaceText("test"), closeSoftKeyboard())
        onView(withId(R.id.actv_employee_permission)).perform(
            replaceText("test"),
            closeSoftKeyboard(),
        )

        onView(withId(R.id.btn_employee)).perform(click())

        onView(withId(R.id.btn_employees_done)).check(matches(isDisplayed()))
    }
}