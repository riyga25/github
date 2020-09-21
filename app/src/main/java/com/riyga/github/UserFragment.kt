package com.riyga.github

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.riyga.github.model.Params
import com.riyga.github.model.User
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment : Fragment() {
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = realm.where(User::class.java).findFirst()
        val userPic = activity?.findViewById<ImageView>(R.id.user_pic)

        if (userPic != null) {
            Glide.with(view).load(user?.avatar).into(userPic)
        }
        user_login.text = user?.login

        sign_out.setOnClickListener{
            logOut()
        }
    }

    override fun onDetach() {
        super.onDetach()
        realm.close()
    }

    private fun logOut(){
        realm.executeTransaction{
            it.where(User::class.java).findFirst()?.deleteFromRealm()
            it.where(Params::class.java).findFirst()?.deleteFromRealm()
        }
        this.startActivity(Intent(context, LoginActivity::class.java))
        activity?.finish()
    }
}