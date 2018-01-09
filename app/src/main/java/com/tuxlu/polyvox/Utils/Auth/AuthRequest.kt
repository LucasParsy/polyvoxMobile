package com.tuxlu.polyvox.Utils.Auth

import com.android.volley.ParseError
import com.android.volley.toolbox.HttpHeaderParser
import org.json.JSONObject
import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIUrl
import java.nio.charset.Charset


/**
 * Created by tuxlu on 17/11/17.
 */

class AuthRequest(ncontext: Context, nlogin: String, method: Int, url: String, jsonRequest: JSONObject, listener: Response.Listener<JSONObject>, errorListener: Response.ErrorListener) : JsonObjectRequest(method, url, jsonRequest, listener, errorListener) {

    private val context : Context = ncontext
    private val login : String = nlogin

    //constructor(url: String, jsonRequest: JSONObject, listener: Response.Listener<JSONObject>, errorListener: Response.ErrorListener) : super(url, jsonRequest, listener, errorListener) {}

    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject> {
        try {
            val jsonString = String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers, JsonRequest.PROTOCOL_CHARSET)))
            val jsonResponse = JSONObject(jsonString)
            var token : String? = null
            if (response.headers.containsKey(APIUrl.COOKIE_HEADER_RECEIVE))
                token = response.headers[APIUrl.COOKIE_HEADER_RECEIVE]
            if (token != null)
            {
                val semicolon : Int = token.indexOf(';')
                if (semicolon != -1)
                    token = token.substring(0, semicolon)
                val account = Account(login, context.getString(R.string.account_type))
                val am = AccountManager.get(context)
                am.addAccountExplicitly(account, null, null)
                //am.setUserData(account, "refreshToken", refreshToken);
                am.setAuthToken(account, context.getString(R.string.account_type), token)
                val username = jsonResponse.getJSONObject("data").getString("userName")
                am.setUserData(account, "name", username)
                val url = jsonResponse.getJSONObject("data").getString("picture")
                am.setUserData(account, "picture", url)
            }
            return Response.success(jsonResponse, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: Exception) {
            return Response.error(ParseError(e))
        }
    }
}