package tk.lorddarthart.githubuserfinder.view.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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

    private val searchViewModelImpl: SearchViewModel by activityScopedFragmentViewModel()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val searchAdapter: SearchAdapter by lazy { SearchAdapter().apply { setHasStableIds(true) } }
    private val session: Session by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

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
        searchViewModelImpl.apply {
            searchStringLiveData.observe(viewLifecycleOwner) { searchString ->
                if (!searchString.isNullOrBlank() && !searchViewModelImpl.beginNetworkRequest) {
                    searchViewModelImpl.beginNetworkRequest = true
                    searchViewModelImpl.apply { clearUserList(); setCurrentPage(1); showLoadingHideOthers() }
                    lifecycleScope.launch { request() }
                }
            }

            userListLiveData.observe(viewLifecycleOwner) {
                if ((binding.foundUsersList.adapter?.itemCount ?: 0) <= 0) {
                    searchViewModelImpl.showNoResultsHideOthers()
                } else {
                    searchViewModelImpl.showLoadingHideOthers()
                }
                searchViewModelImpl.currentPage?.plus(1)?.let { nextPage -> searchViewModelImpl.setCurrentPage(nextPage) }
                searchAdapter.submitList(it.toList())
            }

            errorLiveData.observe(viewLifecycleOwner) { errorMessage ->
                errorMessage?.let { message ->
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                    searchViewModelImpl.setErrorMessageToNull()
                }
            }

            displayPagingLiveData.observe(viewLifecycleOwner) { show ->
                binding.loadingNextPage.setVisible(show)
            }

            displayNoResultsLiveData.observe(viewLifecycleOwner) { show ->
                binding.foundNone.setVisible(show)
            }

            displayTryAgainLiveData.observe(viewLifecycleOwner) { show ->
                binding.tryAgain.setVisible(show)
            }

            currentUserLiveData.observe(viewLifecycleOwner) { firebaseUser ->
                if (firebaseUser != mainActivityViewModel.getCurrentUser()) {
                    mainActivityViewModel.setCurrentUser(firebaseUser)
                }
            }
        }
    }

    private fun start() {
//        with(activity) {
//            setSupportActionBar(binding.toolbar)
//            supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        }

        searchViewModelImpl.begin()

        binding.foundUsersList.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = searchAdapter

            itemAnimator = null
        }

        val googleSignInResults = searchViewModelImpl.getSignInResults(requireContext())

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

//        with(binding.navView.getHeaderView(0)) {
//            val userAvatar = findViewById<CircleImageView>(R.id.user_avatar)
//            val userGivenName = findViewById<TextView>(R.id.user_given_name)
//            val userEmail = findViewById<TextView>(R.id.user_email)
//
//            userGivenName.text = session.personGivenName
//            userEmail.text = session.personEmail
//            Glide.with(requireContext()).load(session.personPhoto)
//                .placeholder(R.drawable.ic_account)
//                .error(R.drawable.ic_account)
//                .into(userAvatar)
//        }

//        val toggle = ActionBarDrawerToggle(
//            activity,
//            binding.drawerLayout,
//            binding.toolbar,
//            R.string.navigation_drawer_open,
//            R.string.navigation_drawer_close
//        )
//        binding.drawerLayout.addDrawerListener(toggle)
//        toggle.isDrawerIndicatorEnabled = true
//        toggle.syncState()
    }

    private fun initListeners() {
        binding.searchField.doAfterTextChanged { text -> searchViewModelImpl.setSearchString(text.toString()) }

        binding.foundUsersList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (binding.loadingNextPage.isDisplayedOnScreen() && newState == SCROLL_STATE_IDLE && !searchViewModelImpl.beginNetworkRequest) {
                    searchViewModelImpl.beginNetworkRequest = true
                    lifecycleScope.launch { request() }
                }
            }
        })

        binding.tryAgain.setOnClickListener {
            searchViewModelImpl.beginNetworkRequest = true
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
            .setPositiveButton(getString(R.string.yes)) { _, _ -> searchViewModelImpl.signOut(requireContext()) }
            .setNeutralButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private suspend fun request() {
        delay(600)
        searchViewModelImpl.fetchData()
    }

    override fun onBackPressed() {
        showExitDialog()
    }
}