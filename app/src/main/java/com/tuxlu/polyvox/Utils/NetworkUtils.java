package com.tuxlu.polyvox.Utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tuxlu.polyvox.R;
import com.tuxlu.polyvox.User.Login;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by parsyl on 19/07/2017.
 */


public class NetworkUtils {

    public static String getParametrizedUrl(String path, HashMap<String, String> params) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(APIUrl.SCHEME);
        builder.authority(APIUrl.AUTHORITY);
        String[] items = path.split("/");
        for (String item : items)
            builder.appendPath(item);
        for (Map.Entry<String, String> entry : params.entrySet())
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        return builder.build().toString();
    }


    public static void removeAccountLogout(Context context) {
        AccountManager am = AccountManager.get(context);
        if (android.os.Build.VERSION.SDK_INT >= 22) {
            am.removeAccountExplicitly(am.getAccounts()[0]);
        } else {
            am.removeAccount(am.getAccounts()[0], null, null);
        }
    }

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
        if (res == null)
            return null;
        return res.get(AccountManager.KEY_AUTHTOKEN).toString();
    }


    public static void startLoginActivity(Context context) {
        context.startActivity(new Intent(context, Login.class));
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
                final String token = usesApi ? getToken(context) : null;
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (method, url, body, listener, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error.networkResponse != null) {
                                    if (error.networkResponse.statusCode == APIUrl.TOKEN_DENIED_CODE) {
                                        removeAccountLogout(context);
                                        startLoginActivity(context);
                                        return;
                                    }
                                    if (error.networkResponse.statusCode == APIUrl.USER_NOT_VALIDATED) {
                                        AlertDialog.Builder build = new AlertDialog.Builder(context);
                                        build.setTitle(context.getString(R.string.not_verified_title))
                                                .setMessage(context.getString(R.string.not_verified_message))
                                                .setPositiveButton(context.getString(R.string.not_verified_resend), new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        UtilsTemp.sendMail(context);
                                                    }
                                                })
                                                .setNegativeButton(context.getString(R.string.not_verified_nope), null)
                                                .show();
                                    }
                                }
                                UtilsTemp.checkNetworkError(context, error);
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
}
