package com.riyga.github

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_favorites.*

class FavoritesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        navigationListener()
    }

    private fun navigationListener() {
        bottom_navigation.selectedItemId = R.id.fav_screen

        bottom_navigation.setOnNavigationItemSelectedListener () {item ->
            when(item.itemId) {
                R.id.home_screen -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent)

                    true
                }
                else -> false
            }
        }
    }
}