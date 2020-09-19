package com.riyga.github

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.VolleyError
import com.riyga.github.model.Params
import com.riyga.github.model.User
import io.realm.Realm
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    lateinit var username: EditText
    lateinit var password: EditText
    lateinit var login: Button
    lateinit var loading: ProgressBar
    lateinit var errorMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
        loading = findViewById(R.id.loading)
        errorMessage = findViewById(R.id.loginError)

        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validate(s.toString(), username.text.toString())
            }
        })

        password.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                if(password.text.trim().isNotEmpty() && username.text.trim().isNotEmpty()){
                    signIn()
                }
                true
            } else {
                false
            }
        }

        username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validate(password.text.toString(), s.toString())
            }
        })

        login.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        loading.visibility = View.VISIBLE
        errorMessage.visibility = View.GONE

        val base64 = Base64.encodeToString(
            "${username.text}:${password.text}".toByteArray(),
            Base64.DEFAULT
        )
        val realm: Realm = Realm.getDefaultInstance()

        realm.executeTransaction {
            val params = it.createObject(Params::class.java)
            params.token = base64
        }

        Api().get(
            this,
            "/user",
            { r -> successLogin(r) },
            { r -> failedLogin(r) })
        realm.close()
    }

    private fun successLogin(response: String) {
        val user = parseResponse(response)
        val realm: Realm = Realm.getDefaultInstance()

        realm.executeTransaction {
            val userObject = realm.createObject(User::class.java, user.id)
            userObject.avatar = user.avatar
            userObject.login = user.login
        }
        loading.visibility = View.GONE
        this.startActivity(Intent(this, MainActivity::class.java))
        realm.close()
        finish()
    }

    private fun failedLogin(response: VolleyError) {
        val realm: Realm = Realm.getDefaultInstance()

        realm.executeTransaction {
            val params = it.where(Params::class.java).findFirst()
            params?.deleteFromRealm()
        }
        loading.visibility = View.GONE
        errorMessage.visibility = View.VISIBLE
        realm.close()
    }

    private fun validate(pass: String, name: String) {
        login.isEnabled = pass.trim().isNotEmpty() && name.trim().isNotEmpty()
    }

    private fun parseResponse(responseString: String): User {
        val user = User()
        val jsonObject = JSONObject(responseString)
        user.id = jsonObject.getInt("id").toString()
        user.login = jsonObject.getString("login")
        user.avatar = jsonObject.getString("avatar_url")

        return user
    }
}