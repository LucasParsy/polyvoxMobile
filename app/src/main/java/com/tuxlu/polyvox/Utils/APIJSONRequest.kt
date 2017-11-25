package com.tuxlu.polyvox.Utils

import com.android.volley.ParseError
import org.json.JSONException
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.Response.success
import org.json.JSONObject
import android.R.attr.data
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import java.nio.charset.Charset


/**
 * Created by tuxlu on 17/11/17.
 */

class APIJsonObjectRequest : JsonObjectRequest {

    constructor(method: Int, url: String, jsonRequest: JSONObject, listener: Response.Listener<JSONObject>, errorListener: Response.ErrorListener) : super(method, url, jsonRequest, listener, errorListener) {}

    constructor(url: String, jsonRequest: JSONObject, listener: Response.Listener<JSONObject>, errorListener: Response.ErrorListener) : super(url, jsonRequest, listener, errorListener) {}

    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject> {
        try {
            val jsonString = String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers, JsonRequest.PROTOCOL_CHARSET)))
            val jsonResponse = JSONObject(jsonString)
            var token : String? = null;
            if (response.headers.containsKey(APIUrl.COOKIE_HEADER_RECEIVE))
                token = response.headers[APIUrl.COOKIE_HEADER_RECEIVE];
            if (token != null)
            {
                var semicolon : Int = token.indexOf(';')
                if (semicolon != -1)
                    token = token.substring(0, semicolon)
                jsonResponse.put(APIUrl.COOKIE_HEADER_RECEIVE, token)
            }
            return Response.success(jsonResponse, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: Exception) {
            return Response.error(ParseError(e))
        }
    }
}