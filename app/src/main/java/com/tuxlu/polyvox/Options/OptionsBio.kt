package com.tuxlu.polyvox.Options

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.view.View
import com.android.volley.Request
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.Auth.AuthUtils
import com.tuxlu.polyvox.Utils.NetworkUtils
import com.tuxlu.polyvox.Utils.UIElements.LoadingUtils
import com.tuxlu.polyvox.Utils.UIElements.MyAppCompatActivity
import com.tuxlu.polyvox.Utils.UtilsTemp
import org.json.JSONObject
import java.util.*

/**
 * Created by tuxlu on 29/11/17.
 */

class OptionsBio : MyAppCompatActivity() {

    private fun showLayout(rootView: View, mainView: View)
    {
        mainView.visibility = View.VISIBLE
        LoadingUtils.EndLoadingView(rootView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_options_bio)

        val rootView = findViewById<View>(R.id.rootView)

        val mainView : View = findViewById<TextInputEditText>(R.id.mainView)

        val bioInput: TextInputEditText = findViewById<TextInputEditText>(R.id.input)

        LoadingUtils.StartLoadingView(rootView, this)


        val userName = AuthUtils.getUsername(baseContext)
        val url = APIUrl.BASE_URL + APIUrl.INFO_USER + userName + APIUrl.INFO_USER_SUFF;

        APIRequest.JSONrequest(this, Request.Method.GET, url,
                    true, null, { response ->
                val topObj = response.getJSONObject(APIUrl.SEARCH_USER_JSONOBJECT)
                val info = topObj.getJSONObject("info")
                val description = info.getString("description")
               if (!UtilsTemp.isStringEmpty(description))
                    bioInput.setText(description)
                else
                    bioInput.setHint(R.string.bio_hint)

                showLayout(rootView, mainView)
            }
                    , { _ -> showLayout(rootView, mainView)
           })
    }

    @Suppress("UNUSED_PARAMETER")
    fun buttonClick(v: View) {

        val bio: String = findViewById<TextInputEditText>(R.id.input).text.toString()
        if (bio.length > 500) {
            UtilsTemp.showToast(this, getString(R.string.bio_too_long))
        }

        val body = JSONObject()
        body.put(APIUrl.UPDATE_INF0_BIO, bio)
        APIRequest.JSONrequest(this, Request.Method.PUT,
                APIUrl.BASE_URL + APIUrl.UPDATE_INF0, true, body, { _ ->
            UtilsTemp.showToast(this, getString(R.string.bio_updated))
        }, null)
    }

}
