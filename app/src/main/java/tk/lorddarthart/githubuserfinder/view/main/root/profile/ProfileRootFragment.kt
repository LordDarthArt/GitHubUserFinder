package tk.lorddarthart.githubuserfinder.view.main.root.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import tk.lorddarthart.githubuserfinder.databinding.FragmentProfileRootBinding
import tk.lorddarthart.githubuserfinder.view.base.BaseFragment

class ProfileRootFragment : Fragment() {
    private lateinit var binding: FragmentProfileRootBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileRootBinding.inflate(inflater, container, false)
        return binding.root
    }
}