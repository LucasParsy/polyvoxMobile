package com.tuxlu.polyvox.Options

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.android.volley.Request
import com.tuxlu.polyvox.Homepage.Home
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.APIUrl
import com.tuxlu.polyvox.Utils.MyAppCompatActivity
import com.tuxlu.polyvox.Utils.NetworkUtils

/**
 * Created by tuxlu on 29/11/17.
 */

class OptionsMenu() : MyAppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_options)
    }

        public fun profileClick(v: View)
    {
        //startActivity(Intent(baseContext, Home::class.java))
    }

    public fun bioClick(v: View)
    {
        startActivity(Intent(baseContext, OptionsBio::class.java))
    }

    public fun photoClick(v: View)
    {
        //startActivity(Intent(baseContext, Home::class.java))
    }

    public fun passwordClick(v: View)
    {
        //startActivity(Intent(baseContext, Home::class.java))
    }

    public fun mailClick(v: View)
    {
        //startActivity(Intent(baseContext, Home::class.java))
    }


    //todo: put in util function
    public fun decoClick(v: View)
    {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.logout_confirm))
                .setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener { _, _ ->
                    NetworkUtils.JSONrequest(this, Request.Method.GET,
                            APIUrl.BASE_URL + APIUrl.LOGOUT,
                            true, null, { _ ->
                        NetworkUtils.removeAccountLogout(this)
                        val nin: Intent = Intent(baseContext, Home::class.java)
                        nin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(nin)
                    }, { e -> e.printStackTrace() })
                })
                .setNegativeButton(getString(R.string.no), null)
                .show()
    }
}
