package com.riyga.github.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.riyga.github.R

class FavoritesFragment : Fragment() {

    private lateinit var dashboardViewModel: FavoritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProviders.of(this).get(FavoritesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_favorites, container, false)

        return root
    }
}