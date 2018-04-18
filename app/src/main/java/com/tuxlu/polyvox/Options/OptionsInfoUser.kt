package com.tuxlu.polyvox.Options

import android.os.Bundle
import android.view.View
import com.android.volley.Request
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.User.Register
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.Auth.AuthUtils
import com.tuxlu.polyvox.Utils.MyDateUtils
import com.tuxlu.polyvox.Utils.NetworkUtils
import com.tuxlu.polyvox.Utils.UIElements.LoadingUtils
import com.tuxlu.polyvox.Utils.UIElements.MyAppCompatActivity
import com.tuxlu.polyvox.Utils.UtilsTemp
import kotlinx.android.synthetic.main.activity_user_options_info_user.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by tuxlu on 29/11/17.
 */

class OptionsInfoUser : MyAppCompatActivity() {

    private fun showLayout()
    {
        mainView.visibility = View.VISIBLE
        LoadingUtils.EndLoadingView(rootView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_options_info_user)
        mainView.visibility = View.INVISIBLE
        MyDateUtils.setDateSpinners(rootView, this)

        val userName = AuthUtils.getUsername(baseContext)
        val url = APIUrl.BASE_URL + APIUrl.INFO_USER + userName + APIUrl.INFO_USER_SUFF;

        APIRequest.JSONrequest(this, Request.Method.GET, url,
                true, null, { current ->
            val topObj = current.getJSONObject(APIUrl.SEARCH_USER_JSONOBJECT)
            val info = topObj.getJSONObject("info")
            surname.setText(info.getString("firstName"))
            name.setText(info.getString("lastName"))

            name.onFocusChangeListener = Register.RegisterHintFocus(nameHint, NameLayout)
            surname.onFocusChangeListener = Register.RegisterHintFocus(surnameHint, SurnameLayout)

            val date = info.getString("birthday")
            MyDateUtils.setSpinnersToDate(date, "yyyy-MM-dd", rootView)
            showLayout()
        }, {_ -> showLayout()})

    }

    @Suppress("UNUSED_PARAMETER")
    fun buttonClick(v: View)
    {
        if (!surname.text.matches("^[a-zA-Z].{2,128}$".toRegex())) {
            SurnameLayout.error = getString(R.string.info_user_hint_bottom)
            return
        }
        if (!name.text.matches("^[a-zA-Z].{2,128}$".toRegex())) {
            NameLayout.error = getString(R.string.info_user_hint_bottom)
            return
        }

        val date : Date? = MyDateUtils.checkDate(rootView, getString(R.string.register_dob_error)) ?: return
        val ft = SimpleDateFormat("yyyy-MM-dd")
        val birthday = ft.format(date)
        val body = JSONObject()

        body.put(APIUrl.INFO_FIRSTNAME, surname.text)
        body.put(APIUrl.INFO_LASTNAME, name.text)
        body.put(APIUrl.REGISTER_PARAM4, birthday)

        APIRequest.JSONrequest(this, Request.Method.PUT,
                APIUrl.BASE_URL + APIUrl.UPDATE_INF0, true, body, { _ ->
            UtilsTemp.showToast(this, getString(R.string.user_info_updated))
        }, null)
    }

    //startActivity(Intent(baseContext, Home::class.java


}
