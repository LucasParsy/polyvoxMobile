package com.tuxlu.polyvox.User;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import java.util.Calendar;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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

import java.util.ArrayList;

/**
 * Created by tuxlu on 16/09/17.
 */



public class Register extends AppCompatActivity {
    private TextInputEditText ID;
    private TextInputEditText mail;
    private TextInputEditText pass;


    //todo: ajouter erreur si champs incorrects avant upload.
    //todo: gérer erreurs pour CGU et date naissance.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setSpinners();

        ID = findViewById(R.id.RegisterIDInput);
        mail = findViewById(R.id.RegisterEmailInput);
        pass = findViewById(R.id.RegisterPasswordInput);

        ID.setOnFocusChangeListener(new RegisterHintFocus(findViewById(R.id.RegisterIDHint)));
        mail.setOnFocusChangeListener(new RegisterHintFocus(findViewById(R.id.RegisterMailHint)));
        pass.setOnFocusChangeListener(new RegisterHintFocus(findViewById(R.id.RegisterPassHint)));

        //lastPass Integration
        if (android.os.Build.VERSION.SDK_INT >= 26) {

            ID.setAutofillHints(View.AUTOFILL_HINT_USERNAME);
            mail.setAutofillHints(View.AUTOFILL_HINT_EMAIL_ADDRESS);
            pass.setAutofillHints(View.AUTOFILL_HINT_PASSWORD);
            ID.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_YES);
            mail.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_YES);
            pass.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_YES);

            /*
            final TextInputEditText confirmPass = findViewById(R.id.RegisterPasswordConfirmInput);
            confirmPass.setAutofillHints(View.AUTOFILL_HINT_PASSWORD);
            confirmPass.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_YES);
            */
        }
    }

    private void setSpinners() {
        Spinner spinMonth = findViewById(R.id.spinnerMonth);
        ArrayAdapter<String> adapterMonth = new ArrayAdapter<String>(this,
                R.layout.spinner_item, getResources().getStringArray(R.array.months_spinner));
        spinMonth.setAdapter(adapterMonth);

        Spinner spinDays = findViewById(R.id.spinnerDays);
        Spinner spinYears = (Spinner) findViewById(R.id.spinnerYear);

        ArrayList<String> years = new ArrayList<String>();
        years.add(getResources().getString(R.string.year));
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 1900; i--) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(this, R.layout.spinner_item, years);
        spinYears.setAdapter(adapterYear);

        ArrayList<String> days = new ArrayList<String>();
        days.add(getResources().getString(R.string.day));
        for (int i = 0; i <= 31; i++) {
            days.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapterDays = new ArrayAdapter<String>(this, R.layout.spinner_item, days);
        spinDays.setAdapter(adapterDays);
    }

    private void displayError(JSONException e, TextInputLayout loginLayout)
    {
        e.printStackTrace();
        loginLayout.setError(getString(R.string.unknown_error));
    }

    public void onRegisterClick(View buttonView)
    {

        View v = buttonView.getRootView();
        final TextInputLayout loginLayout = (TextInputLayout)v.findViewById(R.id.LoginIDLayout);
        final String login = ((TextInputEditText) loginLayout.findViewById(R.id.LoginIDInput)).getText().toString();

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


        JsonObjectRequest jsObjRequest = new APIJsonObjectRequest
                (Request.Method.POST, APIUrl.BASE_URL + APIUrl.LOGIN, req, new Response.Listener<JSONObject>() {

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
        VHttp.getInstance(v.getContext().getApplicationContext()).addToRequestQueue(jsObjRequest);

    }

    
    public void onCGUButtonClick(View v)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_cgu);
        dialog.setTitle(getResources().getString(R.string.CGU));
        dialog.show();


    /*
        Intent webIntent = new Intent(this, SimpleWebView.class);
        webIntent.putExtra("title", R.string.CGU);
        webIntent.putExtra("url", APIUrl.CGU);
        startActivity(webIntent);
     */
    }


    private class RegisterHintFocus implements View.OnFocusChangeListener {
        private  View hint;

        public RegisterHintFocus(View nhint) {hint = nhint;}

        @Override
        public void onFocusChange(android.view.View view, boolean hasFocus) {
            hint.setVisibility(hasFocus ? android.view.View.VISIBLE : android.view.View.GONE);
        }
    }
}