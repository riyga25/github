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

class FavoritesAdapter(private val repos: RealmResults<Repo>) :
    RealmRecyclerViewAdapter<Repo, FavoritesViewHolder>(repos, true, true) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val rootView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return FavoritesViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        repos[position]?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return repos.size
    }

}

class FavoritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val name: TextView = itemView.findViewById(R.id.item_name_id)
    private val description: TextView = itemView.findViewById(R.id.item_description_id)
    private val avatar: ImageView = itemView.findViewById(R.id.item_avatar_id)
    private val favorite: ImageView = itemView.findViewById(R.id.list_favorite_id)
    val realm: Realm = Realm.getDefaultInstance()

    fun bind(repo: Repo) {
        name.text = repo.full_name
        description.text = repo.description
        Glide.with(itemView).load(repo.owner_avatar).into(avatar)
        favorite.setImageResource(R.drawable.ic_baseline_star)

        itemView.setOnClickListener {
            openDetail(repo)
        }

        favorite.setOnClickListener {
            realm.executeTransaction {
                val dbRepo = realm.where(Repo::class.java).equalTo("id", repo.id).findFirst()
                dbRepo?.favorite = false
            }
        }
    }

    private fun openDetail(repo: Repo) {
        val action = FavoritesFragmentDirections.actionNavigationFavoritesToDetailFragment(repo.full_name)
        itemView.findNavController().navigate(action)
    }
}