package com.tuxlu.polyvox.Utils.API;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tuxlu.polyvox.BuildConfig;
import com.tuxlu.polyvox.R;
import com.tuxlu.polyvox.User.Login;
import com.tuxlu.polyvox.Utils.Auth.AuthUtils;
import com.tuxlu.polyvox.Utils.NetworkLibraries.VHttp;
import com.tuxlu.polyvox.Utils.NetworkLibraries.VolleyMultipartRequest;
import com.tuxlu.polyvox.Utils.NetworkUtils;
import com.tuxlu.polyvox.Utils.ToastType;
import com.tuxlu.polyvox.Utils.UtilsTemp;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tuxlu on 19/12/17.
 */

public class APIRequest {


    public static boolean checkConnection(Context context, boolean usesApi) {
        if (!NetworkUtils.isConnected(context)) {
            UtilsTemp.showToast(context, context.getString(R.string.no_network));
            return false;
        }

        if (usesApi && !AuthUtils.hasAccount(context)) {
            startLoginActivity(context);
            return false;
        }
        return true;
    }

    public static void JSONrequest(final Context context, final int method, final String url,
                                   final boolean usesApi, final JSONObject body,
                                   final Response.Listener<JSONObject> listener,
                                   final Response.ErrorListener errorListener) {
        if (!checkConnection(context, usesApi))
            return;
        AsyncTask.execute(() -> {
                final String token = usesApi ? AuthUtils.getToken(context) : null;
                APIJsonObjectRequest jsObjRequest = new APIJsonObjectRequest(method, url, body, listener, errorListener, token, context);
                addRequestToQueue(context, jsObjRequest);
            });
    }


    public static void Multipartequest(final Context context, final int method, final String url,
                                       final boolean usesApi, final List<VolleyMultipartRequest.DataPart> body,
                                       final Response.Listener<JSONObject> listener,
                                       final Response.ErrorListener errorListener) {

        if (!checkConnection(context, usesApi))
            return;
        AsyncTask.execute(() -> {
                final String token = usesApi ? AuthUtils.getToken(context) : null;
                VolleyMultipartRequest request = new VolleyMultipartRequest(method, url, body, listener, errorListener, token, context);
                addRequestToQueue(context, request);
            });
    }


    public static void addRequestToQueue(final Context context, final APIJsonObjectRequest request) {
        //avoid timeout
        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VHttp.getInstance(context.getApplicationContext()).addToRequestQueue(request);
    }

    public static void startLoginActivity(Context context) {
        Intent intent = new Intent(context, Login.class);
        if (!(context instanceof Activity))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (context instanceof Activity)
            ((Activity)context).startActivityForResult(intent, AuthUtils.AUTH_REQUEST_CODE);
        else
            context.startActivity(intent);
    }


    static public class APIJsonObjectRequest extends JsonObjectRequest {
        private String token;

        public APIJsonObjectRequest(int method, String url, JSONObject jsonRequest,
                                    Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener, String nToken, final Context context) {

            super(method, url, jsonRequest, listener, error -> {
                if (error.networkResponse != null) {
                    if (error.networkResponse.statusCode == APIUrl.TOKEN_DENIED_CODE) {
                        AuthUtils.removeAccountLogout(context);
                        startLoginActivity(context);
                        UtilsTemp.showToast(context, context.getString(R.string.logout_been));
                        return;
                    }
                    if (error.networkResponse.statusCode == 418) {
                        AuthUtils.removeAccountLogout(context);
                        UtilsTemp.showToast(context, context.getString(R.string.banned), ToastType.ERROR);
                        return;
                    }
                    String data = null;

                    try {
                        data = new String(error.networkResponse.data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if (error.networkResponse.statusCode == APIUrl.USER_NOT_VALIDATED &&
                            data.contains("user_not_validated")) {
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

                if (BuildConfig.DEBUG) {
                    error.printStackTrace();
                    if (error.networkResponse != null) {
                        NetworkResponse resp = error.networkResponse;
                        String data = new String(resp.data);
                        Log.wtf("NETWORK", "code = " + resp.statusCode +
                                "\n data=" + data);
                    }
                }

                if (errorListener != null)
                    errorListener.onErrorResponse(error);
            });
            token = nToken;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            if (token == null)
                return super.getHeaders();
            Map<String, String> params = new HashMap<>();
            params.put(APIUrl.COOKIE_HEADER, token);
            return params;
        }
    }

}
