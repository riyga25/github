package com.riyga.github

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.riyga.github.model.Commit
import com.riyga.github.model.Repo
import io.realm.Realm
import io.realm.internal.android.ISO8601Utils
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_detail.*
import org.json.JSONArray
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*


class DetailActivity : AppCompatActivity() {
    private lateinit var realm: Realm
    companion object {
        const val DETAIL_FULL_NAME = "com.riyga.github.detail_full_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setActionBar()
        realm = Realm.getDefaultInstance()
        getRepoFromDB()
        getCommits()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun getRepoFromDB() {
        val value = intent?.extras?.getString(DETAIL_FULL_NAME)
        val repo = realm.where<Repo>().equalTo("full_name", value).findFirst()

        if(repo != null) {
            showData(repo)
        }
    }

    private fun showData(repo: Repo) {
        val avatar = findViewById<ImageView>(R.id.detailOwnerPic)
        detailOwnerName.text = repo.owner_login
        detailDescription.text = repo.description
        detailTitle.text = repo.name
        Glide.with(this).load(repo.owner_avatar).into(avatar)
        setList(repo.commits)
    }

    private fun getCommits() {
        val full_name = intent?.extras?.getString(DETAIL_FULL_NAME)
        commits_progressBar.visibility = View.VISIBLE

        if (full_name != null) {
            ApiService().get(
                this,
                "/repos/$full_name/commits",
                { response ->
                    val commits = parseResponse(response)
                    saveIntoDB(commits, full_name)
                    commits_progressBar.visibility = View.GONE
                    setList(commits)
                },
                {
                    commits_progressBar.visibility = View.GONE
                    Toast.makeText(this, "Request error", Toast.LENGTH_SHORT).show()
                }
            )
        }

    }

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

    private fun saveIntoDB(commits: List<Commit>, full_name: String) {
        realm.executeTransaction {
            val repo = realm.where<Repo>().equalTo("full_name", full_name).findFirst()
            repo?.commits?.addAll(commits)
        }
    }

    private fun parseResponse(responseText: String): List<Commit> {
        val commits: MutableList<Commit> = mutableListOf()
        val jsonArray = JSONArray(responseText)
        for (index in 0 until jsonArray.length()) {
            val commit = Commit()

            val jsonObject = jsonArray.getJSONObject(index)
            val sha = jsonObject.getString("sha")
            val commitObject = jsonObject.getJSONObject("commit")
            val commitAuthorObject = commitObject.getJSONObject("author")
            val message = commitObject.getString("message")
            var name = commitAuthorObject.getString("name")
            val date = commitAuthorObject.getString("date")

            if(!jsonObject.isNull("author")){
                val authorObject = jsonObject.getJSONObject("author")
                name = authorObject.getString("login")
                val avatar = authorObject.getString("avatar_url")
                commit.avatar = avatar
            }

            commit.sha = sha
            commit.message = message
            commit.date = date
            commit.author = name

            commits.add(commit)
        }

        return if(commits.size > 10){
            commits.subList(0, 10)
        } else {
            commits
        }
    }

    private fun setList(commits: List<Commit>) {
        val list = findViewById<LinearLayout>(R.id.commits_list)

        fun formatDate(dateString: String): String {
            val pattern = "yyyy-MM-dd"
            val date = ISO8601Utils.parse(dateString, ParsePosition(0))
            val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
            return simpleDateFormat.format(date)
        }

        commits.forEach{
            val child = layoutInflater.inflate(R.layout.commit_item, list, false)
            val name: TextView = child.findViewById(R.id.authorName)
            val message: TextView = child.findViewById(R.id.commitMessage)
            val date: TextView = child.findViewById(R.id.commitDate)
            val avatar: ImageView = child.findViewById(R.id.commit_avatar)

            name.text = it.author
            message.text = it.message
            date.text = formatDate(it.date)

            if(it.avatar != ""){
                Glide.with(child).load(it.avatar).into(avatar);
            } else {
                avatar.visibility = View.GONE
            }

            list.addView(child)
        }
    }
}