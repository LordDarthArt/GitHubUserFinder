package tk.lorddarthart.githubuserfinder.common.helper

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("app:visibility")
fun setVisibility(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}