package tk.lorddarthart.githubuserfinder.view.base

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import tk.lorddarthart.githubuserfinder.common.helper.Event

abstract class BaseViewModel: ViewModel() {
    protected val _currentUserLiveData = MutableLiveData<Event<FirebaseUser?>>()
    val currentUserLiveData: LiveData<Event<FirebaseUser?>>
        get() = _currentUserLiveData

    private fun getCurrentlySignedInUserScope(context: Context): GoogleSignInClient? {
        return GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
    }

    fun signOut(context: Context) {
        FirebaseAuth.getInstance().signOut()

        getCurrentlySignedInUserScope(context)?.signOut()?.addOnCompleteListener {
            _currentUserLiveData.value = Event(FirebaseAuth.getInstance().currentUser)
        }
    }
}
