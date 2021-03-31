package tk.lorddarthart.githubuserfinder.view.main.search

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import tk.lorddarthart.githubuserfinder.domain.local.model.UserItem
import tk.lorddarthart.githubuserfinder.domain.repository.search.SearchRepository

class SearchViewModel(private val searchRepository: SearchRepository) : ViewModel() {
    val searchString: String?
        get() { return _searchStringLiveData.value }

    val loading = ObservableBoolean(false)
    val tryAgainVisible = ObservableBoolean(false)
    val searchBarOpened = ObservableBoolean(false)
    val totalCount = ObservableInt()
    val usersList = ObservableField<List<UserItem>>(listOf())

    private val _userListLiveData = MutableLiveData<List<UserItem>>()
    val userListLiveData: LiveData<List<UserItem>>
        get() = _userListLiveData

    private val _searchStringLiveData = MutableLiveData<String?>()
    val searchStringLiveData: LiveData<String?>
        get() = _searchStringLiveData

    val page = ObservableInt(1)

    private val _errorLiveData = MutableLiveData<String?>()
    val errorLiveData: LiveData<String?>
        get() = _errorLiveData

    private val _currentUserLiveData = MutableLiveData<FirebaseUser?>()
    val currentUserLiveData: LiveData<FirebaseUser?>
        get() = _currentUserLiveData

    var beginNetworkRequest = false

    init {
        _searchStringLiveData.value = ""
        _currentUserLiveData.value = FirebaseAuth.getInstance().currentUser
        _userListLiveData.value = usersList.get()

    }

    fun fetchData() {
        tryAgainVisible.set(false)
        if (!loading.get()) loading.set(true)
        viewModelScope.launch {
            searchRepository.getUser(_searchStringLiveData.value, page.get(), 30)
                .catch {
                    loading.set(false)
                    tryAgainVisible.set(true)
                    _errorLiveData.value = it.message
                }
                .collect {
                    if (it.first != null && totalCount.get() != it.first) {
                        totalCount.set(it.first!!)
                    }
                    if (it.second?.isEmpty() == true) {
                        loading.set(false)
                    } else {
                        val newList = usersList.get()?.toMutableList()
                        it.second?.let {
                            newList?.addAll(it)
                        }
                        usersList.set(newList)
                        _userListLiveData.value = usersList.get()
                    }
                }
        }
    }

    fun setSearchString(searchString: String?) {
        _searchStringLiveData.value = searchString
    }

    fun setCurrentPage(page: Int) {
        if (page == 1 && usersList.get()?.isEmpty() == false) { usersList.set(listOf()); _userListLiveData.value = usersList.get() }
        if (page > 0) this.page.set(page)
    }

    fun setErrorMessageToNull() {
        _errorLiveData.value = null
    }

    fun getSignInResults(context: Context): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun getCurrentlySignedInUserScope(context: Context): GoogleSignInClient? {
        return GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
    }

    fun signOut(context: Context) {
        FirebaseAuth.getInstance().signOut()

        getCurrentlySignedInUserScope(context)?.signOut()?.addOnCompleteListener {
            _currentUserLiveData.value = null
        }
    }
}