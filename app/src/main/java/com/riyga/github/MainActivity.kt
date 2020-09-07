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
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray


class MainActivity : AppCompatActivity() {
    private val url = "https://api.github.com/repositories"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val queue = Volley.newRequestQueue(this)

        getData(queue)
    }

    private fun getData(queue: RequestQueue) {
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                val repos = parseResponse(response)
//                saveIntoDB(catList)
//                showCatsFromDB()
                setList(repos)
            },
            { Toast.makeText(this, "Request error", Toast.LENGTH_SHORT).show() }
        )

        queue.add(stringRequest)
    }

    private fun parseResponse(responseText: String): List<Repo> {
        val repos: MutableList<Repo> = mutableListOf()
        val jsonArray = JSONArray(responseText)
        for (index in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(index)
            val name = jsonObject.getString("name")
            val id = jsonObject.getInt("id")
            val full_name = jsonObject.getString("full_name")
            val description = jsonObject.getString("description")
            val ownerObject = jsonObject.getJSONObject("owner")
            val owner_id = ownerObject.getInt("id")
            val owner_avatar = ownerObject.getString("avatar_url")
            val owner_login = ownerObject.getString("login")

            val repo = Repo(id, name, full_name, description, owner_id, owner_login, owner_avatar)

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
}