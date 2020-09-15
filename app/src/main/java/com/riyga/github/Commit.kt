package com.riyga.github

import io.realm.RealmObject

open class Commit: RealmObject() {
    lateinit var sha: String
    var author: String = ""
    var avatar: String = ""
    lateinit var message: String
    lateinit var date: String
}