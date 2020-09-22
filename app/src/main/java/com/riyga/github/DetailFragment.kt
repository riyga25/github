package com.riyga.github

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.riyga.github.model.Commit
import com.riyga.github.model.Repo
import io.realm.Realm
import io.realm.internal.android.ISO8601Utils
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_detail.*
import org.json.JSONArray
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

class DetailFragment : Fragment() {
    private val args: DetailFragmentArgs by navArgs()
    private lateinit var realm: Realm
    private var repo: Repo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        realm = Realm.getDefaultInstance()
        repo = realm.where<Repo>().equalTo("full_name", args.fullName).findFirst()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getRepoFromDB()
        getCommits()

        repo?.addChangeListener<Repo> {results ->
            setFavoriteImage(results.favorite)
        }

        detailFavorite.setOnClickListener{
            realm.executeTransaction{
                repo?.favorite = !repo?.favorite!!
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            activity?.onBackPressed();
            return true;
        };
        return super.onOptionsItemSelected(item)
    }

    override fun onDetach() {
        super.onDetach()
        realm.close()
    }

    private fun getRepoFromDB() {
        repo?.let { showData(it) }
    }

    private fun showData(repo: Repo) {
        val avatar = activity?.findViewById<ImageView>(R.id.detailOwnerPic)
        detailOwnerName.text = repo.owner_login
        detailDescription.text = repo.description
        detailTitle.text = repo.name

        if (avatar != null) {
            Glide.with(this).load(repo.owner_avatar).into(avatar)
        }
        setFavoriteImage(repo.favorite)
        setList(repo.commits)
    }

    private fun setFavoriteImage(isFavorite: Boolean) {
        if (isFavorite) {
            detailFavorite?.setImageResource(R.drawable.ic_baseline_star)
        } else {
            detailFavorite?.setImageResource(R.drawable.ic_baseline_star_border)
        }
    }

    private fun getCommits() {
        commits_progressBar.visibility = View.VISIBLE

        ApiService().get(
            context,
            "/repos/${args.fullName}/commits",
            { response ->
                val commits = parseResponse(response)
                saveIntoDB(commits)
                commits_progressBar.visibility = View.GONE
                setList(commits)
            },
            {
                commits_progressBar.visibility = View.GONE
                Toast.makeText(context, "Request error", Toast.LENGTH_SHORT).show()
            }
        )

    }

    private fun saveIntoDB(commits: List<Commit>) {
        realm.executeTransaction {
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
        val list = activity?.findViewById<LinearLayout>(R.id.commits_list)

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
                Glide.with(child).load(it.avatar).into(avatar)
            } else {
                avatar.visibility = View.GONE
            }

            list?.addView(child)
        }
    }
}