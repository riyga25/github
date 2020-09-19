package com.riyga.github.model

import com.riyga.github.model.Commit
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

open class Repo: RealmObject() {
    @PrimaryKey
    lateinit var id: String
    lateinit var name: String
    lateinit var full_name: String
    lateinit var description: String
    lateinit var owner_id: String
    lateinit var owner_login: String
    lateinit var owner_avatar: String
    var commits: RealmList<Commit> = RealmList()
    var favorite: Boolean = false
    var stargazers_count: Int = 0
    var forks_count: Int = 0
    var language: String = ""
}