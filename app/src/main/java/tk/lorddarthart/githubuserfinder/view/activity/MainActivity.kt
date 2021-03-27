package tk.lorddarthart.githubuserfinder.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import tk.lorddarthart.githubuserfinder.R
import tk.lorddarthart.githubuserfinder.application.App
import tk.lorddarthart.githubuserfinder.view.base.BaseActivity
import tk.lorddarthart.githubuserfinder.view.fragment.auth.AuthFragment
import tk.lorddarthart.githubuserfinder.view.fragment.main.MainFragment
import tk.lorddarthart.githubuserfinder.databinding.ActivityMainBinding
import tk.lorddarthart.githubuserfinder.util.helper.IOnBackPressed
import tk.lorddarthart.githubuserfinder.util.logs.Loggable
import tk.lorddarthart.githubuserfinder.util.logs.logDebug

class MainActivity : BaseActivity(), Loggable {
    private lateinit var mainActivityView: ActivityMainBinding

    val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityView = DataBindingUtil
            .setContentView(this, R.layout.activity_main)

        initialization()
    }

    private fun initialization() {
        hangObservers()
        start()
    }

    private fun start() {
        mainActivityViewModel.setCurrentUser(FirebaseAuth.getInstance().currentUser)
        mainActivityViewModel.getCurrentUser()?.let { firebaseUser ->
            App.user = firebaseUser
        }
    }

    private fun hangObservers() {
        mainActivityViewModel.currentScreenLiveData.observe(this) {  currentFragment ->
            // todo implement navcomponent
            when (currentFragment) {
                CurrentScreen.AuthScreen -> {
                    supportFragmentManager.beginTransaction()
                        .replace(mainActivityView.mainFragmentContainer.id, AuthFragment())
                        .commitAllowingStateLoss()
                }
                CurrentScreen.MainScreen -> {
                    supportFragmentManager.beginTransaction()
                        .replace(mainActivityView.mainFragmentContainer.id, MainFragment())
                        .commitAllowingStateLoss()
                }
                else -> {
                    logDebug { "Are you serious?!" }
                }
            }
        }

        mainActivityViewModel.currentUserLiveData.observe(this) { firebaseUser ->
            if (firebaseUser == null) {
                mainActivityViewModel.setCurrentFragment(CurrentScreen.AuthScreen)
            } else {
                mainActivityViewModel.setCurrentFragment(CurrentScreen.MainScreen)
            }
        }
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)

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
