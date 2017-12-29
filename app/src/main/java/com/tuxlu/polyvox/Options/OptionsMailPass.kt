package com.tuxlu.polyvox.Options

import android.os.Build
import android.os.Bundle
import android.view.View
import com.android.volley.Request
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.User.Register
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.Auth.AuthUtils
import com.tuxlu.polyvox.Utils.ToastType
import com.tuxlu.polyvox.Utils.InputFieldsVerifier.checkMail
import com.tuxlu.polyvox.Utils.InputFieldsVerifier.checkPassword
import com.tuxlu.polyvox.Utils.UIElements.MyAppCompatActivity
import com.tuxlu.polyvox.Utils.UtilsTemp
import kotlinx.android.synthetic.main.activity_user_options_mail_pass.*

import org.json.JSONObject

/**
 * Created by tuxlu on 29/12/17.
 */


class OptionsMailPass() : MyAppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_options_mail_pass)

        passwordInput.onFocusChangeListener = Register.RegisterHintFocus(passHint, passwordLayout)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            passwordInput.setAutofillHints(View.AUTOFILL_HINT_PASSWORD)
            oldPasswordInput.setAutofillHints(View.AUTOFILL_HINT_PASSWORD)
            passwordInput.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_YES
            oldPasswordInput.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_YES
        }

        val url = APIUrl.BASE_URL + APIUrl.INFO_USER + AuthUtils.getUsername(baseContext)
        APIRequest.JSONrequest(this, Request.Method.GET, url,
                true, null, { current ->
            val topObj = current.getJSONObject(APIUrl.SEARCH_USER_JSONOBJECT)
            val info = topObj.getJSONObject("info")
            val mail = info.getString("email")
            if (!UtilsTemp.isStringEmpty(mail))
                mailInput.setText(mail);
        }, null)
    }

    @Suppress("UNUSED_PARAMETER")
    public fun buttonClick(v: View) {


        if (!checkPassword(passwordLayout, this) ||
                !checkPassword(oldPasswordLayout, this) ||
                !checkMail(mailLayout, this))
            return

        val body = JSONObject()
        body.put("email", mailInput.text.toString())
        body.put("password", passwordInput.text.toString())
        body.put("oldPassword", oldPasswordInput.text.toString())

        APIRequest.JSONrequest(this, Request.Method.POST, APIUrl.BASE_URL + APIUrl.UPDATE_USER, true, body, { _ ->
            UtilsTemp.showToast(this, getString(R.string.user_info_updated), ToastType.SUCCESS)
        }, {error ->  if (error.networkResponse != null && error.networkResponse.statusCode == 422)
            UtilsTemp.showToast(this, getString(R.string.new_incorrect_credentials), ToastType.ERROR)
        })
    }

}
