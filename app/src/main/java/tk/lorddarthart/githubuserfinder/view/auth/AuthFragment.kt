package tk.lorddarthart.githubuserfinder.view.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import tk.lorddarthart.githubuserfinder.view.base.BaseFragment
import tk.lorddarthart.githubuserfinder.view.auth.additional.AuthBoxFragment
import tk.lorddarthart.githubuserfinder.databinding.FragmentAuthBinding
import tk.lorddarthart.githubuserfinder.di.fragmentViewModel

class AuthFragment : BaseFragment(), DIAware {
    override val di: DI by closestDI()
    private lateinit var binding: FragmentAuthBinding

    private val viewModel: AuthViewModel by fragmentViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAuthBinding.inflate(inflater, container, false)

        binding.setOnMockPress { findNavController().navigate(AuthFragmentDirections.actionGlobalToMain()) }
        initialization()

        return binding.root
    }

    private fun initialization() {
        start()
    }

    private fun start() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(binding.authFragment.id, AuthBoxFragment())
            .commit()
    }
}
