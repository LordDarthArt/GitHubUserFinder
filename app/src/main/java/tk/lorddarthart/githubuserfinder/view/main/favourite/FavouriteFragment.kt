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

class FavouriteFragment: BaseFragment(), DIAware {
    override val di: DI by closestDI()
    private lateinit var binding: FragmentFavouriteBinding

    private val viewModel: FavouriteViewModel by activityScopedFragmentViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }
}