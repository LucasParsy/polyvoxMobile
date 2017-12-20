package com.tuxlu.polyvox.Options

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.android.volley.Request
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.User.Register
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.UIElements.LoadingUtils
import com.tuxlu.polyvox.Utils.UtilsTemp
import kotlinx.android.synthetic.main.activity_user_options_info_user.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by tuxlu on 29/11/17.
 */

class OptionsInfoUser() : AppCompatActivity() {

    private fun showLayout()
    {
        mainView.visibility = View.VISIBLE
        LoadingUtils.EndLoadingView(rootView)
    }


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_options_info_user)
        Register.setSpinners(rootView, this, getResources().getString(R.string.day), getResources().getString(R.string.year))


        APIRequest.JSONrequest(this, Request.Method.GET, APIUrl.BASE_URL + APIUrl.INFO_CURRENT_USER,
                true, null, { current ->
            val obj = current.getJSONObject(APIUrl.SEARCH_USER_JSONOBJECT)
            surname.setText(obj.getString("firstName"))
            name.setText(obj.getString("lastName"))
            showLayout()
        }, {_ -> showLayout()})

    }

    public fun buttonClick(v: View)
    {
        var date : Date? = Register.checkDate(v, getString(R.string.register_dob_error))
        if (date == null)
            return
        val ft = SimpleDateFormat("yyyy-MM-dd")
        val birthday = ft.format(date)
        val body = JSONObject()
        body.put(APIUrl.INFO_FIRSTNAME, surname.text)
        body.put(APIUrl.INFO_LASTNAME, surname.text)
        body.put(APIUrl.REGISTER_PARAM4, birthday)

        APIRequest.JSONrequest(this, Request.Method.POST,
                APIUrl.BASE_URL + APIUrl.UPDATE_INF0, true, body, { _ ->
            UtilsTemp.showToast(this, getString(R.string.bio_updated))
        }, null)
    }

    //startActivity(Intent(baseContext, Home::class.java


}
