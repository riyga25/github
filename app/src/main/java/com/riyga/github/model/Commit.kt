package com.riyga.github.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Commit: RealmObject() {
    @PrimaryKey
    lateinit var sha: String
    var author: String = ""
    var avatar: String = ""
    lateinit var message: String
    lateinit var date: String
}