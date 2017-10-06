package com.tuxlu.polyvox.Utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.AndroidAuthenticator;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tuxlu.polyvox.R;
import com.tuxlu.polyvox.User.Login;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by parsyl on 19/07/2017.
 */


public class NetworkUtils {

    public static boolean isConnected(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = mgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isAPIConnected(Context context) {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccounts();
        return (accounts.length != 0);
    }


    public static String getToken(Context context) {
        AccountManager am = AccountManager.get(context);
        //should'nt happen
        if (am.getAccounts().length == 0)
            return null;
        Bundle res;
        try {
                res = am.getAuthToken(am.getAccounts()[0],
                        context.getString(R.string.account_type), null, false, null, null).getResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return res.get(AccountManager.KEY_AUTHTOKEN).toString();
    }


    public static void JSONrequest(final Context context, final int method, final String url,
                                   final boolean usesApi, final JSONObject body,
                                   final Response.Listener<JSONObject> listener,
                                   final Response.ErrorListener errorListener) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (usesApi && !isAPIConnected(context))
                {
                    context.startActivity(new Intent(context, Login.class));
                    return;
                }
                final String token = usesApi ? getToken(context) : null;
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (method, url, body, listener, errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        if (!usesApi)
                            return super.getHeaders();
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("token", token);
                        return params;
                    }
                };
                VHttp.getInstance(context).addToRequestQueue(jsObjRequest);
            }
        });
    }
}
