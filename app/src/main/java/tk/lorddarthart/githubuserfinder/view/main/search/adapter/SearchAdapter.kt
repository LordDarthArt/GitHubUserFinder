package tk.lorddarthart.githubuserfinder.view.main.search.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tk.lorddarthart.githubuserfinder.R
import tk.lorddarthart.githubuserfinder.domain.local.model.UserItem
import tk.lorddarthart.githubuserfinder.databinding.ItemSingleUserBinding
import tk.lorddarthart.githubuserfinder.common.helper.SameCallback

class SearchAdapter : ListAdapter<UserItem, SearchAdapter.SearchViewHolder>(SameCallback<UserItem>()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSingleUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        Glide.with(holder.binding.root.context).load(urlString)
            .placeholder(R.drawable.ic_preload)
            .error(R.drawable.ic_account)
            .centerCrop()
            .into(holder.binding.profileImage)
    }

    inner class SearchViewHolder(val binding: ItemSingleUserBinding) : RecyclerView.ViewHolder(binding.root) {
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