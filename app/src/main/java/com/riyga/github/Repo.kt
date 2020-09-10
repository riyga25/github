package com.riyga.github

import io.realm.RealmObject

open class Repo: RealmObject() {
    lateinit var id: String
    lateinit var name: String
    lateinit var full_name: String
    lateinit var description: String
    lateinit var owner_id: String
    lateinit var owner_login: String
    lateinit var owner_avatar: String
//    lateinit var commits: RealmList<Commit>
    var favorite: Boolean = false
    var stargazers_count: Int = 0
    var forks_count: Int = 0
    var language: String = ""
}