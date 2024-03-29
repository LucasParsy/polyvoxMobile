package com.tuxlu.polyvox.User;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tuxlu.polyvox.R;
import com.tuxlu.polyvox.Utils.API.APIRequest;
import com.tuxlu.polyvox.Utils.API.APIUrl;
import com.tuxlu.polyvox.Utils.Auth.AuthRequest;
import com.tuxlu.polyvox.Utils.Auth.AuthUtils;
import com.tuxlu.polyvox.Utils.MyDateUtils;
import com.tuxlu.polyvox.Utils.NetworkLibraries.VHttp;
import com.tuxlu.polyvox.Utils.NetworkUtils;
import com.tuxlu.polyvox.Utils.ToastType;
import com.tuxlu.polyvox.Utils.UIElements.SimpleWebView;
import com.tuxlu.polyvox.Utils.UtilsTemp;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.tuxlu.polyvox.Utils.InputFieldsVerifier.checkMail;
import static com.tuxlu.polyvox.Utils.InputFieldsVerifier.checkPassword;

/**
 * Created by tuxlu on 16/09/17.
 */


public class Register extends AppCompatActivity {
    private TextInputEditText ID;
    private TextInputEditText mail;
    private TextInputEditText pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        MyDateUtils.setDateSpinners(findViewById(R.id.LayoutCreateAccountActivity), this);

        ID = findViewById(R.id.RegisterIDInput);
        mail = findViewById(R.id.RegisterEmailInput);
        pass = findViewById(R.id.passwordInput);

        ID.setOnFocusChangeListener(new RegisterHintFocus(findViewById(R.id.RegisterIDHint), findViewById(R.id.RegisterIDLayout)));
        mail.setOnFocusChangeListener(new RegisterHintFocus(findViewById(R.id.RegisterMailHint), findViewById(R.id.RegisterEmailLayout)));
        pass.setOnFocusChangeListener(new RegisterHintFocus(findViewById(R.id.passHint), findViewById(R.id.passwordLayout)));

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


    private Date checkInput(String IDText, String mailText, String passText) {

        TextView CGUHint = findViewById(R.id.CGUButton);
        Boolean CGUAccepted = ((CheckBox) findViewById(R.id.CGUCheck)).isChecked();

        if (!checkMail(findViewById(R.id.RegisterEmailLayout), this))
            return null;

        if (!IDText.matches("^[a-zA-Z0-9].{5,}$")) {
            ((TextInputLayout) findViewById(R.id.RegisterIDLayout)).setError(getString(R.string.register_username_error));
            findViewById(R.id.RegisterIDHint).setVisibility(View.GONE);
            return null;
        }

        if (!checkPassword(findViewById(R.id.passwordLayout), this))
            return null;

        if (!CGUAccepted) {
            CGUHint.setError(getString(R.string.register_cgu_error));
            return null;
        } else
            CGUHint.setError(null);

        Date date = MyDateUtils.checkDate(findViewById(R.id.LayoutCreateAccountActivity), getString(R.string.register_dob_error));
        if (date == null)
            return null;
        return date;
    }

    public void onRegisterClick(View buttonView) {

        final Button button = findViewById(R.id.RegisterButton);

        String IDText = ID.getText().toString();
        final String mailText = mail.getText().toString();
        final String passText = pass.getText().toString();
        Date date = checkInput(IDText, mailText, passText);

        if (date == null)
            return;

        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        String textDate = ft.format(date);

        JSONObject req = new JSONObject();
        try {
            req.put(APIUrl.REGISTER_PARAM1, mailText);
            req.put(APIUrl.REGISTER_PARAM2, passText);
            req.put(APIUrl.REGISTER_PARAM3, IDText);
            req.put(APIUrl.REGISTER_PARAM4, textDate);
            req.put(APIUrl.REGISTER_PARAM5, true);
        } catch (JSONException e) {
            return;
        }

        button.setText(getString(R.string.register_sending));
        Context context = getApplicationContext();

        AuthRequest jsObjRequest = new AuthRequest(context, IDText, Request.Method.POST, APIUrl.BASE_URL + APIUrl.REGISTER, req,
                response -> {
                    button.setText(getString(R.string.create_account));
                    NetworkUtils.sendMail(getApplicationContext(), r -> endActivity());
                    UtilsTemp.showToast(context,getString(R.string.account_created), ToastType.SUCCESS);
                    endActivity();
                }, error -> {
            TextInputLayout mail = findViewById(R.id.RegisterEmailLayout);
            TextInputLayout id = findViewById(R.id.RegisterIDLayout);
            findViewById(R.id.RegisterIDHint).setVisibility(View.GONE);
            button.setText(getString(R.string.create_account));
            if (error.networkResponse != null && error.networkResponse.statusCode == APIUrl.REGISTER_ERROR_CODE) {
                String errData = new String(error.networkResponse.data);
                id.clearFocus();
                if (errData.contains(APIUrl.REGISTER_MAIL_ERROR)) {
                    findViewById(R.id.RegisterMailHint).setVisibility(View.GONE);
                    mail.setError(getString(R.string.register_mail_already_used));
                } else if (errData.contains(APIUrl.REGISTER_LOGIN_ERROR))
                    id.setError(getString(R.string.register_ID_already_used));
                else
                    id.setError(getString(R.string.unknown_error));
            } else {
                NetworkUtils.checkNetworkError(getApplicationContext(), error);
                findViewById(R.id.RegisterIDHint).setVisibility(View.GONE);
                id.setError(getString(R.string.no_network));
            }
        });
        VHttp.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
    }


    public void onCGUButtonClick(View v) {
       /*
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.util_dialog_cgu);
        dialog.setTitle(getResources().getString(R.string.CGU));
        dialog.show();
        */
        Intent webIntent = new Intent(this, SimpleWebView.class);
        webIntent.putExtra("title", R.string.CGU);
        webIntent.putExtra("url", APIUrl.CGU);
        startActivity(webIntent);
    }

    private void endActivity() {
        /*
        Intent intent = new Intent(this, RegisterSuccessful.class);
        intent.putExtra("mail", mail);
        startActivity(intent);
        */
        //UtilsTemp.showToast(this, getResources().getString(R.string.account_created), ToastType.SUCCESS);
        setResult(42);
        finish();
    }


    public static class RegisterHintFocus implements View.OnFocusChangeListener {
        private View hint;
        private TextInputLayout upperLay;

        public RegisterHintFocus(View nhint, View nUpper) {
            hint = nhint;
            upperLay = (TextInputLayout) nUpper;
        }

        @Override
        public void onFocusChange(android.view.View view, boolean hasFocus) {
            hint.setVisibility(hasFocus ? android.view.View.VISIBLE : android.view.View.GONE);
            upperLay.setError(null);
        }
    }
}