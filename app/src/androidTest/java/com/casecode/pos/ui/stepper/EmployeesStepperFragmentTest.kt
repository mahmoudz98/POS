package com.casecode.pos.ui.stepper

import androidx.appcompat.widget.AppCompatTextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.casecode.pos.R
import com.casecode.pos.adapter.EmployeeAdapter
import com.casecode.pos.utils.launchFragmentInHiltContainer
import com.casecode.pos.viewmodel.StepperBusinessViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test for the Employee screen.
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class EmployeesStepperFragmentTest {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var businessViewModel: StepperBusinessViewModel

    @Before
    fun init() {
        hiltRule.inject()
        launchFragmentInHiltContainer<EmployeesStepperFragment> {
            this@EmployeesStepperFragmentTest.businessViewModel = businessViewModel
        }
    }

    @Test
    fun selectorAddEmployee_shouldShowNewEmployeeInList() {
        givenEmployeesAdded()

        onView(withId(R.id.rv_employees)).perform(
                RecyclerViewActions.scrollToPosition<EmployeeAdapter.EmployeeViewHolder>(0),
            )
        onView(withId(R.id.tv_employee_permission_branch)).check(
            matches(
                allOf(
                    instanceOf(AppCompatTextView::class.java),
                    withText("$permissionEmployee/$branchEmployee"),
                ),
            ),
        )
        onView(withId(R.id.tv_employee_name)).check(
            matches(
                allOf(
                    instanceOf(AppCompatTextView::class.java),
                    withText(nameEmployee),
                ),
            ),
        )
        onView(withId(R.id.tv_employee_phone)).check(
            matches(
                allOf(
                    instanceOf(AppCompatTextView::class.java),
                    withText(phoneEmployee),
                ),
            ),
        )
    }

    @Test
    fun selectorAddEmployeeAndThanUpdate_shouldShowUpdateInEmployeeList() {
        // Given - add new employee
        givenEmployeesAdded()
        // When - update the employee
        givenEmployeesUpdated()

        onView(withId(R.id.rv_employees)).perform(
                RecyclerViewActions.scrollToPosition<EmployeeAdapter.EmployeeViewHolder>(0),
            )
        onView(withId(R.id.tv_employee_permission_branch)).check(
            matches(
                allOf(
                    instanceOf(AppCompatTextView::class.java),
                    withText("$updatePermissionEmployee/$updateBranchEmployee"),
                ),
            ),
        )
        onView(withId(R.id.tv_employee_name)).check(
            matches(
                allOf(
                    instanceOf(AppCompatTextView::class.java),
                    withText(updateNameEmployee),
                ),
            ),
        )
        onView(withId(R.id.tv_employee_phone)).check(
            matches(
                allOf(
                    instanceOf(AppCompatTextView::class.java),
                    withText(updatePhoneEmployee),
                ),
            ),
        )
    }


    @Test
    fun selectorDone_whenEmployeeListIsEmpty_showsMessageEmployeesEmpty() {
        businessViewModel.setConnected(true)
        // When - click on the employees done button
        onView(withId(R.id.btn_employees_stepper_done)).perform(click())

        // Then - verify that the employees empty message is shown
        assertThat(
            businessViewModel.userMessage.value?.peekContent(),
            `is`(com.casecode.pos.domain.R.string.employees_empty),
        )
    }

    @Test
    fun selectorDone_whenEmployeesAddedErrorNetwork_showsMessageEmployeesErrorNetwork() {
        // When - click on the employees done button
        businessViewModel.setConnected(false)
        onView(withId(R.id.btn_employees_stepper_done)).perform(click())
        // Then - verify that the error network message is shown
        assertThat(businessViewModel.userMessage.value?.peekContent(), `is`(R.string.network_error))
    }

    private fun givenEmployeesAdded() {
        onView(withId(R.id.btn_employees_add)).perform(click())

        onView(withId(R.id.et_employee_name)).perform(
            ViewActions.replaceText(nameEmployee),
            ViewActions.closeSoftKeyboard(),
        )
        onView(withId(R.id.et_employee_phone)).perform(
            ViewActions.replaceText(phoneEmployee),
            ViewActions.closeSoftKeyboard(),
        )
        onView(withId(R.id.et_employee_password)).perform(
            ViewActions.replaceText(passwordEmployee),
            ViewActions.closeSoftKeyboard(),
        )
        onView(withId(R.id.actv_employee_branch)).perform(
            ViewActions.replaceText(branchEmployee),
            ViewActions.closeSoftKeyboard(),
        )
        onView(withId(R.id.actv_employee_permission)).perform(
            ViewActions.replaceText(permissionEmployee),
            ViewActions.closeSoftKeyboard(),
        )

        onView(withId(R.id.btn_employee)).perform(click())
    }

    private fun givenEmployeesUpdated() {
        onView(withId(R.id.rv_employees)).perform(
                RecyclerViewActions.actionOnItemAtPosition<EmployeeAdapter.EmployeeViewHolder>(
                    0,
                    click(),
                ),
            )
        onView(withId(R.id.et_employee_name)).perform(
            ViewActions.replaceText(updateNameEmployee),
            ViewActions.closeSoftKeyboard(),
        )
        onView(withId(R.id.et_employee_phone)).perform(
            ViewActions.replaceText(updatePhoneEmployee),
            ViewActions.closeSoftKeyboard(),
        )
        onView(withId(R.id.et_employee_password)).perform(
            ViewActions.replaceText(updatePasswordEmployee),
            ViewActions.closeSoftKeyboard(),
        )
        onView(withId(R.id.actv_employee_branch)).perform(
            ViewActions.replaceText(updateBranchEmployee),
            ViewActions.closeSoftKeyboard(),
        )
        onView(withId(R.id.actv_employee_permission)).perform(
            ViewActions.replaceText(updatePermissionEmployee),
            ViewActions.closeSoftKeyboard(),
        )

        onView(withId(R.id.btn_employee)).perform(click())
    }

    // For Add Employee
    private val nameEmployee = "Ahmed"
    private val phoneEmployee = "08123456789"
    private val passwordEmployee = "password232"
    private val branchEmployee = "branch1"
    private val permissionEmployee = "admin"

    // For Update Employee
    private val updateNameEmployee = "Youssef"
    private val updatePhoneEmployee = "08123456789"
    private val updatePasswordEmployee = "password123"
    private val updateBranchEmployee = "branch2"
    private val updatePermissionEmployee = "sale"
}