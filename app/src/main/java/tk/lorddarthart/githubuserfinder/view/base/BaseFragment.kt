package tk.lorddarthart.githubuserfinder.view.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tk.lorddarthart.githubuserfinder.R
import tk.lorddarthart.githubuserfinder.common.helper.observeEvent
import tk.lorddarthart.githubuserfinder.view.activity.MainActivityViewModel
import kotlin.system.exitProcess

/** Base Fragment class that is parent to other fragments of this application */
abstract class BaseFragment : Fragment() {
    /** Instance of binding for current fragment. */
    open lateinit var binding: ViewDataBinding

    /** Current fragment's viewModel. */
    abstract val viewModel: BaseViewModel

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initBinding(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()
    }

    /**
     * Function that initializes the [binding] instance
     *
     * @param inflater Current fragment's inflater;
     * @param container Current fragment's container;
     * @param savedInstanceState Current fragment's savedInstanceState;
     */
    abstract fun initBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)

    /** Function called to set up everything for the current fragment (e.g. observers, config, data source, etc). */
    open fun initialization() {
        initListeners()
        hangObservers()
        start()
    }

    /** Function called to set up every fragment view's listener that needs to be set up. */
    open fun initListeners() { /* empty by default */ }

    /** Function called to assign actions to observables (LiveData) in viewModels. */
    open fun hangObservers() {
        viewModel.apply {
            currentUserLiveData.observeEvent(viewLifecycleOwner) { firebaseUser ->
                if (firebaseUser != mainActivityViewModel.getCurrentUser()) {
                    mainActivityViewModel.setCurrentUser(firebaseUser)
                }
            }
        }
    }

    open fun start() { /* empty by default */ }

    protected fun showExitDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.ExitAlertDialogTheme)
            .setTitle(getString(R.string.menu_exit))
            .setMessage(getString(R.string.exit_accept_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ -> exitProcess(0) }
            .setNeutralButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    protected fun showSignOutDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.ExitAlertDialogTheme)
            .setTitle(getString(R.string.sign_out))
            .setMessage(getString(R.string.sign_out_accept_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ -> viewModel.signOut(requireContext()) }
            .setNeutralButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}