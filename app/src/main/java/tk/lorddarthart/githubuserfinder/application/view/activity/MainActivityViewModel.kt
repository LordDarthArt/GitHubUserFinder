package tk.lorddarthart.githubuserfinder.application.view.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import tk.lorddarthart.githubuserfinder.application.view.base.BaseViewModel

class MainActivityViewModel : BaseViewModel() {
    private var _currentFragmentLiveData = MutableLiveData<CurrentFragment>()
    val currentFragmentLiveData: LiveData<CurrentFragment>
        get() = _currentFragmentLiveData

    private var _currentUserLiveData = MutableLiveData<FirebaseUser?>()
    val currentUserLiveData: LiveData<FirebaseUser?>
        get() = _currentUserLiveData

    fun setCurrentFragment(fragment: CurrentFragment) {
        _currentFragmentLiveData.value = fragment
    }

    fun setCurrentUser(user: FirebaseUser?) {
        _currentUserLiveData.value = user
    }

    fun getCurrentUser(): FirebaseUser? {
        return _currentUserLiveData.value
    }
}