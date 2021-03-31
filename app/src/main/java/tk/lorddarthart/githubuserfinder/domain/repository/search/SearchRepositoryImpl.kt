package tk.lorddarthart.githubuserfinder.domain.repository.search

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import tk.lorddarthart.githubuserfinder.common.network.GithubApi
import tk.lorddarthart.githubuserfinder.domain.local.model.UserItem

class SearchRepositoryImpl(private val githubApi: GithubApi): SearchRepository {
    override fun getUser(login: String?, page: Int?, pageSize: Int?): Flow<List<UserItem>> = githubApi.getUserByName(login, page, pageSize)
        .flowOn(Dispatchers.IO)
        .map { it.toUsersList() ?: listOf() }
        .flowOn(Dispatchers.Main)
}