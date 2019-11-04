package tk.lorddarthart.githubuserfinder.application.view.base

import android.content.Context
import androidx.fragment.app.Fragment
import tk.lorddarthart.githubuserfinder.application.view.activity.MainActivity

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