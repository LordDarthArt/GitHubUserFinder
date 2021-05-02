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
import tk.lorddarthart.githubuserfinder.common.helper.Event
import tk.lorddarthart.githubuserfinder.common.helper.SingleLiveEvent
import tk.lorddarthart.githubuserfinder.domain.local.model.UserItem
import tk.lorddarthart.githubuserfinder.domain.repository.search.SearchRepository
import tk.lorddarthart.githubuserfinder.view.base.BaseViewModel

class SearchViewModel(private val searchRepository: SearchRepository) : BaseViewModel() {
    val searchString = ObservableField("")
    val loading = ObservableBoolean(false)
    val tryAgainVisible = ObservableBoolean(false)
    val searchBarOpened = ObservableBoolean(false)
    val totalCount = ObservableInt()
    val usersList = ObservableField<List<UserItem>>(listOf())

    private val _userListLiveData = MutableLiveData<Event<List<UserItem>>>()
    val userListLiveData: LiveData<Event<List<UserItem>>>
        get() = _userListLiveData

    private val _searchStringLiveData = MutableLiveData<Event<String?>>()
    val searchStringLiveData: LiveData<Event<String?>>
        get() = _searchStringLiveData

    val page = ObservableInt(1)

    private val _errorLiveData = MutableLiveData<Event<String?>>()
    val errorLiveData: LiveData<Event<String?>>
        get() = _errorLiveData

    var beginNetworkRequest = false

    init {
        _currentUserLiveData.value = Event(FirebaseAuth.getInstance().currentUser)
        usersList.get()?.let { _userListLiveData.value = Event(it) }
    }

    fun fetchData() {
        tryAgainVisible.set(false)
        if (!loading.get()) loading.set(true)
        viewModelScope.launch {
            searchRepository.getUser(searchString.get(), page.get(), 30)
                .catch {
                    loading.set(false)
                    tryAgainVisible.set(true)
                    _errorLiveData.value = Event(it.message)
                }
                .collect {
                    if (it.first != null && totalCount.get() != it.first) {
                        totalCount.set(it.first!!)
                    }
                    if (it.second?.isEmpty() == true) {
                        loading.set(false)
                    } else {
                        val newList = usersList.get()?.toMutableList()
                        it.second?.let { newList?.addAll(it) }
                        usersList.set(newList)
                        usersList.get()?.let { _userListLiveData.value = Event(it) }
                    }
                }
        }
    }

    fun setSearchString(searchString: String?) {
        if (this.searchString.get() != searchString) {
            this.searchString.set(searchString)
            _searchStringLiveData.value = Event(this.searchString.get())
        }
    }

    fun setCurrentPage(page: Int) {
        if (page == 1 && usersList.get()?.isEmpty() == false) {
            usersList.set(listOf())
        }

        if (page > 0) this.page.set(page)
    }

    fun getSignInResults(context: Context): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun notifyUsersList() {
        usersList.get()?.let { _userListLiveData.value = Event(it) }
    }

    fun setNoResults() {
        usersList.set(listOf())
        usersList.get()?.let { _userListLiveData.value = Event(it) }
    }
}