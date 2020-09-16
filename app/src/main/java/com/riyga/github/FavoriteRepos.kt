package com.riyga.github

import io.realm.RealmList
import io.realm.RealmObject

open class FavoriteRepos: RealmObject() {
    var list: RealmList<String> = RealmList()
}