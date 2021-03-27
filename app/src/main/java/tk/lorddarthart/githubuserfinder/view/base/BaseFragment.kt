package tk.lorddarthart.githubuserfinder.view.base

import android.content.Context
import androidx.fragment.app.Fragment
import tk.lorddarthart.githubuserfinder.view.activity.MainActivity

/**
 * Base Fragment class that is parent to other fragments of this application
 */
open class BaseFragment : Fragment() {
    protected lateinit var activity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)

        activity = context as MainActivity
    }
}