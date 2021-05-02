package tk.lorddarthart.githubuserfinder.view.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import tk.lorddarthart.githubuserfinder.databinding.FragmentProfileBinding
import tk.lorddarthart.githubuserfinder.di.activityScopedFragmentViewModel
import tk.lorddarthart.githubuserfinder.view.base.BaseFragment
import tk.lorddarthart.githubuserfinder.view.base.BaseTabFragment

class ProfileFragment : BaseTabFragment(), DIAware {
    override val di: DI by closestDI()
    override val viewModel: ProfileViewModel by activityScopedFragmentViewModel()

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        (binding as FragmentProfileBinding).apply {
            viewModel = this@ProfileFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }
    }
}