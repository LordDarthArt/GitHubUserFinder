package tk.lorddarthart.githubuserfinder.application.view.fragment.auth.additional

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import tk.lorddarthart.githubuserfinder.application.view.base.BaseViewModel

class AuthBoxViewModel : BaseViewModel() {

    private var _currentSignInLiveData = MutableLiveData<SignInVariants>()
    val currentSignInLiveData: LiveData<SignInVariants>
        get() = _currentSignInLiveData

    fun begin() {
        _currentSignInLiveData.value = SignInVariants.NONE
    }

    fun setCurrentSignIn(signIn: SignInVariants) {
        _currentSignInLiveData.value = signIn
    }

    fun getCurrentSignIn(): SignInVariants? {
        return _currentSignInLiveData.value
    }
}