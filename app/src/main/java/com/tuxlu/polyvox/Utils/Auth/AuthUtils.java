package com.tuxlu.polyvox.Utils.Auth;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.tuxlu.polyvox.Homepage.Home;
import com.tuxlu.polyvox.R;
import com.tuxlu.polyvox.Utils.API.APIRequest;
import com.tuxlu.polyvox.Utils.API.APIUrl;

import org.json.JSONObject;

/**
 * Created by tuxlu on 19/12/17.
 */

public class AuthUtils {

    public static final int AUTH_REQUEST_CODE = 2816;

    public static void logout(final Context context)
    {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.logout))
                .setMessage(context.getString(R.string.logout_confirm))
                .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        APIRequest.JSONrequest(context, Request.Method.GET,
                                APIUrl.BASE_URL + APIUrl.LOGOUT,
                                true, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        AuthUtils.removeAccountLogout(context);
                                        Intent nin = new Intent(context, Home.class);
                                        nin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(nin);
                                    }
                                }, null);
                    }})
                .setNegativeButton(context.getString(R.string.no), null)
            .show();
    }

    public static void removeAccountLogout(Context context) {
        AccountManager am = AccountManager.get(context);
        if (android.os.Build.VERSION.SDK_INT >= 22) {
            am.removeAccountExplicitly(am.getAccounts()[0]);
        } else {
            am.removeAccount(am.getAccounts()[0], null, null);
        }
    }

    public static String getUsername(Context context)
    {
        AccountManager am = AccountManager.get(context);
        if (am.getAccounts().length == 0)
            return "";
        return am.getUserData(am.getAccounts()[0], "name");
    }

    public static String getPictureUrl(Context context)
    {
        AccountManager am = AccountManager.get(context);
        if (am.getAccounts().length == 0)
            return "";
        return am.getUserData(am.getAccounts()[0], "picture");
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
        Object acc = res.get(AccountManager.KEY_AUTHTOKEN);
        if (acc != null)
            return acc.toString();
        return null;
    }
}
