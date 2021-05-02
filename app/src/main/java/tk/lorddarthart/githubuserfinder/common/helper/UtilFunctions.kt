package tk.lorddarthart.githubuserfinder.common.helper

import android.graphics.Rect
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.ui.AppBarConfiguration
import tk.lorddarthart.githubuserfinder.BuildConfig
import tk.lorddarthart.githubuserfinder.R

/** Boolean that checks if current build is DEBUG build. */
val isDebug = BuildConfig.DEBUG

val APP_BAR_CONFIGURATION = AppBarConfiguration(setOf(
    R.id.search_fragment,
    R.id.favourite_fragment,
    R.id.profile_fragment
))


/** Boolean that checks that preferred view is displayed to user */
inline fun <reified T: View?> T.isDisplayedOnScreen(): Boolean {
    if (this == null) { return false }
    if (!isShown) { return false }
    val actualPosition = Rect()
    getGlobalVisibleRect(actualPosition)
    val screen = Rect(0, 0, context.resources.displayMetrics.widthPixels, context.resources.displayMetrics.heightPixels)
    return actualPosition.intersect(screen)
}

fun <T> LiveData<Event<T>>.observeEvent(lifecycleOwner: LifecycleOwner, observer: (T) -> Unit) {
    observe(lifecycleOwner) {
        it.getContentIfNotHandled()?.let { t ->
            observer(t)
        }
    }
}