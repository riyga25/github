package com.riyga.github

import io.realm.RealmObject

open class Commit: RealmObject() {
    lateinit var sha: String
    lateinit var author: String
    lateinit var message: String
    lateinit var date: String
}