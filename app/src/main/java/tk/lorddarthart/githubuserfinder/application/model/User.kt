package tk.lorddarthart.githubuserfinder.application.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

// User model with info we are receiving from server
class User {
    @SerializedName("login")
    @Expose
    var login: String? = null

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("node_id")
    @Expose
    var nodeId: String? = null

    @SerializedName("avatar_url")
    @Expose
    var avatarUrl: String? = null

    @SerializedName("gravatar_id")
    @Expose
    var gravatarId: String? = null

    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("html_url")
    @Expose
    var htmlUrl: String? = null

    @SerializedName("followers_url")
    @Expose
    var followersUrl: String? = null

    @SerializedName("subscriptions_url")
    @Expose
    var subscriptionsUrl: String? = null

    @SerializedName("organizations_url")
    @Expose
    var organizationsUrl: String? = null

    @SerializedName("repos_url")
    @Expose
    var reposUrl: String? = null

    @SerializedName("received_events_url")
    @Expose
    var users: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("score")
    @Expose
    var score: Double? = null
}