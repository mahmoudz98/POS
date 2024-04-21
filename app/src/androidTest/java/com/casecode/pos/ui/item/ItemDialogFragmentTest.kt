package com.casecode.pos.ui.item

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.casecode.pos.R
import com.casecode.pos.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Before
import org.junit.Rule

@MediumTest
class ItemDialogFragmentTest
{
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
        launchFragmentInHiltContainer<ItemsFragment>()
        onView(withId(R.id.btn_employees_add)).perform(click())
    }
}