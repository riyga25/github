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
import com.riyga.github.model.Repo
import io.realm.Realm
import io.realm.RealmResults
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

    private fun setList(repos: RealmResults<Repo>) {
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

    private fun loadFromDB(): RealmResults<Repo> {
        val realm = Realm.getDefaultInstance()
        return realm.where(Repo::class.java).equalTo("favorite", true).findAll()
    }

    private fun showReposFromDB() {
        val repos = loadFromDB()
        setList(repos)
    }
}