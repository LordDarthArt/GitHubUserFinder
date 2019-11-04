package tk.lorddarthart.githubuserfinder.application.view.fragment.main.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tk.lorddarthart.githubuserfinder.R

class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val singleUserProfileImage: ImageView = itemView.findViewById(R.id.single_user_profile_image)
    val singleUserNickname: TextView = itemView.findViewById(R.id.single_user_nickname)
    val singleUserId: TextView = itemView.findViewById(R.id.single_user_id)
    val singleUserIsAdmin: TextView = itemView.findViewById(R.id.single_user_is_admin)
    val singleUserScore: TextView = itemView.findViewById(R.id.single_user_score)
}