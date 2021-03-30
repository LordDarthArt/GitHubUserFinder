package tk.lorddarthart.githubuserfinder.view.auth.additional

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import tk.lorddarthart.githubuserfinder.R
import tk.lorddarthart.githubuserfinder.application.App
import tk.lorddarthart.githubuserfinder.view.base.BaseFragment
import tk.lorddarthart.githubuserfinder.databinding.ItemAuthBoxBinding
import tk.lorddarthart.githubuserfinder.common.constants.IntConstants.GOOGLE_SIGN_IN_CODE
import tk.lorddarthart.githubuserfinder.common.logs.Loggable
import tk.lorddarthart.githubuserfinder.common.logs.logDebug
import tk.lorddarthart.githubuserfinder.di.activityScopedFragmentViewModel
import tk.lorddarthart.githubuserfinder.di.fragmentViewModel
import tk.lorddarthart.githubuserfinder.view.activity.MainActivityViewModel

class AuthBoxFragment : BaseFragment(), Loggable, DIAware {
    override val di: DI by closestDI()

    private lateinit var binding: ItemAuthBoxBinding
    private lateinit var googleSignInOptons: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    private val viewModel: AuthBoxViewModel by fragmentViewModel()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ItemAuthBoxBinding.inflate(inflater, container, false)

        initialization()

        return binding.root
    }

    private fun initialization() {
        hangObservers()
        start()
        initListeners()
    }

    private fun hangObservers() {
        viewModel.currentSignInLiveData.observe(viewLifecycleOwner) { currentSignIn ->
            when (currentSignIn) {
                is SignInVariants.Google, is SignInVariants.Facebook -> { signIn() }
                else -> { /* do nothing */ }
            }
        }
    }

    private fun start() {
        googleSignInOptons = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptons)

        auth = FirebaseAuth.getInstance()
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        when (viewModel.getCurrentSignIn()) {
            SignInVariants.Google -> { startActivityForResult(signInIntent, GOOGLE_SIGN_IN_CODE) }
            SignInVariants.Facebook -> { /* do something */ }
            else -> { /* do nothing */ }
        }
    }

    private fun initListeners() {
        binding.authBoxGoogle.setOnClickListener {
            viewModel.setCurrentSignIn(SignInVariants.Google)
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
                    requireActivity().findViewById<View>(android.R.id.content),
                    "Auth error: ${e.cause}, ${e.message ?: "no error message"}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        logDebug { "firebaseAuthWithGoogle:${acct.id}" }

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    logDebug { "signInWithCredential:success" }
                    mainActivityViewModel.setCurrentUser(auth.currentUser)
                } else {
                    // If sign in fails, display a message to the user.
                    logDebug { "signInWithCredential:failure" }
                    Snackbar.make(requireActivity().findViewById(android.R.id.content), "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                }
            }
    }
}