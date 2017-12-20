package com.tuxlu.polyvox.Utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import com.android.volley.*
import com.tuxlu.polyvox.R

import com.tuxlu.polyvox.Utils.API.APIUrl

import java.util.HashMap


/**
 * Created by parsyl on 19/07/2017.
 */


object NetworkUtils {

    @JvmStatic
    fun getParametrizedUrl(path: String, params: HashMap<String, String>): String {
        val builder = Uri.Builder()
        builder.scheme(APIUrl.SCHEME)
        builder.authority(APIUrl.AUTHORITY)
        val items = path.split("/")
        //automatic conversion by kotlin, surely a conversion problem
        //val items = path.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (item in items)
            builder.appendPath(item)
        for ((key, value) in params)
            builder.appendQueryParameter(key, value)
        return builder.build().toString()
    }

    @JvmStatic
    fun isConnected(context: Context): Boolean {
        val mgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = mgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    @JvmStatic
    fun checkNetworkError(context: Context, error: VolleyError) {
        if (error is NetworkError ||
                error is AuthFailureError ||
                error is TimeoutError ||
                error is ParseError) {
            UtilsTemp.showToast(context, context.getString(R.string.no_wifi_home), ToastType.NORMAL, R.drawable.ic_wifi_off_24dp)
        } else if (error is ServerError)
            UtilsTemp.showToast(context, context.getString(R.string.no_server), ToastType.NORMAL, R.drawable.no_server)
    }

    @JvmStatic
    fun sendMail(context: Context) {
        com.tuxlu.polyvox.Utils.API.APIRequest.JSONrequest(context, Request.Method.GET, APIUrl.BASE_URL + APIUrl.MAIL_SEND, true, null,
                { _ ->
                    UtilsTemp.showToast(context, context.getString(R.string.confirmation_mail_sent_short), ToastType.SUCCESS)
                }, { error ->
            //Todo: Gestion d'erreur si tout se passe mal
            error.printStackTrace()
        });
    }


}
