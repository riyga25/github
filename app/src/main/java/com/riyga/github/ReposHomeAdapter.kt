package com.riyga.github

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.riyga.github.model.Repo
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

class RepoAdapter(private val repos: RealmResults<Repo>) :
    RealmRecyclerViewAdapter<Repo, RepoViewHolder>(repos, true, true) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val rootView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return RepoViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        repos[position]?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return repos.size
    }

}

class RepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val name: TextView = itemView.findViewById(R.id.item_name_id)
    private val description: TextView = itemView.findViewById(R.id.item_description_id)
    private val avatar: ImageView = itemView.findViewById(R.id.item_avatar_id)
    private val favorite: ImageView = itemView.findViewById(R.id.list_favorite_id)
    private val stars: TextView = itemView.findViewById(R.id.item_stars)

    fun bind(repo: Repo) {
        name.text = repo.full_name
        description.text = repo.description
        stars.text = repo.stargazers_count.toString()
        Glide.with(itemView).load(repo.owner_avatar).into(avatar)

        if (repo.favorite) {
            favorite.setImageResource(R.drawable.ic_baseline_star)
        } else {
            favorite.setImageResource(R.drawable.ic_baseline_star_border)
        }

        itemView.setOnClickListener {
            openDetail(repo)
        }

        favorite.setOnClickListener {
            Realm.getDefaultInstance().executeTransaction {
                val dbRepo = it.where(Repo::class.java).equalTo("id", repo.id).findFirst()
                dbRepo?.favorite = !repo.favorite
            }
        }
    }

    private fun openDetail(repo: Repo) {
        val action = HomeFragmentDirections.actionNavigationHomeToDetailFragment(repo.full_name)
        itemView.findNavController().navigate(action)
    }
}