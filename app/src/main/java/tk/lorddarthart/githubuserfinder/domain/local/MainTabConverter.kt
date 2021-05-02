package tk.lorddarthart.githubuserfinder.domain.local

import tk.lorddarthart.githubuserfinder.R

class MainTabConverter {
    fun getIdByPosition(position: Int) = when (position) {
        0 -> R.id.search_fragment
        1 -> R.id.favourite_fragment
        2 -> R.id.profile_fragment
        else -> throw NotImplementedError("Unknown bottomnav id for page: $position")
    }

    fun getPositionById(itemId: Int): Int = when (itemId) {
        R.id.search_fragment -> 0
        R.id.favourite_fragment -> 1
        R.id.profile_fragment -> 2
        else -> throw NotImplementedError("Unknown bottomnav page for id: $itemId")
    }
}