package tk.lorddarthart.githubuserfinder.view.main.search

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import okhttp3.internal.notify
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import tk.lorddarthart.githubuserfinder.R
import tk.lorddarthart.githubuserfinder.common.helper.*
import tk.lorddarthart.githubuserfinder.databinding.FragmentSearchBinding
import tk.lorddarthart.githubuserfinder.di.activityScopedFragmentViewModel
import tk.lorddarthart.githubuserfinder.domain.local.Session
import tk.lorddarthart.githubuserfinder.view.activity.MainActivityViewModel
import tk.lorddarthart.githubuserfinder.view.base.BaseFragment
import tk.lorddarthart.githubuserfinder.view.base.BaseTabFragment
import tk.lorddarthart.githubuserfinder.view.main.MainFragmentDirections
import tk.lorddarthart.githubuserfinder.view.main.search.adapter.SearchAdapter
import kotlin.random.Random
import kotlin.system.exitProcess

class SearchFragment : BaseTabFragment(), NavigationView.OnNavigationItemSelectedListener, DIAware {
    override val di: DI by closestDI()

    override val viewModel: SearchViewModel by activityScopedFragmentViewModel()
    private val searchAdapter: SearchAdapter by lazy { SearchAdapter { if (Random.nextBoolean()) parentNavController.navigate(MainFragmentDirections.actionGlobalToAuth()) else (findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToAuthFragment())) }.apply { setHasStableIds(true); stateRestorationPolicy = PREVENT_WHEN_EMPTY } }
    private val session: Session by instance()

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        (binding as FragmentSearchBinding).apply {
            this.viewModel = this@SearchFragment.viewModel
            lifecycleOwner = viewLifecycleOwner

            setOnTryAgainClick { lifecycleScope.launch { request() } }
            setOnSearchClick { this@SearchFragment.viewModel.searchBarOpened.set(true) }
            setOnCloseSearchClick { this@SearchFragment.viewModel.searchBarOpened.set(false) }

            searchField.doAfterTextChanged { this@SearchFragment.viewModel.setSearchString(it.toString()) }

            foundUsersList.apply {
                setItemViewCacheSize(20)
                itemAnimator = null
                isDrawingCacheEnabled = true
                drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
            }
        }
    }

    override fun hangObservers() {
        viewModel.apply {
            searchStringLiveData.observeEvent(viewLifecycleOwner) { searchString ->
                if (!viewModel.loading.get()) viewModel.loading.set(true)
                viewModel.setCurrentPage(1)
                searchAdapter.submitList(listOf())
                lifecycleScope.launch {
                    delay(1000)
                    if (searchString == viewModel.searchString.get()) {
                        restartSearch(searchString)
                    }
                }
            }

            userListLiveData.observeEvent(viewLifecycleOwner) {
                if ((binding as FragmentSearchBinding).foundUsersList.adapter != searchAdapter) {
                    (binding as FragmentSearchBinding).foundUsersList.adapter = searchAdapter
                }
                searchAdapter.submitList(it.toList())
                if (viewModel.page.get() == 1 && viewModel.totalCount.get() < 30) {
                    viewModel.loading.set(false)
                } else {
                    viewModel.setCurrentPage(viewModel.page.get().plus(1))
                }
            }

            errorLiveData.observeEvent(viewLifecycleOwner) { errorMessage ->
                errorMessage?.let { Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show() }
            }
        }
    }

    private fun restartSearch(searchString: String?) {
        if (!searchString.isNullOrBlank()) {
            viewModel.setCurrentPage(1)
            lifecycleScope.launch { request() }
        } else {
            viewModel.setNoResults()
        }
    }

    override fun start() {
        if (!viewModel.usersList.get().isNullOrEmpty()) {
            viewModel.notifyUsersList()
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

    override fun initListeners() {
        (binding as FragmentSearchBinding).apply {
            foundUsersList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (loadingNextPage.isDisplayedOnScreen() && newState == SCROLL_STATE_IDLE && !this@SearchFragment.viewModel.beginNetworkRequest) {
                        lifecycleScope.launch { request() }
                    }
                }
            })
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sign_out -> { showSignOutDialog() }
            R.id.menu_exit -> { showExitDialog() }
        }
        return true
    }

    private fun request() {
        viewModel.fetchData()
    }
}