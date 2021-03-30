package tk.lorddarthart.githubuserfinder.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import tk.lorddarthart.githubuserfinder.R
import tk.lorddarthart.githubuserfinder.databinding.FragmentMainBinding
import tk.lorddarthart.githubuserfinder.di.fragmentViewModel
import tk.lorddarthart.githubuserfinder.view.base.BaseFragment
import tk.lorddarthart.githubuserfinder.view.main.adapter.MainAdapter
import tk.lorddarthart.githubuserfinder.view.main.profile.ProfileFragment
import tk.lorddarthart.githubuserfinder.view.main.search.SearchFragment

class MainFragment: BaseFragment(), DIAware {
    override val di: DI by closestDI()
    private lateinit var binding: FragmentMainBinding

    private val fragmentsList = listOf(SearchFragment(), ProfileFragment())
    private val viewModel: MainViewModel by fragmentViewModel()
    private val mainAdapter: MainAdapter by lazy { MainAdapter(fragmentsList, this) }

//    private val onNavigationListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.navHostTabFragment.adapter = mainAdapter
        binding.bottomNavBar.setOnNavigationItemSelectedListener { menuItem ->
            viewModel.setCurrentTab(menuItem.itemId)
            when (menuItem.itemId) {
                R.id.search_fragment -> { binding.navHostTabFragment.setCurrentItem(0, true); return@setOnNavigationItemSelectedListener true }
                R.id.profile_fragment -> { binding.navHostTabFragment.setCurrentItem(1, true); return@setOnNavigationItemSelectedListener true }
            }
            false
        }

        binding.navHostTabFragment.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.setCurrentTab(
                    when (position) {
                        0 -> binding.bottomNavBar.menu.getItem(0).itemId
                        1 -> binding.bottomNavBar.menu.getItem(1).itemId
                        else -> throw NotImplementedError("Unknown bottomnav id for page: $position")
                    }
                )
                super.onPageSelected(position)
            }
        })

        viewModel.currentTabLiveData.observe(viewLifecycleOwner) {
            it?.let {
                binding.bottomNavBar.selectedItemId = it
            }
        }

        return binding.root
    }
}