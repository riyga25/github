package com.riyga.github

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray


class MainActivity : AppCompatActivity() {
    private val url = "https://api.github.com/repositories"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRealm()
        showReposFromDB()
        val queue = Volley.newRequestQueue(this)
        getData(queue)
    }

    private fun getData(queue: RequestQueue) {
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                val repos = parseResponse(response)
                saveIntoDB(repos)
                showReposFromDB()
            },
            { Toast.makeText(this, "Request error", Toast.LENGTH_SHORT).show() }
        )

        queue.add(stringRequest)
    }

    private fun parseResponse(responseText: String): List<Repo> {
        val repos: MutableList<Repo> = mutableListOf()
        val jsonArray = JSONArray(responseText)
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

            repos.add(repo)
        }

        return repos
    }

    private fun setList(repos: List<Repo>) {
        val adapter = RepoAdapter(repos)
        recyclerId.adapter = adapter

        recyclerId.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        val layoutManager = LinearLayoutManager(this)
        recyclerId.layoutManager = layoutManager
    }

    private fun initRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }

    private fun saveIntoDB(repos: List<Repo>) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.copyToRealm(repos)
        realm.commitTransaction()
    }

    private fun loadFromDB(): List<Repo> {
        val realm = Realm.getDefaultInstance()
        return realm.where(Repo::class.java).findAll()
    }

    private fun showReposFromDB() {
        val repos = loadFromDB()
        setList(repos)
    }
}