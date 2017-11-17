package com.tuxlu.polyvox.User;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tuxlu.polyvox.R;
import com.tuxlu.polyvox.Utils.APIJsonObjectRequest;
import com.tuxlu.polyvox.Utils.APIUrl;
import com.tuxlu.polyvox.Utils.VHttp;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

import static com.tuxlu.polyvox.Utils.NetworkUtils.getParametrizedUrl;

/**
 * Created by tuxlu on 16/09/17.
 */

public class Login extends AccountAuthenticatorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    private void displayError(JSONException e, TextInputLayout loginLayout)
    {
        e.printStackTrace();
        loginLayout.setError(getString(R.string.unknown_error));
    }

    public void onLoginClick(View buttonView)
    {

        View v = buttonView.getRootView();
        final TextInputLayout loginLayout = (TextInputLayout)v.findViewById(R.id.LoginIDLayout);
        final String login = ((TextInputEditText) loginLayout.findViewById(R.id.LoginIDInput)).getText().toString();
        final String password = ((TextInputEditText)v.findViewById(R.id.LoginPasswordInput)).getText().toString();
        if (login.isEmpty() || password.isEmpty())
            return;

        //Drawable d = ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_email);

        HashMap params = new HashMap();
        params.put(APIUrl.LOGIN_PARAM1, login);
        params.put(APIUrl.LOGIN_PARAM2, password);
        String url = getParametrizedUrl(APIUrl.LOGIN, params);

        JsonObjectRequest jsObjRequest = new APIJsonObjectRequest
                (Request.Method.POST, url, new JSONObject(), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String token;
                        //String refreshToken;
                        try {
                        token = response.getString(APIUrl.COOKIE_HEADER);
                        //refreshToken = response.getString("refreshToken");
                        }
                        catch (JSONException e) {
                            displayError(e, loginLayout);
                            return;
                        }

                        Account account = new Account(login, getString(R.string.account_type));
                        AccountManager am = AccountManager.get(getBaseContext());
                        am.addAccountExplicitly(account, null, null);
                        //am.setUserData(account, "refreshToken", refreshToken);
                        am.setAuthToken(account, getString(R.string.account_type), token);
                        //setResult(RESULT_OK); si utilisation startActivityForResult()
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.statusCode == 400) {
                            loginLayout.setError(getString(R.string.login_incorrect_credentials));
                            findViewById(R.id.LoginConnectionProblemButton).setVisibility(View.VISIBLE);
                        }
                        else
                            loginLayout.setError(getString(R.string.no_network));
                    }
                });
        VHttp.getInstance(v.getContext()).addToRequestQueue(jsObjRequest);

    }

    public void onLoginProblemClick(View v)
    {

    }

    public void onCreateAccountClick(View v)
    {

    }


}