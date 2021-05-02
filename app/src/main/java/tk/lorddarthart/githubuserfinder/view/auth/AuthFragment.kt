package tk.lorddarthart.githubuserfinder.view.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import tk.lorddarthart.githubuserfinder.BuildConfig
import tk.lorddarthart.githubuserfinder.view.base.BaseFragment
import tk.lorddarthart.githubuserfinder.view.auth.additional.AuthBoxFragment
import tk.lorddarthart.githubuserfinder.databinding.FragmentAuthBinding
import tk.lorddarthart.githubuserfinder.di.fragmentViewModel

class AuthFragment : BaseFragment(), DIAware {
    override val di: DI by closestDI()

    override val viewModel: AuthViewModel by fragmentViewModel()

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        (binding as FragmentAuthBinding).apply {
            viewModel = this@AuthFragment.viewModel
            lifecycleOwner = viewLifecycleOwner

            setOnMockPress { findNavController().navigate(AuthFragmentDirections.actionGlobalToMain()) }
        }
    }

    override fun initListeners() {
        (binding as FragmentAuthBinding).apply {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                showExitDialog()
            }
        }
    }

    override fun start() {
        viewModel.debug.set(BuildConfig.DEBUG)
    }
}
