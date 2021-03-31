package tk.lorddarthart.githubuserfinder.view.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import tk.lorddarthart.githubuserfinder.R
import tk.lorddarthart.githubuserfinder.view.base.BaseActivity
import tk.lorddarthart.githubuserfinder.databinding.ActivityMainBinding
import tk.lorddarthart.githubuserfinder.common.helper.IOnBackPressed
import tk.lorddarthart.githubuserfinder.common.logs.Loggable
import tk.lorddarthart.githubuserfinder.common.logs.logDebug
import tk.lorddarthart.githubuserfinder.di.activityViewModel
import tk.lorddarthart.githubuserfinder.view.main.MainFragmentDirections

class MainActivity : BaseActivity(), Loggable, DIAware {
    private lateinit var binding: ActivityMainBinding

    override val di: DI by closestDI()
    private val viewModel: MainActivityViewModel by activityViewModel()
    private val navController: NavController by lazy { findNavController(R.id.nav_host_fragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (savedInstanceState == null) {
            initialization()
        }
    }

    private fun initialization() {
        hangObservers()
        start()
    }

    private fun start() {
        viewModel.setCurrentUser(FirebaseAuth.getInstance().currentUser)
//        viewModel.getCurrentUser()?.let { firebaseUser -> App.user = firebaseUser }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.main_graph)

        val destination = if (viewModel.getCurrentUser() == null) R.id.auth_fragment else R.id.main_fragment
        navGraph.startDestination = destination
        navController.graph = navGraph
    }

    private fun hangObservers() {
        viewModel.currentScreenLiveData.observe(this) { currentScreen ->
            when (currentScreen) {
                CurrentScreen.AuthScreen -> { navController.navigate(MainFragmentDirections.actionGlobalToAuth()) }
                CurrentScreen.MainScreen -> { navController.navigate(MainFragmentDirections.actionGlobalToMain()) }
                else -> { logDebug { "Are you serious?!" } }
            }
        }

        viewModel.currentUserLiveData.observe(this) { firebaseUser ->
            if (firebaseUser == null) {
                viewModel.setCurrentFragment(CurrentScreen.AuthScreen)
            } else {
                viewModel.setCurrentFragment(CurrentScreen.MainScreen)
            }
        }
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)

        if (currentFragment is IOnBackPressed) {
            currentFragment.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        return true
    }
}
