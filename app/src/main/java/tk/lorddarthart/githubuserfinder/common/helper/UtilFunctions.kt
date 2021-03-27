package tk.lorddarthart.githubuserfinder.common.helper

import android.graphics.Rect
import android.view.View
import tk.lorddarthart.githubuserfinder.BuildConfig

/**
 * Boolean that checks if current build is DEBUG build
 */
val isDebug = BuildConfig.DEBUG


/** Boolean that checks that preferred view is displayed to user */
inline fun <reified T: View?> T.isDisplayedOnScreen(): Boolean {
    if (this == null) { return false }
    if (!isShown) { return false }
    val actualPosition = Rect()
    getGlobalVisibleRect(actualPosition)
    val screen = Rect(0, 0, context.resources.displayMetrics.widthPixels, context.resources.displayMetrics.heightPixels)
    return actualPosition.intersect(screen)
}