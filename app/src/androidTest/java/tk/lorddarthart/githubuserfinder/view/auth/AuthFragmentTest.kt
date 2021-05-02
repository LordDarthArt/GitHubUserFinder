package tk.lorddarthart.githubuserfinder.view.auth

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Test
import org.junit.runner.RunWith
import tk.lorddarthart.githubuserfinder.BuildConfig
import tk.lorddarthart.githubuserfinder.R

@RunWith(AndroidJUnit4ClassRunner::class)
class AuthFragmentTest {
    @Test fun testAuthFragment() {
        launchFragmentInContainer<AuthFragment>()

        onView(withId(R.id.auth_box_title)).check(matches(isCompletelyDisplayed()))
    }

    @Test fun testAuthFooterVisibility() {
        launchFragmentInContainer<AuthFragment>()

        if (BuildConfig.DEBUG) {
            onView(withId(R.id.auth_footer)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        } else {
            onView(withId(R.id.auth_footer)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        }
    }
}