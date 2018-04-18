package com.tuxlu.polyvox.Utils.Auth;

import android.accounts.Account;
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

    public static Account getAppAccount(final Context context, final AccountManager am)
    {
        return am.getAccountsByType(context.getString(R.string.account_type))[0];
    }

    public static boolean hasAccount(final Context context, final AccountManager am)
    {
        Account[] accs = am.getAccountsByType(context.getString(R.string.account_type));
        return (accs.length != 0);
    }

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
        if (!hasAccount(context, am))
            return;
        if (android.os.Build.VERSION.SDK_INT >= 22) {
            am.removeAccountExplicitly(getAppAccount(context, am));
        } else {
            am.removeAccount(getAppAccount(context, am), null, null);
        }
    }

    public static String getUsername(Context context)
    {
        AccountManager am = AccountManager.get(context);
        if (!hasAccount(context, am))
            return "";
        return am.getUserData(getAppAccount(context, am), "name");
    }

    public static String getPictureUrl(final Context context)
    {
        AccountManager am = AccountManager.get(context);
        if (!hasAccount(context, am))
            return "";
        String str = am.getUserData(getAppAccount(context, am), "picture");
        return str;
    }

    public static void setPictureUrl(final Context context, String url)
    {
        AccountManager am = AccountManager.get(context);
        if (!hasAccount(context, am))
            return;
        Account acc = getAppAccount(context, am);
        am.setUserData(acc, "picture", url);
    }

    public static String getToken(Context context) {
        AccountManager am = AccountManager.get(context);
        //should'nt happen
        if (!hasAccount(context, am))
            return null;
        Bundle res;
        try {
            res = am.getAuthToken(getAppAccount(context, am),
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
