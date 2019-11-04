package tk.lorddarthart.githubuserfinder.util.helper

import android.view.View

fun View.setVisible(show: Boolean) {
    if (show) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}