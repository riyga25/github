package com.riyga.github.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.riyga.github.*
import com.riyga.github.model.FavoriteRepos
import com.riyga.github.model.Repo
import io.realm.Realm
import io.realm.kotlin.oneOf
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_favorites.*

class FavoritesFragment : Fragment() {

    private lateinit var dashboardViewModel: FavoritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProviders.of(this).get(FavoritesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_favorites, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showReposFromDB()
    }

    private fun setList(repos: List<Repo>) {
        val adapter = FavoritesAdapter(repos)
        recyclerId?.adapter = adapter

        recyclerId?.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        val layoutManager = LinearLayoutManager(context)
        recyclerId?.layoutManager = layoutManager
    }

    private fun loadFromDB(): List<Repo> {
        val realm = Realm.getDefaultInstance()
        val favoritesIds = realm.where<FavoriteRepos>().findFirst()?.list
        var repos: MutableList<Repo> = mutableListOf()

        if(favoritesIds != null){
            repos = realm.where<Repo>().oneOf("id", favoritesIds.toTypedArray()).findAll()
        }

        return repos
    }

    private fun showReposFromDB() {
        val repos = loadFromDB()
        setList(repos)
    }
}