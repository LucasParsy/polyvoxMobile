package com.tuxlu.polyvox.User

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.tuxlu.polyvox.Homepage.Home
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.*
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by tuxlu on 26/11/17.
 */

class RegisterSuccessful() : AppCompatActivity() {
    lateinit var rootView: View
    lateinit var mainView: View
    lateinit var mailView: TextView

    public override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_successful)

        rootView = findViewById(R.id.rootView)
        mainView = findViewById(R.id.mainView)
        mailView = findViewById(R.id.mailTextView)

        val mail = intent.getStringExtra("mail")
        mailView.text = mail

        mainView.visibility = View.INVISIBLE
        LoadingUtils.StartLoadingView(rootView, applicationContext)

        //ENVOI MAIL CONFIRMATION
        NetworkUtils.JSONrequest(applicationContext, Request.Method.POST, APIUrl.BASE_URL + APIUrl.MAIL_SEND, true, null,
                { _ ->
                    mainView.visibility = View.VISIBLE
                    LoadingUtils.EndLoadingView(rootView)
                }, { error ->
            //Todo: Gestion d'erreur si tout se passe mal
            error.printStackTrace()
        });
    }

    public fun backHome(v: View) {
        startActivity(Intent(this, Home::class.java))
        finish()
    }
}