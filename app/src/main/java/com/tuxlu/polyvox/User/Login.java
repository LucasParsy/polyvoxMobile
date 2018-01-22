package com.tuxlu.polyvox.User;

import android.accounts.AccountAuthenticatorActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tuxlu.polyvox.R;
import com.tuxlu.polyvox.Utils.API.APIUrl;
import com.tuxlu.polyvox.Utils.Auth.AuthRequest;
import com.tuxlu.polyvox.Utils.NetworkLibraries.VHttp;
import com.tuxlu.polyvox.Utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tuxlu on 16/09/17.
 */

public class Login extends AccountAuthenticatorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar bar = findViewById(R.id.LoginToolbar);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            setActionBar(bar);
            bar.setTitleTextColor(Color.WHITE);
        }
        else
            bar.setVisibility(View.GONE);

        //lastPass Integration
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            View login = findViewById(R.id.LoginIDInput);
            View pass = findViewById(R.id.LoginPasswordInput);
            login.setAutofillHints(View.AUTOFILL_HINT_EMAIL_ADDRESS);
            pass.setAutofillHints(View.AUTOFILL_HINT_PASSWORD);
            login.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_YES);
            pass.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_YES);
        }
    }

    private void displayError(JSONException e, TextInputLayout loginLayout)
    {
        e.printStackTrace();
        loginLayout.setError(getString(R.string.unknown_error));
    }

    public void onLoginClick(View buttonView)
    {

        View v = buttonView.getRootView();
        final TextInputLayout loginLayout = v.findViewById(R.id.LoginIDLayout);
        final TextInputEditText loginInput = loginLayout.findViewById(R.id.LoginIDInput);

        loginInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    loginLayout.setErrorEnabled(false);
            }
        }});

        final String login = loginInput.getText().toString();
        final String password = ((TextInputEditText)v.findViewById(R.id.LoginPasswordInput)).getText().toString();
        if (login.isEmpty() || password.isEmpty())
            return;

        JSONObject req = new JSONObject();
        try {
            req.put(APIUrl.LOGIN_PARAM1, login);
            req.put(APIUrl.LOGIN_PARAM2, password);
        }
        catch (JSONException e) {
            displayError(e, loginLayout);
            return;
        }
        //Drawable d = ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_email);


        final Button button = ((Button)buttonView);
        button.setText(getString(R.string.login_connect_waiting));
        JsonObjectRequest jsObjRequest = new AuthRequest
                (getApplicationContext(), login, Request.Method.POST, APIUrl.BASE_URL + APIUrl.LOGIN, req, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        setResult(RESULT_OK); //si utilisation startActivityForResult()
                        button.setText(getString(R.string.login_connect));
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        button.setText(getString(R.string.login_connect));
                        NetworkResponse networkResponse = error.networkResponse;
                        loginLayout.setError(error.getMessage());
                        if (networkResponse != null && networkResponse.statusCode == APIUrl.LOGIN_INVALID_USER_CODE) {
                            loginLayout.setError(getString(R.string.login_incorrect_credentials));
                            findViewById(R.id.LoginConnectionProblemButton).setVisibility(View.VISIBLE);
                        }
                        else {
                            NetworkUtils.checkNetworkError(getApplicationContext(), error);
                            loginLayout.setError(getString(R.string.no_network));
                        }
                    }
                });
        VHttp.getInstance(v.getContext().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public void onLoginProblemClick(View v)
    {
        startActivity(new Intent(this, ForgotPassword.class));
    }

    public void onCreateAccountClick(View v)
    {
        startActivity(new Intent(this, Register.class));
    }


}