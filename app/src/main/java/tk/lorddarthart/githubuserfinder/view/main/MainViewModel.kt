package tk.lorddarthart.githubuserfinder.view.main

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import tk.lorddarthart.githubuserfinder.view.base.BaseViewModel

class MainViewModel : BaseViewModel() {
    val searchSelected = ObservableBoolean(true)
    val favouriteSelected = ObservableBoolean(false)
    val profileSelected = ObservableBoolean(false)

    val tabNavigationHistory = mutableListOf<Int>()

    private val _currentTabLiveData = MutableLiveData<Int?>()
    val currentTabLiveData: LiveData<Int?>
        get() = _currentTabLiveData

    fun setCurrentTab(itemId: Int) {
        if (_currentTabLiveData.value != itemId) {
            _currentTabLiveData.value = itemId
        }
        saveToHistory(itemId)
    }

    private fun saveToHistory(itemId: Int) {
        tabNavigationHistory.iterator().let { iterator ->
            iterator.forEach {
                if (it == itemId) {
                    iterator.remove()
                }
            }
        }
        tabNavigationHistory.add(itemId)
    }
}