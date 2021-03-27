package tk.lorddarthart.githubuserfinder.view.fragment.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import tk.lorddarthart.githubuserfinder.R
import tk.lorddarthart.githubuserfinder.application.App
import tk.lorddarthart.githubuserfinder.application.model.User
import tk.lorddarthart.githubuserfinder.databinding.ItemSingleUserBinding
import tk.lorddarthart.githubuserfinder.util.helper.SameCallback

class SearchAdapter : ListAdapter<User, SearchAdapter.SearchViewHolder>(SameCallback<User>()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding =
            ItemSingleUserBinding.inflate(LayoutInflater.from(App.instance), parent, false)
        return SearchViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        currentList[position]?.let {
            holder.bind(position)
        }
    }

    override fun getItemId(position: Int) = position.toLong()

    private fun animatedImgLoad(urlString: String?, holder: SearchViewHolder) {
        Picasso.get().load(urlString)
            .placeholder(R.drawable.ic_preload)
            .error(R.drawable.ic_account)
            .resize(40, 40)
            .centerCrop()
            .into(holder.binding.profileImage)
    }

    inner class SearchViewHolder(val binding: ItemSingleUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val user = currentList[position]
            binding.apply {
                nickname.text = user?.login
                userId.text = "${binding.root.context.getString(R.string.id)} ${user?.id}"
                isAdmin.text = "${binding.root.context.getString(R.string.type)} ${user?.type}"
                score.text = "${binding.root.context.getString(R.string.score)} ${user?.score}"
            }
            try {
                animatedImgLoad(user?.avatarUrl, this)
            } catch (e: Exception) {
                // todo handle
            }
        }
    }
}