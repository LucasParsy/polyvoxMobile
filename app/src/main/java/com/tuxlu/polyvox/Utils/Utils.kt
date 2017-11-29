package com.tuxlu.polyvox.Utils

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.tuxlu.polyvox.R

/**
 * Created by tuxlu on 28/11/17.
 */

public fun sendMail(context: Context)
{
    NetworkUtils.JSONrequest(context, Request.Method.GET, APIUrl.BASE_URL + APIUrl.MAIL_SEND, true, null,
            { _ ->
                ImageUtils.showToast(context, context.getString(R.string.confirmation_mail_sent_short), R.style.SimpleToast)
            }, { error ->
        //Todo: Gestion d'erreur si tout se passe mal
        error.printStackTrace()
    });
}
