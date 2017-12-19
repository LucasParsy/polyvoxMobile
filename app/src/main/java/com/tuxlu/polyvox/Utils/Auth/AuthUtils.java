package com.tuxlu.polyvox.Utils.Auth;

import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;

import com.tuxlu.polyvox.R;

/**
 * Created by tuxlu on 19/12/17.
 */

public class AuthUtils {


    public static void removeAccountLogout(Context context) {
        AccountManager am = AccountManager.get(context);
        if (android.os.Build.VERSION.SDK_INT >= 22) {
            am.removeAccountExplicitly(am.getAccounts()[0]);
        } else {
            am.removeAccount(am.getAccounts()[0], null, null);
        }
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
}
