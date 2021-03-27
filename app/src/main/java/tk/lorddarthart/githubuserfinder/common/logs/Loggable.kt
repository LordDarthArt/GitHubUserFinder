package tk.lorddarthart.githubuserfinder.common.logs

import android.util.Log
import tk.lorddarthart.githubuserfinder.common.helper.isDebug

interface Loggable

/** Lazy wrapper over [Log.v] */
inline fun <reified T : Loggable> T.logv(
    onlyInDebugMode: Boolean = true,
    lazyMessage: () -> String
) {
    log(onlyInDebugMode) {
        Log.v(
            getClassSimpleName(),
            lazyMessage.invoke()
        )
    }
}

/** Lazy wrapper over [Log.d] */
inline fun <reified T : Loggable> T.logDebug(
    onlyInDebugMode: Boolean = true,
    lazyMessage: () -> String
) {
    log(onlyInDebugMode) {
        Log.d(
            getClassSimpleName(),
            lazyMessage.invoke()
        )
    }
}

/** Lazy wrapper over [Log.i] */
inline fun <reified T : Loggable> T.logi(
    onlyInDebugMode: Boolean = true,
    lazyMessage: () -> String
) {
    log(onlyInDebugMode) {
        Log.i(
            getClassSimpleName(),
            lazyMessage.invoke()
        )
    }
}

/** Lazy wrapper over [Log.i] */
inline fun <reified T : Loggable> T.logw(
    onlyInDebugMode: Boolean = true,
    lazyMessage: () -> String
) {
    log(onlyInDebugMode) {
        Log.w(
            getClassSimpleName(),
            lazyMessage.invoke()
        )
    }
}

/** Lazy wrapper over [Log.e] */
inline fun <reified T : Loggable> T.loge(
    onlyInDebugMode: Boolean = true,
    exception: Exception? = null,
    lazyMessage: () -> String
) {
    log(onlyInDebugMode) {
        Log.e(
            getClassSimpleName(),
            lazyMessage.invoke(),
            exception
        )
    }
}

/** Lazy wrapper over [Log.e] */
inline fun <reified T : Loggable> T.loge(
    onlyInDebugMode: Boolean = true,
    lazyMessage: () -> String
) {
    log(onlyInDebugMode) {
        Log.e(
            getClassSimpleName(),
            lazyMessage.invoke()
        )
    }
}

inline fun log(onlyInDebugMode: Boolean, logger: () -> Unit) {
    when {
        onlyInDebugMode && isDebug -> logger()
        !onlyInDebugMode -> logger()
    }
}

/**
 * Utility that returns the name of the class from within it is invoked.
 */
inline fun <reified T : Loggable> T.getClassSimpleName(): String =
    if (T::class.java.simpleName.isNotBlank()) {
        T::class.java.simpleName
    } else {
        "Anonymous"
    }