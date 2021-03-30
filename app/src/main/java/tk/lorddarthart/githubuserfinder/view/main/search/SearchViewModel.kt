package tk.lorddarthart.githubuserfinder.view.main.search

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import retrofit2.Call
import tk.lorddarthart.githubuserfinder.domain.local.model.UserItem
import tk.lorddarthart.githubuserfinder.domain.remote.UserListObject

class SearchViewModel : ViewModel() {
    private val _userListLiveData = MutableLiveData<MutableList<UserItem?>>()
    val userListLiveData: LiveData<MutableList<UserItem?>>
        get() = _userListLiveData

    private val _searchStringLiveData = MutableLiveData<String?>()
    val searchStringLiveData: LiveData<String?>
        get() = _searchStringLiveData

    private val _currentPageLiveData = MutableLiveData<Int>()
    val currentPageLiveData: LiveData<Int>
        get() = _currentPageLiveData

    private val _errorLiveData = MutableLiveData<String?>()
    val errorLiveData: LiveData<String?>
        get() = _errorLiveData

    private val _displayPagingLiveData = MutableLiveData<Boolean>()
    val displayPagingLiveData: LiveData<Boolean>
        get() = _displayPagingLiveData

    private val _displayNoResultsLiveData = MutableLiveData<Boolean>()
    val displayNoResultsLiveData: LiveData<Boolean>
        get() = _displayNoResultsLiveData

    private val _displayTryAgainLiveData = MutableLiveData<Boolean>()
    val displayTryAgainLiveData: LiveData<Boolean>
        get() = _displayTryAgainLiveData

    private val _currentUserLiveData = MutableLiveData<FirebaseUser?>()
    val currentUserLiveData: LiveData<FirebaseUser?>
        get() = _currentUserLiveData

    var beginNetworkRequest = false
    val currentPage: Int?
        get() { return _currentPageLiveData.value }

    var call: Call<UserListObject>? = null

    init {
        _searchStringLiveData.value = ""
        _displayPagingLiveData.value = false
        _displayTryAgainLiveData.value = false
        _displayNoResultsLiveData.value = true
        _currentUserLiveData.value = FirebaseAuth.getInstance().currentUser
    }

    fun begin() {
        if (_currentPageLiveData.value == null) {
            _currentPageLiveData.value = 1
        } else {
            _currentPageLiveData.value = _currentPageLiveData.value
        }
        if (_userListLiveData.value == null) {
            _userListLiveData.value = mutableListOf()
        } else {
            _userListLiveData.value = _userListLiveData.value
        }
    }

    fun fetchData() {
//        call = HttpServiceHelper.instance?.jsonApi
//            ?.getUserByName(_searchStringLiveData.value!!, _currentPageLiveData.value!!, 30)
//
//        call?.enqueue(object : Callback<UserListObject> {
//            override fun onResponse(call: Call<UserListObject>, response: Response<UserListObject>) {
//                val users = response.body()
//                if (users?.users != null && users.users!!.isNotEmpty()) {
//                    (_userListLiveData.value as MutableList).addAll(users.users!!)
//                    _userListLiveData.value = _userListLiveData.value
//                    if (_userListLiveData.value?.size!! < 10) {
//                        showNothingHideEverything()
//                    }
//                } else {
//                    when (response.code()) {
//                        200 -> {
//                            _displayPagingLiveData.value = false
//                            if (_userListLiveData.value?.size!! > 0) {
//                                showNothingHideEverything()
//                            } else {
//                                showNoResultsHideOthers()
//                            }
//                        }
//                        else -> {
//                            if (response.errorBody()?.string() != null && !response.errorBody()?.string()?.isBlank()!!) {
//                                _errorLiveData.value =
//                                    "FATAL: Response error: ${response.code()}, ${JSONObject(
//                                        response.errorBody()?.string()!!
//                                    ).get("message")}"
//                            } else {
//                                _errorLiveData.value = "FATAL: Response error: ${response.code()}"
//                            }
//                            if (_userListLiveData.value?.size!! > 0) {
//                                showTryAgainHideOthers()
//                            } else {
//                                showNoResultsHideOthers()
//                            }
//                        }
//                    }
//                }
//                beginNetworkRequest = false
//                this@SearchViewModelImpl.call = null
//            }
//
//            override fun onFailure(call: Call<UserListObject>, t: Throwable) {
//                call.cancel()
//                _errorLiveData.value = "FATAL: Request error: ${t.message}"
//                beginNetworkRequest = false
//            }
//        })
    }

    fun setSearchString(searchString: String?) {
        _searchStringLiveData.value = searchString
    }

    fun getUserList(): List<UserItem?>? {
        return _userListLiveData.value
    }

    fun setCurrentPage(page: Int) {
        if (page > 0) {
            _currentPageLiveData.value = page
        }
    }

    fun clearUserList() {
        _userListLiveData.value?.clear()
    }

    fun setDisplayPaging(isVisible: Boolean) {
        _displayPagingLiveData.value = isVisible
    }

    fun setDisplayNoResults(isVisible: Boolean) {
        _displayNoResultsLiveData.value = isVisible
    }

    fun setDisplayTryAgain(isVisible: Boolean) {
        _displayTryAgainLiveData.value = isVisible
    }

    fun setErrorMessageToNull() {
        _errorLiveData.value = null
    }

    fun getSignInResults(context: Context): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun showLoadingHideOthers() {
        _displayTryAgainLiveData.value = false
        _displayPagingLiveData.value = true
        _displayNoResultsLiveData.value = false
    }

    fun showNoResultsHideOthers() {
        _displayTryAgainLiveData.value = false
        _displayPagingLiveData.value = false
        _displayNoResultsLiveData.value = true
    }

    fun showTryAgainHideOthers() {
        _displayTryAgainLiveData.value = true
        _displayPagingLiveData.value = false
        _displayNoResultsLiveData.value = false
    }

    fun showNothingHideEverything() {
        _displayTryAgainLiveData.value = false
        _displayPagingLiveData.value = false
        _displayNoResultsLiveData.value = false
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