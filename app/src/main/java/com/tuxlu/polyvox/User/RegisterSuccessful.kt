package com.tuxlu.polyvox.User

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.android.volley.Response
import com.tuxlu.polyvox.Homepage.Home
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.NetworkUtils
import com.tuxlu.polyvox.Utils.UIElements.LoadingUtils

/**
 * Created by tuxlu on 26/11/17.
 */

class RegisterSuccessful : AppCompatActivity() {
    lateinit var rootView: View
    lateinit var mainView: View
    private lateinit var mailView: TextView

    public override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register_successful)

        rootView = findViewById(R.id.rootView)
        mainView = findViewById(R.id.mainView)
        mailView = findViewById(R.id.mailTextView)

        val mail = intent.getStringExtra("mail")
        mailView.text = mail

        mainView.visibility = View.INVISIBLE
        LoadingUtils.StartLoadingView(rootView, applicationContext)

        //ENVOI MAIL CONFIRMATION
        NetworkUtils.sendMail(baseContext, Response.Listener {
            mainView.visibility = View.VISIBLE
            LoadingUtils.EndLoadingView(rootView)
         })
    }

    @Suppress("UNUSED_PARAMETER")
    fun backHome(v: View) {
        val nin: Intent = Intent(applicationContext, Home::class.java)
        nin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(nin)
        finish()
    }
}