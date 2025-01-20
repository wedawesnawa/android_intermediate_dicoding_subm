package id.example.storyapp.ui.addStory

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import id.example.storyapp.R
import id.example.storyapp.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddStoryActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(AddStoryActivity::class.java)

    @Before
    fun setup() {
        EspressoIdlingResource.increment()
    }

    @After
    fun tearDown() {
        EspressoIdlingResource.decrement()
    }

    @Test
    fun testAddStoryWorkflow() {

        Espresso.onView(ViewMatchers.withId(R.id.iv_preview))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.ed_add_description))
            .perform(
                ViewActions.typeText("Test Story Description"),
                ViewActions.closeSoftKeyboard()
            )
        Espresso.onView(ViewMatchers.withId(R.id.btnGallery))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.checkBox))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))
        Espresso.onView(ViewMatchers.withId(R.id.button_add))
            .perform(ViewActions.click())

    }
}
