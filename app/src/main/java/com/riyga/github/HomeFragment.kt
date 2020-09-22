package com.riyga.github

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.riyga.github.model.Repo
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class HomeFragment : Fragment() {
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
        getData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showReposFromDB()
        swipeContainer.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getData()
        })
    }

    override fun onDetach() {
        super.onDetach()
        realm.close()
    }

    private fun getData() {
        val progress = activity?.findViewById<View>(R.id.progressBar)
        progress?.visibility = View.VISIBLE

        ApiService().get(
            context,
            "/repositories",
            { response ->
                val repos = parseResponse(response)
                saveIntoDB(repos)

                lifecycleScope.launch {
                    getAdditionalInfo()
                }
                progress?.visibility = View.INVISIBLE
                swipeContainer?.isRefreshing = false
                showReposFromDB()
            },
            {
                progress?.visibility = View.INVISIBLE
                swipeContainer?.isRefreshing = false
                Toast.makeText(context, "Request error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun parseResponse(responseText: String): List<Repo> {
        val repos: MutableList<Repo> = mutableListOf()
        val jsonArray = JSONArray(responseText)
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
        val layoutManager = LinearLayoutManager(context)

        reposList?.adapter = adapter
        reposList?.layoutManager = layoutManager
        reposList?.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun saveIntoDB(repos: List<Repo>) {
        realm.executeTransaction {
            realm.insertOrUpdate(repos)
        }
    }

    private fun loadFromDB(): RealmResults<Repo> {
        return realm.where(Repo::class.java).findAll()
    }

    private fun showReposFromDB() {
        val repos = loadFromDB()
        setList(repos)
    }

    private suspend fun getAdditionalInfo() {
        withContext(Dispatchers.IO) {
            launch {
                Realm.getDefaultInstance().executeTransaction {
                    val dbRepos = it.where(Repo::class.java).findAll()
                    dbRepos.subList(0, 5).map { repo ->
                        ApiService().get(
                            context,
                            "/repos/${repo.full_name}",
                            { response ->
                                val jsonObject = JSONObject(response)
                                println(jsonObject.getInt("stargazers_count"))
                                repo.stargazers_count = jsonObject.getInt("stargazers_count")
                                repo.language = jsonObject.getString("language")
                                repo.forks_count = jsonObject.getInt("forks_count")
                            }, {}
                        )
                    }
                }
            }

        }
    }
}