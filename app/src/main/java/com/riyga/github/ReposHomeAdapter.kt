package com.riyga.github

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.riyga.github.DetailActivity.Companion.DETAIL_FULL_NAME
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
    val realm: Realm = Realm.getDefaultInstance()

    fun bind(repo: Repo) {
        name.text = repo.full_name
        description.text = repo.description
        Glide.with(itemView).load(repo.owner_avatar).into(avatar)

        itemView.setOnClickListener {
            openDetail(itemView.context, repo)
        }

        if (repo.favorite) {
            favorite.setImageResource(R.drawable.ic_baseline_star)
        }

        favorite.setOnClickListener {
            realm.executeTransaction {
                val dbRepo = realm.where(Repo::class.java).equalTo("id", repo.id).findFirst()
                dbRepo?.favorite = !repo.favorite
            }
        }
    }

    private fun openDetail(context: Context, repo: Repo) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(DETAIL_FULL_NAME, repo.full_name)
        context.startActivity(intent)
    }
}