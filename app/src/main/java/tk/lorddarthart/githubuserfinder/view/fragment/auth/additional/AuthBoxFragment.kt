package tk.lorddarthart.githubuserfinder.view.fragment.auth.additional

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import tk.lorddarthart.githubuserfinder.R
import tk.lorddarthart.githubuserfinder.application.App
import tk.lorddarthart.githubuserfinder.view.base.BaseFragment
import tk.lorddarthart.githubuserfinder.databinding.ItemAuthBoxBinding
import tk.lorddarthart.githubuserfinder.util.constants.IntConstants.GOOGLE_SIGN_IN_CODE
import tk.lorddarthart.githubuserfinder.util.logs.Loggable
import tk.lorddarthart.githubuserfinder.util.logs.logDebug

class AuthBoxFragment : BaseFragment(), Loggable {
    private lateinit var authBoxBinding: ItemAuthBoxBinding
    private lateinit var googleSignInOptons: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    private val authBoxViewModel: AuthBoxViewModel by lazy {
        ViewModelProvider(
            this
        )[AuthBoxViewModel::class.java]
    }

    private val currentSignInObserver = Observer<SignInVariants> { currentSignIn ->
        when (currentSignIn) {
            SignInVariants.GOOGLE, SignInVariants.FACEBOOK -> {
                signIn()
            }
            else -> {
                // do nothing
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authBoxBinding = ItemAuthBoxBinding.inflate(
            inflater,
            container,
            false
        )

        initialization()

        return authBoxBinding.root
    }

    private fun initialization() {
        hangObservers()
        start()
        initListeners()
    }

    private fun hangObservers() {
        authBoxViewModel.currentSignInLiveData.observe(this, currentSignInObserver)
    }

    private fun start() {
        googleSignInOptons = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, googleSignInOptons)

        auth = FirebaseAuth.getInstance()
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        when (authBoxViewModel.getCurrentSignIn()) {
            SignInVariants.GOOGLE -> {
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN_CODE)
            }
            SignInVariants.FACEBOOK -> {
                // do something
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun initListeners() {
        authBoxBinding.authBoxGoogle.setOnClickListener {
            authBoxViewModel.setCurrentSignIn(SignInVariants.GOOGLE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Log.e(this::class.java.simpleName, "Error", e)
                Snackbar.make(
                    activity.findViewById<View>(android.R.id.content),
                    "Auth error: " + e.cause + ", " + e.message!!,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        logDebug { "firebaseAuthWithGoogle:" + acct.id!! }

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    logDebug { "signInWithCredential:success" }
                    App.user = auth.currentUser
                    activity.mainActivityViewModel.setCurrentUser(App.user)
                } else {
                    // If sign in fails, display a message to the user.
                    logDebug { "signInWithCredential:failure" }
                    Snackbar.make(
                        activity.findViewById(android.R.id.content),
                        "Authentication Failed.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                // ...
            }
    }
}