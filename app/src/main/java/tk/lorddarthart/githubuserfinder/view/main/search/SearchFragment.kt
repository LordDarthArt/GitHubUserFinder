package tk.lorddarthart.githubuserfinder.view.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import tk.lorddarthart.githubuserfinder.R
import tk.lorddarthart.githubuserfinder.common.helper.IOnBackPressed
import tk.lorddarthart.githubuserfinder.common.helper.isDisplayedOnScreen
import tk.lorddarthart.githubuserfinder.common.helper.setVisible
import tk.lorddarthart.githubuserfinder.databinding.FragmentSearchBinding
import tk.lorddarthart.githubuserfinder.di.activityScopedFragmentViewModel
import tk.lorddarthart.githubuserfinder.domain.local.Session
import tk.lorddarthart.githubuserfinder.view.activity.MainActivityViewModel
import tk.lorddarthart.githubuserfinder.view.base.BaseFragment
import tk.lorddarthart.githubuserfinder.view.main.search.adapter.SearchAdapter
import kotlin.system.exitProcess

class SearchFragment : BaseFragment(), IOnBackPressed, NavigationView.OnNavigationItemSelectedListener, DIAware {
    override val di: DI by closestDI()

    private lateinit var binding: FragmentSearchBinding

    private val viewModel: SearchViewModel by activityScopedFragmentViewModel()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val searchAdapter: SearchAdapter by lazy { SearchAdapter().apply { setHasStableIds(true) } }
    private val session: Session by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.setOnTryAgainClick {
            lifecycleScope.launch { request() }
        }
        initialization()

        return binding.root
    }

    private fun initialization() {
        hangObservers()
        start()
        initListeners()
        configure()
    }

    private fun configure() {
        binding.foundUsersList.apply {
            setItemViewCacheSize(20)
            itemAnimator = null
            isDrawingCacheEnabled = true
            drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH;
        }
    }

    private fun hangObservers() {
        viewModel.apply {
            searchStringLiveData.observe(viewLifecycleOwner) { searchString ->
                restartSearch(searchString)
            }

            userListLiveData.observe(viewLifecycleOwner) {
                viewModel.page.get().plus(1).let { nextPage -> viewModel.setCurrentPage(nextPage) }
                searchAdapter.submitList(it.toList())
            }

            errorLiveData.observe(viewLifecycleOwner) { errorMessage ->
                errorMessage?.let { message ->
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                    viewModel.setErrorMessageToNull()
                }
            }

            currentUserLiveData.observe(viewLifecycleOwner) { firebaseUser ->
                if (firebaseUser != mainActivityViewModel.getCurrentUser()) {
                    mainActivityViewModel.setCurrentUser(firebaseUser)
                }
            }
        }
    }

    private fun restartSearch(searchString: String?) {
        if (!searchString.isNullOrBlank()) {
            viewModel.setCurrentPage(1)
            lifecycleScope.launch { request() }
        }
    }

    private fun start() {
        binding.foundUsersList.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = searchAdapter

            itemAnimator = null
        }

        val googleSignInResults = viewModel.getSignInResults(requireContext())

        googleSignInResults?.let { user ->
            session.apply {
                personEmail = user.email
                personFamilyName = user.familyName
                personGivenName = user.givenName
                personId = user.id
                personName = user.displayName
                personPhoto = user.photoUrl
            }
        }
    }

    private fun initListeners() {
        binding.searchField.doAfterTextChanged { text -> viewModel.setSearchString(text.toString()) }

        binding.foundUsersList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (binding.loadingNextPage.isDisplayedOnScreen() && newState == SCROLL_STATE_IDLE && !viewModel.beginNetworkRequest) {
                    lifecycleScope.launch { request() }
                }
            }
        })

        binding.tryAgain.setOnClickListener {
            lifecycleScope.launch { request() }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sign_out -> { showSignOutDialog() }
            R.id.menu_exit -> { showExitDialog() }
        }
        return true
    }

    private fun showExitDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.menu_exit))
            .setMessage(getString(R.string.exit_accept_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ -> exitProcess(0) }
            .setNeutralButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showSignOutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.sign_out))
            .setMessage(getString(R.string.sign_out_accept_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ -> viewModel.signOut(requireContext()) }
            .setNeutralButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun request() {
        viewModel.fetchData()
    }

    override fun onBackPressed() {
        showExitDialog()
    }
}