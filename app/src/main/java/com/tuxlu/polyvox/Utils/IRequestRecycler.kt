package com.tuxlu.polyvox.Utils

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by tuxlu on 12/11/17.
 */


abstract class IRequestRecycler<T : Any>() : IRecycler<T>() {

    abstract val requestUrl: String
    abstract val requestBody: JSONObject
    abstract val usesAPI: Boolean;

    abstract fun errorListener(error: VolleyError)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View? = super.onCreateView(inflater, container, savedInstanceState)
        LoadingUtils.StartLoadingView(view, context)
        request(requestUrl, requestBody, true, view)
        return view
    }

    internal fun request(url: String, body: JSONObject, append: Boolean, view: View? = null) {
        val eListen = Response.ErrorListener { error -> errorListener(error) }
        NetworkUtils.JSONrequest(context, Request.Method.GET, APIUrl.BASE_URL + url, usesAPI, body, Response.Listener<JSONObject> { response ->
            add(response, false)
            if (view != null)
                LoadingUtils.EndLoadingView(view)
        }, eListen)

    }
}
