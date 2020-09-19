package com.riyga.github

import android.content.Context
import android.util.Base64
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.riyga.github.model.Params
import com.riyga.github.model.User
import io.realm.Realm

class Api {
    private val BASE_URL = "https://api.github.com"
    private var TOKEN = ""

    fun init() {
        getCredentials()
    }

    fun get(
        context: Context,
        requestUrl: String,
        success: Response.Listener<String>,
        error: Response.ErrorListener?
    ) {
        val queue = Volley.newRequestQueue(context)

        if(TOKEN == ""){
            getCredentials()
        }

        val stringRequest = object: StringRequest(
            Request.Method.GET,
            "${BASE_URL}${requestUrl}",
            success,
            error
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                if(TOKEN != ""){
                    headers["Authorization"] = "Basic $TOKEN"
                }
                return headers
            }
        }

        queue.add(stringRequest)
    }

    fun getCredentials() {
        val realm = Realm.getDefaultInstance()
        val params = realm.where(Params::class.java).findFirst()

        if (params?.token != null) {
            TOKEN = params.token
        }

        realm.close()
    }
}