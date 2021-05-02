package tk.lorddarthart.githubuserfinder.view.main.root.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import tk.lorddarthart.githubuserfinder.databinding.FragmentSearchRootBinding
import tk.lorddarthart.githubuserfinder.view.base.BaseFragment

class SearchRootFragment : Fragment () {
    private lateinit var binding: FragmentSearchRootBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchRootBinding.inflate(inflater, container, false)
        return binding.root
    }
}