package tk.lorddarthart.githubuserfinder.view.main.root.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import tk.lorddarthart.githubuserfinder.databinding.FragmentFavouriteRootBinding
import tk.lorddarthart.githubuserfinder.view.base.BaseFragment

class FavouriteRootFragment : Fragment() {
    private lateinit var binding: FragmentFavouriteRootBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteRootBinding.inflate(inflater, container, false)
        return binding.root
    }
}