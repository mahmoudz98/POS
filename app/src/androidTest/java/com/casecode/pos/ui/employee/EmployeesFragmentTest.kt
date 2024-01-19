package com.casecode.pos.ui.employee

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.casecode.pos.R
import com.casecode.pos.utils.DataBindingIdlingRes
import com.casecode.pos.utils.EspressoIdlingResource
import com.casecode.pos.utils.launchFragmentInHiltContainer
import com.casecode.pos.utils.monitorFragment
import com.casecode.pos.viewmodel.BusinessViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class EmployeesFragmentTest
{
   
   @get:Rule(order = 0)
   var hiltRule = HiltAndroidRule(this)
   
   // Executes tasks in the Architecture Components in the same thread
   @get:Rule(order = 1)
   var instantTaskExecutorRule = InstantTaskExecutorRule()
   
   private lateinit var businessViewModel: BusinessViewModel
   
   // An idling resource that waits for Data Binding to have no pending bindings.
   private val dataBindingIdlingResource = DataBindingIdlingRes()
   
   @Before
   fun init()
   {
      hiltRule.inject()
      
      launchFragmentInHiltContainer<EmployeesFragment> {
         this@EmployeesFragmentTest.businessViewModel = businessViewModel
         dataBindingIdlingResource.monitorFragment(this)
         
      }
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
   
   /**
    * test when employees is add success, empty employee and uid, error network and
    */
   
   @Test
   fun selectorDone_whenEmployeesAddedSuccess_showsMessageEmployeesSuccess()
   {
      // Given - add new employee
      businessViewModel.newEmployee("name", "2123123", "123445", "branch", "admin")
      businessViewModel.addEmployee()
      businessViewModel.setConnected(true)
      // When - click on the employees done button
      Thread.sleep(2000)
      onView(withId(R.id.btn_employees_done)).perform(click())
      // Then - verify that the employees success message is shown
      assertThat(businessViewModel.userMessage.value?.peekContent(),
         `is`(R.string.add_employees_success))
   }
   
   @Test
   fun selectorDone_whenEmployeesAddedEmpty_showsMessageEmployeesEmpty()
   {
      businessViewModel.setConnected(true)
      // When - click on the employees done button
      onView(withId(R.id.btn_employees_done)).perform(click())
      
      // Then - verify that the employees empty message is shown
      assertThat(businessViewModel.userMessage.value?.peekContent(),
         `is`(com.casecode.pos.domain.R.string.employees_empty))
   }
   
   @Test
   fun selectorDone_whenEmployeesAddedErrorNetwork_showsMessageEmployeesErrorNetwork()
   {
      // When - click on the employees done button
      businessViewModel.setConnected(false)
      onView(withId(R.id.btn_employees_done)).perform(click())
      // Then - verify that the error network message is shown
      assertThat(businessViewModel.userMessage.value?.peekContent(), `is`(R.string.network_error))
      
   }
   
   
}