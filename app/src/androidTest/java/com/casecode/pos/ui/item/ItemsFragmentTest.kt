package com.casecode.pos.ui.item

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.casecode.domain.model.users.Item
import com.casecode.pos.R
import com.casecode.pos.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MediumTest
class ItemsFragmentTest {
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
        launchFragmentInHiltContainer<ItemsFragment> {}
    }
    @Test
    fun selectorAddItem_shouldShowNewItemInList(){
        onView(withId(R.id.btn_sale)).perform(click())


    }
    @Test
    fun testRecyclerViewItemCount(){
        // create a list of items
        val items = listOf(
            Item(name = "Item 1", price = 10.0, quantity = 5.0, sku = "12345"),
            Item(name = "Item 2", price = 20.0, quantity = 10.0, sku = "54321"),
        )
        // Set the items to the adapter


    }

}