package com.tuxlu.polyvox.Options

import android.content.DialogInterface
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.android.volley.Request
import com.tuxlu.polyvox.Homepage.Home
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.User.ProfilePage
import com.tuxlu.polyvox.Utils.APIUrl
import com.tuxlu.polyvox.Utils.ImageUtils
import com.tuxlu.polyvox.Utils.LoadingUtils
import com.tuxlu.polyvox.Utils.NetworkUtils
import org.json.JSONObject

/**
 * Created by tuxlu on 29/11/17.
 */

class OptionsBio() : AppCompatActivity() {

    private fun showLayout(rootView: View, mainView: View)
    {
        mainView.visibility = View.VISIBLE
        LoadingUtils.EndLoadingView(rootView)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options_bio)
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
            ImageUtils.showToast(this, getString(R.string.bio_too_long), R.style.SimpleToast)
        }

        val body = JSONObject()
        body.put(APIUrl.UPDATE_INF0_BIO, bio)
        NetworkUtils.JSONrequest(this, Request.Method.POST,
                APIUrl.BASE_URL + APIUrl.UPDATE_INF0, true, body, { _ ->
            ImageUtils.showToast(this, getString(R.string.bio_updated), R.style.SimpleToast)
        }, { e ->
            e.printStackTrace()
            if (e.networkResponse != null) {
                var debug_response: String = String(e.networkResponse.data)
                debug_response += "debug"
            }
        })
    }

}
