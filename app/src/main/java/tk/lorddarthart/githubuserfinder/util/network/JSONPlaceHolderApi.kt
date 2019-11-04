package tk.lorddarthart.githubuserfinder.util.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import tk.lorddarthart.githubuserfinder.application.model.UserList
import tk.lorddarthart.githubuserfinder.util.constants.UrlConstants.SEARCH_PARAMETER_PAGE
import tk.lorddarthart.githubuserfinder.util.constants.UrlConstants.SEARCH_PARAMETER_PER_PAGE
import tk.lorddarthart.githubuserfinder.util.constants.UrlConstants.SEARCH_PARAMETER_Q
import tk.lorddarthart.githubuserfinder.util.constants.UrlConstants.SEARCH_USER_URL

interface JSONPlaceHolderApi {
    @GET(SEARCH_USER_URL)
    fun getUserByName(
        @Query(value = SEARCH_PARAMETER_Q, encoded = true) login: String,
        @Query(value = SEARCH_PARAMETER_PAGE, encoded = true) page: Int,
        @Query(value = SEARCH_PARAMETER_PER_PAGE, encoded = true) perPage: Int
    ): Call<UserList>

    @GET(SEARCH_USER_URL)
    fun getUserByRepos(
        @Query(value = SEARCH_PARAMETER_Q, encoded = true) reposCount: Int,
        @Query(value = SEARCH_PARAMETER_PAGE, encoded = true) page: Int,
        @Query(value = SEARCH_PARAMETER_PER_PAGE, encoded = true) perPage: Int
    ): Call<UserList>

    @GET(SEARCH_USER_URL)
    fun getUserByFollowers(
        @Query(value = SEARCH_PARAMETER_Q, encoded = true) followersCount: Int,
        @Query(value = SEARCH_PARAMETER_PAGE, encoded = true) page: Int,
        @Query(value = SEARCH_PARAMETER_PER_PAGE, encoded = true) perPage: Int
    ): Call<UserList>
}