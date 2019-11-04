package tk.lorddarthart.githubuserfinder.application.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

// Common model for parsing answer to our direct github request (contains list of User models)
class UserList {
    @SerializedName("total_count")
    @Expose
    var totalCount: Int? = null

    @SerializedName("incomplete_results")
    @Expose
    var incompleteResults: Boolean? = null

    @SerializedName("items")
    @Expose
    var users: List<User>? = null
}