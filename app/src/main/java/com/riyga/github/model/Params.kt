package com.riyga.github.model

import io.realm.RealmObject

open class Params : RealmObject() {
    lateinit var token: String
}