package tk.lorddarthart.githubuserfinder.domain.remote

import com.google.gson.annotations.SerializedName
import tk.lorddarthart.githubuserfinder.domain.local.model.UserItem

// Common model for parsing answer to our direct github request (contains list of User models)
data class UserListObject(
    @SerializedName("total_count") val totalCount: Int? = null,
    @SerializedName("incomplete_results") var incompleteResults: Boolean? = null,
    @SerializedName("items") var users: List<UserObject>? = null
)