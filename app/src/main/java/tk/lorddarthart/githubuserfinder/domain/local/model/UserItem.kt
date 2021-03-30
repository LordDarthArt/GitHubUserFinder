package tk.lorddarthart.githubuserfinder.domain.local.model

data class UserItem(
    val login: String?,
    val id: Int?,
    val nodeId: String?,
    val avatarUrl: String?,
    val gravatarId: String?,
    val url: String?,
    val htmlUrl: String?,
    val followersUrl: String?,
    val subscriptionsUrl: String?,
    val organizationsUrl: String?,
    val reposUrl: String?,
    val users: String?,
    val type: String?,
    val score: Double?
)