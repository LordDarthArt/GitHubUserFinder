package tk.lorddarthart.githubuserfinder.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    private val _currentTabLiveData = MutableLiveData<Int?>()
    val currentTabLiveData: LiveData<Int?>
        get() = _currentTabLiveData

    fun setCurrentTab(itemId: Int) {
        if (_currentTabLiveData.value != itemId) {
            _currentTabLiveData.value = itemId
        }
    }
}