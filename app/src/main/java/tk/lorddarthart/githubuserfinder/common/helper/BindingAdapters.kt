package tk.lorddarthart.githubuserfinder.common.helper

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2

@BindingAdapter("app:visibility")
fun setVisibility(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}

@BindingAdapter("app:user_input_enabled")
fun setUserInputEnabled(view: ViewPager2, isEnabled: Boolean) {
    view.isUserInputEnabled = isEnabled
}