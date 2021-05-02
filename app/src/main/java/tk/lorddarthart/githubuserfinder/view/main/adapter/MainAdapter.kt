package tk.lorddarthart.githubuserfinder.view.main.adapter

import androidx.viewpager2.adapter.FragmentStateAdapter
import tk.lorddarthart.githubuserfinder.view.base.BaseFragment
import tk.lorddarthart.githubuserfinder.view.main.root.favourite.FavouriteRootFragment
import tk.lorddarthart.githubuserfinder.view.main.root.profile.ProfileRootFragment
import tk.lorddarthart.githubuserfinder.view.main.root.search.SearchRootFragment

class MainAdapter(
    currentFragment: BaseFragment
) : FragmentStateAdapter(currentFragment) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int) = when (position) {
        0 -> SearchRootFragment()
        1 -> FavouriteRootFragment()
        2 -> ProfileRootFragment()
        else -> throw NotImplementedError("Unknown page")
    }
}