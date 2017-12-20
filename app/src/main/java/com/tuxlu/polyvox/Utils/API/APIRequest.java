package com.tuxlu.polyvox.Utils.API;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tuxlu.polyvox.R;
import com.tuxlu.polyvox.User.Login;
import com.tuxlu.polyvox.Utils.Auth.AuthUtils;
import com.tuxlu.polyvox.Utils.NetworkLibraries.VHttp;
import com.tuxlu.polyvox.Utils.NetworkUtils;
import com.tuxlu.polyvox.Utils.UtilsTemp;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tuxlu on 19/12/17.
 */

public class APIRequest {


    public static boolean isAPIConnected(Context context) {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccounts();
        return (accounts.length != 0);
    }

    public static void JSONrequest(final Context context, final int method, final String url,
                                   final boolean usesApi, final JSONObject body,
                                   final Response.Listener<JSONObject> listener,
                                   final Response.ErrorListener errorListener) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (usesApi && !isAPIConnected(context)) {
                    startLoginActivity(context);
                    return;
                }
                final String token = usesApi ? AuthUtils.getToken(context) : null;
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (method, url, body, listener, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error.networkResponse != null) {
                                    if (error.networkResponse.statusCode == APIUrl.TOKEN_DENIED_CODE) {
                                        AuthUtils.removeAccountLogout(context);
                                        startLoginActivity(context);
                                        return;
                                    }
                                    if (error.networkResponse.statusCode == APIUrl.USER_NOT_VALIDATED) {
                                        AlertDialog.Builder build = new AlertDialog.Builder(context);
                                        build.setTitle(context.getString(R.string.not_verified_title))
                                                .setMessage(context.getString(R.string.not_verified_message))
                                                .setPositiveButton(context.getString(R.string.not_verified_resend), new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        NetworkUtils.sendMail(context);
                                                    }
                                                })
                                                .setNegativeButton(context.getString(R.string.not_verified_nope), null)
                                                .show();
                                    }
                                }
                                NetworkUtils.checkNetworkError(context, error);
                                errorListener.onErrorResponse(error);
                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        if (!usesApi)
                            return super.getHeaders();
                        Map<String, String> params = new HashMap<>();
                        params.put(APIUrl.COOKIE_HEADER, token);
                        return params;
                    }
                };

                //avoid timeout
                jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                        15000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                VHttp.getInstance(context.getApplicationContext()).addToRequestQueue(jsObjRequest);
            }
        });
    }

    public static void startLoginActivity(Context context) {
        context.startActivity(new Intent(context, Login.class));
    }
}
