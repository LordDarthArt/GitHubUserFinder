package tk.lorddarthart.githubuserfinder.view.auth.additional

import android.content.Intent
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import tk.lorddarthart.githubuserfinder.R
import tk.lorddarthart.githubuserfinder.view.activity.MainActivity

@RunWith(AndroidJUnit4ClassRunner::class)
class AuthBoxFragmentTest {

//    @get:Rule
//    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testCorrectnessOfTitle() {
        launchFragmentInContainer<AuthBoxFragment>()

        onView(withId(R.id.auth_box_title)).check(matches(withText(R.string.sign_in)))
    }

//    @Test fun testAuthIconsAreCorrect() {
//        val scenario = launchFragmentInContainer { AuthBoxFragment() }
//
//        onView(withId(R.id.auth_box_google)).check(matches(withS))
//    }
}