package tk.lorddarthart.githubuserfinder.view.main.adapter

import androidx.viewpager2.adapter.FragmentStateAdapter
import tk.lorddarthart.githubuserfinder.view.base.BaseFragment

class MainAdapter(
    private val fragments: List<BaseFragment>,
    currentFragment: BaseFragment
) : FragmentStateAdapter(currentFragment) {
    override fun getItemCount() = fragments.size
    override fun createFragment(position: Int) = fragments[position]
}