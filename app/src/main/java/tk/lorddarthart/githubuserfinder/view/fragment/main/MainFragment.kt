package tk.lorddarthart.githubuserfinder.view.fragment.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.*
import tk.lorddarthart.githubuserfinder.R
import tk.lorddarthart.githubuserfinder.domain.local.model.Session
import tk.lorddarthart.githubuserfinder.view.base.BaseFragment
import tk.lorddarthart.githubuserfinder.view.fragment.main.adapter.SearchAdapter
import tk.lorddarthart.githubuserfinder.databinding.FragmentMainBinding
import tk.lorddarthart.githubuserfinder.common.helper.IOnBackPressed
import tk.lorddarthart.githubuserfinder.common.helper.isDisplayedOnScreen
import tk.lorddarthart.githubuserfinder.common.helper.setVisible

class MainFragment : BaseFragment(), IOnBackPressed, NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: FragmentMainBinding

    private val mainViewModel: MainViewModel by viewModels()
    private val searchAdapter: SearchAdapter by lazy { SearchAdapter().apply { setHasStableIds(true) } }

    private val displayPagingObserver = Observer<Boolean> { show ->
        binding.fragmentMainLoadingNextPage.setVisible(show)
    }

    private val displayNoResultsObserver = Observer<Boolean> { show ->
        binding.fragmentMainNoResultsText.setVisible(show)
    }

    private val displayTryAgainObserver = Observer<Boolean> { show ->
        binding.fragmentMainButtonTryAgain.setVisible(show)
    }

    private val currentUserObserver = Observer<FirebaseUser> { firebaseUser ->
        if (firebaseUser != activity.mainActivityViewModel.getCurrentUser()) {
            activity.mainActivityViewModel.setCurrentUser(firebaseUser)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

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
        // For RecyclerView's better performance
        binding.fragmentMainListOfUsersFound.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(20);
            isDrawingCacheEnabled = true;
            drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH;
        }
    }

    private fun hangObservers() {
        with(mainViewModel) {
            searchStringLiveData.observe(viewLifecycleOwner) { searchString ->
                if (!searchString.isNullOrBlank() && !mainViewModel.beginNetworkRequest) {
                    mainViewModel.beginNetworkRequest = true
                    mainViewModel.apply { clearUserList(); setCurrentPage(1); showLoadingHideOthers() }
                    lifecycleScope.launch { request() }
                }
            }

            userListLiveData.observe(viewLifecycleOwner) {
                if (binding.fragmentMainListOfUsersFound.adapter?.itemCount!! <= 0) {
                    mainViewModel.showNoResultsHideOthers()
                } else {
                    mainViewModel.showLoadingHideOthers()
                }
                mainViewModel.getCurrentPage()?.plus(1)?.let {
                    mainViewModel.setCurrentPage(it)
                }
                searchAdapter.submitList(it.toList())
            }

            errorLiveData.observe(viewLifecycleOwner) { errorMessage ->
                errorMessage?.let { message ->
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                    mainViewModel.setErrorMessageToNull()
                }
            }

            displayPagingLiveData.observe(viewLifecycleOwner) { displayPagingObserver }
            displayNoResultsLiveData.observe(viewLifecycleOwner) { displayNoResultsObserver }
            displayTryAgainLiveData.observe(viewLifecycleOwner) { displayTryAgainObserver }
            currentUserLiveData.observe(viewLifecycleOwner) { currentUserObserver }
        }
    }

    private fun start() {
        with(activity) {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        mainViewModel.begin()

        binding.fragmentMainListOfUsersFound.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = searchAdapter

            itemAnimator = null
        }

        val googleSignInResults = mainViewModel.getSignInResults()

        googleSignInResults?.let { user ->
            with(Session) {
                personEmail = user.email
                personFamilyName = user.familyName
                personGivenName = user.givenName
                personId = user.id
                personName = user.displayName
                personPhoto = user.photoUrl
            }
        }

        with(binding.navView.getHeaderView(0)) {
            val userAvatar = findViewById<CircleImageView>(R.id.user_avatar)
            val userGivenName = findViewById<TextView>(R.id.user_given_name)
            val userEmail = findViewById<TextView>(R.id.user_email)

            userGivenName.text = Session.personGivenName
            userEmail.text = Session.personEmail
            Picasso.get().load(Session.personPhoto)
                .placeholder(R.drawable.ic_account)
                .error(R.drawable.ic_account)
                .into(userAvatar)
        }

        val toggle = ActionBarDrawerToggle(
            activity,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
    }

    private fun initListeners() {
        binding.fragmentMainSearchField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // do nothing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mainViewModel.setSearchString(s.toString())
            }
        })

        binding.fragmentMainListOfUsersFound.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (mainViewModel.call == null
                    && binding.fragmentMainLoadingNextPage.isDisplayedOnScreen()
                    && newState == SCROLL_STATE_IDLE && !mainViewModel.beginNetworkRequest) {
                    mainViewModel.beginNetworkRequest = true
                    coroutineScope.launch { request() }
                }
            }
        })

        binding.fragmentMainButtonTryAgain.setOnClickListener {
            mainViewModel.beginNetworkRequest = true
            coroutineScope.launch { request() }
        }

        binding.navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sign_out -> { showSignOutDialog() }
            R.id.menu_exit -> { showExitDialog() }
        }
        return true
    }

    private fun showExitDialog() {
        MaterialAlertDialogBuilder(activity)
            .setTitle(getString(R.string.menu_exit))
            .setMessage(getString(R.string.exit_accept_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                activity.finishAffinity()
            }
            .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showSignOutDialog() {
        MaterialAlertDialogBuilder(activity)
            .setTitle(getString(R.string.sign_out))
            .setMessage(getString(R.string.sign_out_accept_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                mainViewModel.signOutOfGoogle()
            }
            .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private suspend fun request() {
        delay(600)
        mainViewModel.fetchData()
    }

    override fun onBackPressed() {
        showExitDialog()
    }
}