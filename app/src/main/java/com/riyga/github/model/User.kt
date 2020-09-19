package com.riyga.github.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class User: RealmObject() {
    @PrimaryKey
    lateinit var id: String
    lateinit var login: String
    lateinit var avatar: String
}