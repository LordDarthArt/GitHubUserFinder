package tk.lorddarthart.githubuserfinder.view.fragment.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import tk.lorddarthart.githubuserfinder.view.base.BaseFragment
import tk.lorddarthart.githubuserfinder.view.fragment.auth.additional.AuthBoxFragment
import tk.lorddarthart.githubuserfinder.databinding.FragmentAuthBinding

class AuthFragment : BaseFragment() {
    private lateinit var authBinding: FragmentAuthBinding

    private val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(
            this
        )[AuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authBinding = FragmentAuthBinding.inflate(inflater, container, false)

        initialization()

        return authBinding.root
    }

    private fun initialization() {
        start()
    }

    private fun start() {
        activity.supportFragmentManager
            .beginTransaction()
            .replace(authBinding.authFragment.id, AuthBoxFragment())
            .commitAllowingStateLoss()
    }
}
