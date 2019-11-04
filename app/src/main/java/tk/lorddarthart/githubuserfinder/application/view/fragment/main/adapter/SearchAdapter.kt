package tk.lorddarthart.githubuserfinder.application.view.fragment.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import tk.lorddarthart.githubuserfinder.R
import tk.lorddarthart.githubuserfinder.application.App
import tk.lorddarthart.githubuserfinder.application.model.User

class SearchAdapter(
    private val userList: List<User?>?
) : RecyclerView.Adapter<SearchViewHolder>() {
    private lateinit var singleUserView: View
    private var singleUserViewHolder: SearchViewHolder? = null

    override fun getItemCount(): Int {
        userList?.size?.let {
            return it
        }
        return 0
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = userList?.get(position)
        with(holder) {
            singleUserNickname.text = user?.login
            singleUserId.text = "${App.instance.getString(R.string.id)} ${user?.id}"
            singleUserIsAdmin.text = "${App.instance.getString(R.string.type)} ${user?.type}"
            singleUserScore.text = "${App.instance.getString(R.string.score)} ${user?.score}"
        }
        try {
            animatedImgLoad(user?.avatarUrl, holder)
        } catch (e: Exception) {
            Toast
                .makeText(
                    App.instance,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        singleUserView = LayoutInflater.from(App.instance).inflate(
            R.layout.item_single_user,
            parent,
            false
        )

        singleUserViewHolder = SearchViewHolder(singleUserView)
        return singleUserViewHolder as SearchViewHolder
    }

    private fun animatedImgLoad(urlString: String?, holder: SearchViewHolder) {
        Picasso.get().load(urlString)
            .placeholder(R.drawable.ic_preload)
            .error(R.drawable.ic_account)
            .resize(40, 40)
            .centerCrop()
            .into(holder.singleUserProfileImage)
    }
}