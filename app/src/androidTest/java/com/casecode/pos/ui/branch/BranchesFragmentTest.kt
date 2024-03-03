package com.casecode.pos.ui.branch

import androidx.appcompat.widget.AppCompatTextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withResourceName
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.casecode.domain.model.users.Branch
import com.casecode.pos.R
import com.casecode.pos.adapter.BranchesAdapter
import com.casecode.pos.utils.launchFragmentInHiltContainer
import com.casecode.pos.viewmodel.StepperBusinessViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers
import org.hamcrest.collection.IsMapContaining.hasEntry
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.Description
import org.junit.runner.RunWith
import java.util.regex.Matcher
import javax.inject.Inject

/**
 * Integration test for the  Branches screen.
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class BranchesFragmentTest {


    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()



    private val nameBranch = "Branch 1"
    private val phoneBranch = "123456789"
    private val context = InstrumentationRegistry.getInstrumentation().targetContext


    @Before
    fun init() {
        hiltRule.inject()

        // Subject under test
        launchFragmentInHiltContainer<BranchesFragment> {
        }
    }

    @Test
    fun selectingUserAddBranch_thenShowsItemInBranchRecyclerview() {

        // select add a new branch
        selectingAddBranch()
        onView(withId(R.id.rv_branches))
            .perform(
                RecyclerViewActions.scrollToPosition<BranchesAdapter.BranchesViewHolder>(0),
            )

        onView(withId(R.id.tv_branch_code)).check(
            matches(
                allOf(
                    instanceOf(AppCompatTextView::class.java),
                    withText("01"),
                ),
            ),
        )
        onView(withId(R.id.tv_branch_name)).check(
            matches(
                allOf(
                    instanceOf(AppCompatTextView::class.java),
                    withText(context.getString(R.string.branch_name) + nameBranch),
                ),
            ),
        )
        onView(withId(R.id.tv_branch_phone)).check(
            matches(
                allOf(
                    instanceOf(AppCompatTextView::class.java),
                    withText(phoneBranch),
                ),
            ),
        )


    }

    @Test
    fun shouldHideRecycleView_whenBranchesAreEmpty() {

        onView(withId(R.id.rv_branches)).check(matches((not(isDisplayed()))))

        thenUserShouldSeeAddBranchesButton()
    }


    private fun selectingAddBranch() {
        onView(withId(R.id.btn_branches_add)).perform(click())
        onView(withId(R.id.et_add_branches_name)).perform(replaceText(nameBranch))
        onView(withId(R.id.et_add_branches_phone)).perform(replaceText(phoneBranch))
        onView(withId(R.id.btn_branch)).perform(click())

    }

    private fun thenUserShouldSeeAddBranchesButton() {
        onView(withId(R.id.btn_branches_add)).check(matches(isDisplayed()))
    }


}