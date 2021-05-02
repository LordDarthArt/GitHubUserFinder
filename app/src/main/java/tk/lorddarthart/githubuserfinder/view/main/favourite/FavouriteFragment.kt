package tk.lorddarthart.githubuserfinder.view.main.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import tk.lorddarthart.githubuserfinder.databinding.FragmentFavouriteBinding
import tk.lorddarthart.githubuserfinder.di.activityScopedFragmentViewModel
import tk.lorddarthart.githubuserfinder.view.base.BaseFragment
import tk.lorddarthart.githubuserfinder.view.base.BaseTabFragment

class FavouriteFragment: BaseTabFragment(), DIAware {
    override val di: DI by closestDI()
    override val viewModel: FavouriteViewModel by activityScopedFragmentViewModel()

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        (binding as? FragmentFavouriteBinding)?.apply {
            viewModel = this@FavouriteFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }
    }
}