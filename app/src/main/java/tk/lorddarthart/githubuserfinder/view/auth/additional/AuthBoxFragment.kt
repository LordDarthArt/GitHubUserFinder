package tk.lorddarthart.githubuserfinder.view.auth.additional

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import tk.lorddarthart.githubuserfinder.R
import tk.lorddarthart.githubuserfinder.application.App
import tk.lorddarthart.githubuserfinder.view.base.BaseFragment
import tk.lorddarthart.githubuserfinder.common.constants.IntConstants.GOOGLE_SIGN_IN_CODE
import tk.lorddarthart.githubuserfinder.common.logs.Loggable
import tk.lorddarthart.githubuserfinder.common.logs.logDebug
import tk.lorddarthart.githubuserfinder.databinding.FragmentAuthBoxBinding
import tk.lorddarthart.githubuserfinder.di.activityScopedFragmentViewModel
import tk.lorddarthart.githubuserfinder.di.fragmentViewModel
import tk.lorddarthart.githubuserfinder.view.activity.MainActivityViewModel

class AuthBoxFragment : BaseFragment(), Loggable, DIAware {
    override val di: DI by closestDI()
    override val viewModel: AuthBoxViewModel by fragmentViewModel()

    private lateinit var googleSignInOptons: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val callbackManager by lazy { CallbackManager.Factory.create() }

    private val googleAuthResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                account?.let { firebaseAuthWithGoogle(it) }
            } catch (e: ApiException) {
                Log.e(this::class.java.simpleName, "Error", e)
                Snackbar.make(requireActivity().findViewById<View>(android.R.id.content), "Auth error: ${e.cause}, ${e.message ?: "no error message"}", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) {
        binding = FragmentAuthBoxBinding.inflate(inflater, container, false)
        (binding as FragmentAuthBoxBinding).apply {
            viewModel = this@AuthBoxFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun initListeners() {
        (binding as FragmentAuthBoxBinding).apply {
            setOnSignInWithGoogleClick { this@AuthBoxFragment.viewModel.setCurrentSignIn(SignInVariants.Google) }
            setOnSignInWithGithubClick { this@AuthBoxFragment.viewModel.setCurrentSignIn(SignInVariants.GitHub) }
            setOnSignInWithFacebookClick { this@AuthBoxFragment.viewModel.setCurrentSignIn(SignInVariants.Facebook) }
        }
    }

    override fun hangObservers() {
        viewModel.apply {
            currentSignInLiveData.observe(viewLifecycleOwner) { currentSignIn ->
                when (currentSignIn) {
                    is SignInVariants.None -> { /* do nothing */ }
                    else -> { signIn() }
                }
            }
        }
    }

    override fun start() {
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
            SignInVariants.Google -> { googleAuthResultLauncher.launch(signInIntent) }
            SignInVariants.Facebook -> { loginWithFacebook() }
            SignInVariants.GitHub -> { /* do something */ }
            else -> { /* do nothing */ }
        }
    }

    private fun loginWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, setOf("email"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                result?.let {
                    firebaseAuthWithFacebook(it.accessToken)
                }
            }

            override fun onCancel() {
                Snackbar.make(requireActivity().findViewById<View>(android.R.id.content), "Facebook login cancelled", Snackbar.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException?) {
                Snackbar.make(requireActivity().findViewById<View>(android.R.id.content), "Facebook login failed: ${error.toString()}", Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
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

    private fun firebaseAuthWithFacebook(token: AccessToken) {
        logDebug { "firebaseAuthWithFacebook:${token.token}" }

        val credential = FacebookAuthProvider.getCredential(token.token)
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