package tk.lorddarthart.githubuserfinder.domain.repository.search

import kotlinx.coroutines.flow.Flow
import tk.lorddarthart.githubuserfinder.domain.local.model.UserItem

interface SearchRepository {
    fun getUser(login: String?, page: Int?, pageSize: Int?): Flow<List<UserItem>>
}