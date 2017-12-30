package com.tuxlu.polyvox.User


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.android.volley.ParseError
import com.android.volley.Request
import com.tuxlu.polyvox.Homepage.Home
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.ToastType
import com.tuxlu.polyvox.Utils.UIElements.LoadingUtils
import com.tuxlu.polyvox.Utils.UtilsTemp
import kotlinx.android.synthetic.main.activity_login_mail_validated.*

/**
 * Created by tuxlu on 26/11/17.
 */

class MailValidated : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_mail_validated)
        mainView.visibility = View.GONE
        LoadingUtils.StartLoadingView(rootView, applicationContext)

        var url = intent.data.toString()
        url = url.replace("http://", "https://")
        if (UtilsTemp.isStringEmpty(url)) {
            UtilsTemp.showToast(this, getString(R.string.invalid_link), ToastType.ERROR)
            backHome(mainView)
        }

        APIRequest.JSONrequest(this, Request.Method.GET, url, false, null, { _ ->
            LoadingUtils.EndLoadingView(rootView)
            mainView.visibility = View.VISIBLE
        }, { error ->
            if (error.networkResponse != null && error.networkResponse.statusCode == 422) {
                UtilsTemp.showToast(this, getString(R.string.invalid_link), ToastType.ERROR)
                backHome(mainView)
            }
            if (error is ParseError) {
                LoadingUtils.EndLoadingView(rootView)
                mainView.visibility = View.VISIBLE
            }
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