package com.riyga.github.model

import io.realm.RealmList
import io.realm.RealmObject

open class FavoriteRepos: RealmObject() {
    var list: RealmList<String> = RealmList()
}