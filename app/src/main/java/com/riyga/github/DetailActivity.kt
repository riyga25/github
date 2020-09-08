package com.riyga.github

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_detail.*
import org.json.JSONArray

class DetailActivity : AppCompatActivity() {
    companion object {
        const val DETAIL_FULL_NAME = "com.riyga.github.detail_full_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setActionBar()
        getRepoFromDB()
//        val queue = Volley.newRequestQueue(this)
//        getCommits(queue)
    }

    private fun getRepoFromDB() {
        val realm = Realm.getDefaultInstance()
        val full_name = intent?.extras?.getString(DETAIL_FULL_NAME)
        val repo = realm.where<Repo>().equalTo("full_name", full_name).findFirst()

        if(repo != null) {
            showData(repo)
        }
    }

    private fun showData(repo: Repo) {
        val avatar = findViewById<ImageView>(R.id.detailOwnerPic)
        detailOwnerName.text = repo.owner_login
        detailDescription.text = repo.description
        detailTitle.text = repo.name
        Glide.with(this).load(repo.owner_avatar).into(avatar);

    }

//    private fun getCommits(queue: RequestQueue) {
//        val full_name = intent?.extras?.getString(DETAIL_FULL_NAME)
//
//        if (full_name != null) {
//            val url = "https://api.github.com/repos/$full_name/commits"
//
//            val stringRequest = StringRequest(
//                Request.Method.GET,
//                url,
//                { response ->
//                    val commits = parseResponse(response)
//                    saveIntoDB(commits, full_name)
//                },
//                { Toast.makeText(this, "Request error", Toast.LENGTH_SHORT).show() }
//            )
//
//            queue.add(stringRequest)
//        }
//
//    }

    private fun setActionBar() {
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

//    private fun saveIntoDB(commits: List<Commit>, full_name: String) {
//        val realm = Realm.getDefaultInstance()
//
//        realm.executeTransaction {
//            val repo = realm.where<Repo>().equalTo("full_name", full_name).findFirst()
//            repo?.commits = commits.toTypedArray()
//        }
//    }

//    private fun parseResponse(responseText: String): List<Commit> {
//        val commits: MutableList<Commit> = mutableListOf()
//        val jsonArray = JSONArray(responseText)
//        for (index in 0 until jsonArray.length()) {
//            val commit = Commit()
//
//            val jsonObject = jsonArray.getJSONObject(index)
//            val sha = jsonObject.getString("sha")
//            val commitObject = jsonObject.getJSONObject("commit")
//            val authorObject = commitObject.getJSONObject("author")
//            val message = commitObject.getString("message")
//            val name = authorObject.getString("name")
//            val date = authorObject.getString("date")
//
//            commit.sha = sha
//            commit.author = name
//            commit.message = message
//            commit.date = date
//
//            commits.add(commit)
//        }
//
//        return commits
//    }
}