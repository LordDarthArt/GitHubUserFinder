package tk.lorddarthart.githubuserfinder.util.logs

import android.util.Log
import tk.lorddarthart.githubuserfinder.util.helper.isDebug

/**
 * Types "verbose" logs with exceptions desc only in debug version
 */
fun logv(
    tag: String,
    string: String,
    exception: Exception? = null
) {
    if (isDebug) {
        Log.v(tag, string, exception)
    }
}

/**
 * Types "debug" logs with exceptions desc only in debug version
 */
fun logDebug(
    tag: String,
    string: String,
    exception: Exception? = null
) {
    if (isDebug) {
        Log.d(tag, string, exception)
    }
}

/**
 * Types "info" logs with exceptions desc only in debug version
 */
fun logi(
    tag: String,
    string: String,
    exception: Exception? = null
) {
    if (isDebug) {
        Log.i(tag, string, exception)
    }
}

/**
 * Types "warning" logs with exceptions desc only in debug version
 */
fun logw(
    tag: String,
    string: String,
    exception: Exception? = null
) {
    if (isDebug) {
        Log.w(tag, string, exception)
    }
}

/**
 * Types "error" logs with exceptions desc only in debug version
 */
fun loge(
    tag: String,
    string: String,
    exception: Exception? = null
) {
    if (isDebug) {
        Log.e(tag, string, exception)
    }
}

/**
 * Types "assert" logs with exceptions desc only in debug version
 */
fun logwtf(
    tag: String,
    string: String,
    exception: Exception? = null
) {
    if (isDebug) {
        Log.wtf(tag, string, exception)
    }
}
