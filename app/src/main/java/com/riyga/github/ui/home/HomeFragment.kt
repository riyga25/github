package com.riyga.github.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.riyga.github.R
import com.riyga.github.model.Repo
import com.riyga.github.RepoAdapter
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONArray

class HomeFragment : Fragment() {
    private val url = "https://api.github.com/repositories"
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        showReposFromDB()

        return root
    }

    private fun getData() {
        val queue = Volley.newRequestQueue(context)
        val progress = activity?.findViewById<View>(R.id.progressBar)
        progress?.visibility = View.VISIBLE

        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                val repos = parseResponse(response)
                saveIntoDB(repos)
                progress?.visibility = View.INVISIBLE
                showReposFromDB()
            },
            {
                progress?.visibility = View.INVISIBLE
                Toast.makeText(context, "Request error", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(stringRequest)
    }

    private fun parseResponse(responseText: String): List<Repo> {
        val repos: MutableList<Repo> = mutableListOf()
        val jsonArray = JSONArray(responseText)
        val realm: Realm = Realm.getDefaultInstance()
        val favRepos = realm.where(Repo::class.java).equalTo("favorite", true).findAll()
            .map { repo -> repo.id }

        for (index in 0 until jsonArray.length()) {
            val repo = Repo()

            val jsonObject = jsonArray.getJSONObject(index)
            val name = jsonObject.getString("name")
            val id = jsonObject.getInt("id")
            val full_name = jsonObject.getString("full_name")
            val description = jsonObject.getString("description")
            val ownerObject = jsonObject.getJSONObject("owner")
            val owner_id = ownerObject.getInt("id")
            val owner_avatar = ownerObject.getString("avatar_url")
            val owner_login = ownerObject.getString("login")

            repo.id = id.toString()
            repo.name = name
            repo.full_name = full_name
            repo.description = description
            repo.owner_id = owner_id.toString()
            repo.owner_avatar = owner_avatar
            repo.owner_login = owner_login
            repo.favorite = favRepos.contains(id.toString())

            repos.add(repo)
        }

        return repos
    }

    private fun setList(repos: RealmResults<Repo>) {
        val adapter = RepoAdapter(repos)
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

    private fun saveIntoDB(repos: List<Repo>) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            realm.insertOrUpdate(repos)
        }
    }

    private fun loadFromDB(): RealmResults<Repo> {
        val realm = Realm.getDefaultInstance()
        return realm.where(Repo::class.java).findAll()
    }

    private fun showReposFromDB() {
        val repos = loadFromDB()
        setList(repos)
    }
}