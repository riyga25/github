package com.riyga.github

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.riyga.github.model.Repo
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_favorites.*

class FavoritesFragment : Fragment() {
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showReposFromDB()
    }

    override fun onDetach() {
        super.onDetach()
        realm.close()
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
        return realm.where(Repo::class.java).equalTo("favorite", true).findAll()
    }

    private fun showReposFromDB() {
        val repos = loadFromDB()
        setList(repos)
    }
}