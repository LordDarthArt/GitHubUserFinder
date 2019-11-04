package tk.lorddarthart.githubuserfinder.application.view.fragment.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import tk.lorddarthart.githubuserfinder.application.model.SignedInGoogleUser
import tk.lorddarthart.githubuserfinder.application.model.User
import tk.lorddarthart.githubuserfinder.application.view.base.BaseFragment
import tk.lorddarthart.githubuserfinder.application.view.fragment.main.adapter.SearchAdapter
import tk.lorddarthart.githubuserfinder.databinding.FragmentMainBinding
import tk.lorddarthart.githubuserfinder.util.helper.IOnBackPressed
import tk.lorddarthart.githubuserfinder.util.helper.UtilFunctions.isDisplayedOnScreen
import tk.lorddarthart.githubuserfinder.util.helper.setVisible


class MainFragment : BaseFragment(), IOnBackPressed,
    NavigationView.OnNavigationItemSelectedListener {
    private lateinit var mainBinding: FragmentMainBinding
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(
            this
        )[MainViewModel::class.java]
    }

    private val searchStringObserver = Observer<String> { searchString ->
        if (!searchString.isNullOrBlank()) {
            with(mainViewModel) {
                clearUserList()
                setCurrentPage(1)
                showLoadingHideOthers()
            }
            coroutineScope.launch { request() }
        }
    }

    private val errorDataObserver = Observer<String?> { errorMessage ->
        errorMessage?.let { message ->
            activity.runOnUiThread {
                Snackbar.make(mainBinding.root, message, Snackbar.LENGTH_LONG).show()
            }
            mainViewModel.setErrorMessageToNull()
        }
    }

    private val userListObserver = Observer<MutableList<User?>> {
        if (mainBinding.fragmentMainListOfUsersFound.adapter?.itemCount!! <= 0) {
            mainViewModel.showNoResultsHideOthers()
        } else {
            mainViewModel.showLoadingHideOthers()
        }
        mainViewModel.getCurrentPage()?.plus(1)?.let {
            activity.runOnUiThread(Runnable {
                mainViewModel.setCurrentPage(it)
            })
        }
        mainBinding.fragmentMainListOfUsersFound.adapter?.notifyDataSetChanged()
    }

    private val displayPagingObserver = Observer<Boolean> { show ->
        mainBinding.fragmentMainLoadingNextPage.setVisible(show)
    }

    private val displayNoResultsObserver = Observer<Boolean> { show ->
        mainBinding.fragmentMainNoResultsText.setVisible(show)
    }

    private val displayTryAgainObserver = Observer<Boolean> { show ->
        mainBinding.fragmentMainButtonTryAgain.setVisible(show)
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
    ): View? {
        mainBinding = FragmentMainBinding.inflate(
            inflater,
            container,
            false
        )

        initialization()

        return mainBinding.root
    }

    private fun initialization() {
        hangObservers()
        start()
        initListeners()
        configure()
    }

    private fun configure() {
        // For RecyclerView's better performance
        with(mainBinding.fragmentMainListOfUsersFound) {
            setHasFixedSize(true)
            setItemViewCacheSize(20);
            isDrawingCacheEnabled = true;
            drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH;
        }
    }

    private fun hangObservers() {
        with(mainViewModel) {
            searchStringLiveData.observe(this@MainFragment, searchStringObserver)
            userListLiveData.observe(this@MainFragment, userListObserver)
            errorLiveData.observe(this@MainFragment, errorDataObserver)
            displayPagingLiveData.observe(this@MainFragment, displayPagingObserver)
            displayNoResultsLiveData.observe(this@MainFragment, displayNoResultsObserver)
            displayTryAgainLiveData.observe(this@MainFragment, displayTryAgainObserver)
            currentUserLiveData.observe(this@MainFragment, currentUserObserver)
        }
    }

    private fun start() {
        with(activity) {
            setSupportActionBar(mainBinding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        mainViewModel.begin()

        mainBinding.fragmentMainListOfUsersFound.layoutManager = LinearLayoutManager(activity)
        mainBinding.fragmentMainListOfUsersFound.adapter =
            SearchAdapter(mainViewModel.getUserList())

        val googleSignInResults = mainViewModel.getSignInResults()

        googleSignInResults?.let { user ->
            with(SignedInGoogleUser) {
                personEmail = user.email
                personFamilyName = user.familyName
                personGivenName = user.givenName
                personId = user.id
                personName = user.displayName
                personPhoto = user.photoUrl
            }
        }

        with(mainBinding.navView.getHeaderView(0)) {
            val userAvatar = findViewById<CircleImageView>(R.id.user_avatar)
            val userGivenName = findViewById<TextView>(R.id.user_given_name)
            val userEmail = findViewById<TextView>(R.id.user_email)

            userGivenName.text = SignedInGoogleUser.personGivenName
            userEmail.text = SignedInGoogleUser.personEmail
            Picasso.get().load(SignedInGoogleUser.personPhoto)
                .placeholder(R.drawable.ic_account)
                .error(R.drawable.ic_account)
                .into(userAvatar)
        }

        val toggle = ActionBarDrawerToggle(
            activity,
            mainBinding.drawerLayout,
            mainBinding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        mainBinding.drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
    }

    private fun initListeners() {
        mainBinding.fragmentMainSearchField.addTextChangedListener(object : TextWatcher {
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

        mainBinding.fragmentMainListOfUsersFound.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (mainViewModel.call == null
                    && isDisplayedOnScreen(mainBinding.fragmentMainLoadingNextPage)
                    && newState == SCROLL_STATE_IDLE && !mainViewModel.beginNetworkRequest
                ) {
                    mainViewModel.beginNetworkRequest = true
                    coroutineScope.launch { request() }
                }
            }
        })

        mainBinding.fragmentMainButtonTryAgain.setOnClickListener {
            mainViewModel.beginNetworkRequest = true
            coroutineScope.launch { request() }
        }

        mainBinding.navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sign_out -> {
                showSignOutDialog()
            }
            R.id.menu_exit -> {
                showExitDialog()
            }
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