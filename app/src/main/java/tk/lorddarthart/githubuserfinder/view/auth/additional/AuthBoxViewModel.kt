package tk.lorddarthart.githubuserfinder.view.auth.additional

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tk.lorddarthart.githubuserfinder.view.base.BaseViewModel

class AuthBoxViewModel : BaseViewModel() {
    private var _currentSignInLiveData = MutableLiveData<SignInVariants>()
    val currentSignInLiveData: LiveData<SignInVariants>
        get() = _currentSignInLiveData

    init {
        _currentSignInLiveData.value = SignInVariants.None
    }

    fun setCurrentSignIn(signIn: SignInVariants) {
        _currentSignInLiveData.value = signIn
    }

    fun getCurrentSignIn() = _currentSignInLiveData.value
}