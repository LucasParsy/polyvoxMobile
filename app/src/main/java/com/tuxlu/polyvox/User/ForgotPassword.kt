package com.tuxlu.polyvox.User

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.view.View
import com.android.volley.Request
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.ToastType
import com.tuxlu.polyvox.Utils.UIElements.InputFieldsVerifier.checkMail
import com.tuxlu.polyvox.Utils.UIElements.MyAppCompatActivity
import com.tuxlu.polyvox.Utils.UtilsTemp
import kotlinx.android.synthetic.main.activity_login_forgot_password.*

import org.json.JSONObject
import java.net.URLEncoder

/**
 * Created by tuxlu on 29/12/17.
 */


class ForgotPassword() : MyAppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_forgot_password)
    }

    @Suppress("UNUSED_PARAMETER")
    public fun buttonClick(v: View) {

        MailInput.text;
        if (!checkMail(LoginMailLayout, this))
            return

        val body = JSONObject()
        body.put("email", MailInput.text.toString())
        APIRequest.JSONrequest(this, Request.Method.POST,
                APIUrl.BASE_URL + APIUrl.FORGOT_PASSWORD, false, body, { _ ->
            UtilsTemp.showToast(this, getString(R.string.mail_sent), ToastType.SUCCESS)
            finish()
        }, { error ->
            if (error.networkResponse != null && error.networkResponse.statusCode == 404)
                LoginMailLayout.error = getString(R.string.register_mail_error)
        })
    }

}
