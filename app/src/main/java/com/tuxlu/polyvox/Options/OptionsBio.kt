package com.tuxlu.polyvox.Options

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.view.View
import com.android.volley.Request
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.*
import org.json.JSONObject

/**
 * Created by tuxlu on 29/11/17.
 */

class OptionsBio() : MyAppCompatActivity() {

    private fun showLayout(rootView: View, mainView: View)
    {
        mainView.visibility = View.VISIBLE
        LoadingUtils.EndLoadingView(rootView)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_options_bio)

        val rootView = findViewById<View>(R.id.rootView)

        val mainView : View = findViewById<TextInputEditText>(R.id.mainView)

        val bioInput: TextInputEditText = findViewById<TextInputEditText>(R.id.input)

        LoadingUtils.StartLoadingView(rootView, this)

        NetworkUtils.JSONrequest(this, Request.Method.GET, APIUrl.BASE_URL + APIUrl.INFO_CURRENT_USER,
                true, null, { current ->
            val obj = current.getJSONObject(APIUrl.SEARCH_USER_JSONOBJECT)
            val currentUserName = obj.getString("userName")


            NetworkUtils.JSONrequest(this, Request.Method.GET,
                    APIUrl.BASE_URL + APIUrl.INFO_USER + currentUserName,
                    true, null, { response ->
                val topObj = response.getJSONObject(APIUrl.SEARCH_USER_JSONOBJECT)
                val info = topObj.getJSONObject("info")
                val description = info.getString("description")
                if (!description.isBlank() && description != "null")
                    bioInput.setText(description)
                else
                    bioInput.setHint(R.string.bio_hint)

                showLayout(rootView, mainView)
            }
                    , { e ->
                e.printStackTrace()
                showLayout(rootView, mainView)
            })


        }, { e ->
            e.printStackTrace()
            showLayout(rootView, mainView)
        })
    }

    public fun buttonClick(v: View) {

        val bio: String = findViewById<TextInputEditText>(R.id.input).text.toString()
        if (bio.length > 500) {
            UtilsTemp.showToast(this, getString(R.string.bio_too_long))
        }

        val body = JSONObject()
        body.put(APIUrl.UPDATE_INF0_BIO, bio)
        NetworkUtils.JSONrequest(this, Request.Method.POST,
                APIUrl.BASE_URL + APIUrl.UPDATE_INF0, true, body, { _ ->
            UtilsTemp.showToast(this, getString(R.string.bio_updated))
        }, { e ->
            e.printStackTrace()
            if (e.networkResponse != null) {
                var debug_response: String = String(e.networkResponse.data)
                debug_response += "debug"
            }
        })
    }

}
