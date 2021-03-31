package tk.lorddarthart.githubuserfinder.domain.remote

import com.google.gson.annotations.SerializedName
import tk.lorddarthart.githubuserfinder.domain.local.model.UserItem

// Common model for parsing answer to our direct github request (contains list of User models)
data class UserListObject(
    @SerializedName("total_count") val totalCount: Int? = null,
    @SerializedName("incomplete_results") var incompleteResults: Boolean? = null,
    @SerializedName("items") var users: List<UserObject>? = null
) {
    fun toUsersList() = if ((users?.size ?: 0) > 0) users?.map {
        UserItem(
            login = it.login,
            id = it.id,
            nodeId = it.nodeId,
            avatarUrl = it.avatarUrl,
            gravatarId = it.gravatarId,
            url = it.url,
            htmlUrl = it.htmlUrl,
            followersUrl = it.followersUrl,
            subscriptionsUrl = it.subscriptionsUrl,
            organizationsUrl = it.organizationsUrl,
            reposUrl = it.reposUrl,
            users = it.users,
            type = it.type,
            score = it.score
        )
    } else listOf()
}