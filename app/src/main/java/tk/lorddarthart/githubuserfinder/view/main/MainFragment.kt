package tk.lorddarthart.githubuserfinder.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.viewpager2.widget.ViewPager2
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import tk.lorddarthart.githubuserfinder.R
import tk.lorddarthart.githubuserfinder.databinding.FragmentMainBinding
import tk.lorddarthart.githubuserfinder.di.fragmentViewModel
import tk.lorddarthart.githubuserfinder.domain.local.MainTabConverter
import tk.lorddarthart.githubuserfinder.view.base.BaseFragment
import tk.lorddarthart.githubuserfinder.view.main.adapter.MainAdapter
import tk.lorddarthart.githubuserfinder.view.main.favourite.FavouriteFragment
import tk.lorddarthart.githubuserfinder.view.main.profile.ProfileFragment
import tk.lorddarthart.githubuserfinder.view.main.root.favourite.FavouriteRootFragment
import tk.lorddarthart.githubuserfinder.view.main.root.profile.ProfileRootFragment
import tk.lorddarthart.githubuserfinder.view.main.root.search.SearchRootFragment

class MainFragment: BaseFragment(), DIAware {
    override val di: DI by closestDI()
    override val viewModel: MainViewModel by fragmentViewModel()
    private val mainTabConverter: MainTabConverter by instance()

    private val mainAdapter by lazy { MainAdapter(this) }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        (binding as FragmentMainBinding).apply {
            viewModel = this@MainFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            navHostTabFragment.adapter = mainAdapter

            setOnSearchClick { navHostTabFragment.setCurrentItem(0, true) }
            setOnFavouriteClick { navHostTabFragment.setCurrentItem(1, true) }
            setOnProfileClick { navHostTabFragment.setCurrentItem(2, true) }

            navHostTabFragment.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    this@MainFragment.viewModel.setCurrentTab(mainTabConverter.getIdByPosition(position))
                    super.onPageSelected(position)
                }
            })
        }
    }

    override fun initListeners() {
        super.initListeners()
        (binding as? FragmentMainBinding)?.apply {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                if (this@MainFragment.viewModel.tabNavigationHistory.size > 1) {
                    this@MainFragment.viewModel.tabNavigationHistory.remove(this@MainFragment.viewModel.tabNavigationHistory.last())
                    this@MainFragment.viewModel.setCurrentTab(this@MainFragment.viewModel.tabNavigationHistory.last())
                    navHostTabFragment.setCurrentItem(mainTabConverter.getPositionById(this@MainFragment.viewModel.tabNavigationHistory.last()), true)
                } else {
                    showExitDialog()
                }
            }
        }
    }

    override fun hangObservers() {
        super.hangObservers()
        viewModel.apply {
            currentTabLiveData.observe(viewLifecycleOwner) {
                it?.let {
                    if (searchSelected.get() != (it == R.id.search_fragment)) { searchSelected.set(it == R.id.search_fragment)}
                    if (favouriteSelected.get() != (it == R.id.favourite_fragment)) { favouriteSelected.set(it == R.id.favourite_fragment)}
                    if (profileSelected.get() != (it == R.id.profile_fragment)) { profileSelected.set(it == R.id.profile_fragment)}
                }
            }
        }
    }
}