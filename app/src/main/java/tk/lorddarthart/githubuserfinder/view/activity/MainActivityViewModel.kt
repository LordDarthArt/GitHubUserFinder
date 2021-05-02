package tk.lorddarthart.githubuserfinder.view.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class MainActivityViewModel : ViewModel() {
    private var _currentFragmentLiveData = MutableLiveData<CurrentScreen>()
    val currentScreenLiveData: LiveData<CurrentScreen>
        get() = _currentFragmentLiveData

    private var _currentUserLiveData = MutableLiveData<FirebaseUser?>()
    val currentUserLiveData: LiveData<FirebaseUser?>
        get() = _currentUserLiveData

    fun setCurrentFragment(screen: CurrentScreen) {
        _currentFragmentLiveData.value = screen
    }

    fun setCurrentUser(user: FirebaseUser?) {
        _currentUserLiveData.value = user
    }

    fun getCurrentUser(): FirebaseUser? {
        return _currentUserLiveData.value
    }
}