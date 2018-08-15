package com.tuxlu.polyvox.User

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.android.volley.Request
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.InputFieldsVerifier.checkPassword
import com.tuxlu.polyvox.Utils.NetworkUtils
import com.tuxlu.polyvox.Utils.ToastType
import com.tuxlu.polyvox.Utils.UtilsTemp
import kotlinx.android.synthetic.main.activity_login_password_reset.*
import org.json.JSONObject

/**
 * Created by tuxlu on 29/12/17.
 */


class ForgotReset : AppCompatActivity() {

    private var resetID : String = ""

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_password_reset)
        resetID = intent.data.getQueryParameter("id")
        if (UtilsTemp.isStringEmpty(resetID)) {
            UtilsTemp.showToast(this, getString(R.string.invalid_link), ToastType.ERROR)
            finish()
        }
        passwordInput.onFocusChangeListener = Register.RegisterHintFocus(passHint, passwordLayout)
        oldPasswordInput.onFocusChangeListener = Register.RegisterHintFocus(registerPassHint2, oldPasswordLayout)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            passwordInput.setAutofillHints(View.AUTOFILL_HINT_PASSWORD)
            oldPasswordInput.setAutofillHints(View.AUTOFILL_HINT_PASSWORD)
            passwordInput.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_YES
            oldPasswordInput.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_YES
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun buttonClick(v: View) {
        if (!checkPassword(passwordLayout, this))
            return
        val password = passwordInput.text.toString()
        val passwordCheck = oldPasswordInput.text.toString()
        if (password != passwordCheck)
        {
            passwordLayout.error = getString(R.string.passwords_not_identical)
             passHint.visibility = View.GONE
            return
        }


        val map = HashMap<String, String>()
        map.put("id", resetID)
        val url = NetworkUtils.getParametrizedUrl(APIUrl.RESET_PASSWORD, map)

        val body = JSONObject()
        body.put("password", password)

        APIRequest.JSONrequest(this, Request.Method.POST, url, false, body, { _ ->
            UtilsTemp.showToast(this, getString(R.string.password_changed), ToastType.SUCCESS)
            startActivity(Intent(baseContext, Login::class.java))
            finish()
        }, {error ->  if (error.networkResponse != null && error.networkResponse.statusCode == 422)
            UtilsTemp.showToast(this, getString(R.string.invalid_link), ToastType.ERROR)
        })
    }

}
