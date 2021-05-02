package tk.lorddarthart.githubuserfinder.view.base

import androidx.navigation.findNavController
import tk.lorddarthart.githubuserfinder.R

abstract class BaseTabFragment: BaseFragment() {
    protected val parentNavController by lazy { requireActivity().findNavController(R.id.nav_host_fragment) }
}