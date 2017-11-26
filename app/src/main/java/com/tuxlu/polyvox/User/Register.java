package com.tuxlu.polyvox.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tuxlu.polyvox.R;
import com.tuxlu.polyvox.Utils.APILoginJsonObjectRequest;
import com.tuxlu.polyvox.Utils.APIUrl;
import com.tuxlu.polyvox.Utils.MyDateUtils;
import com.tuxlu.polyvox.Utils.VHttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

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
        setContentView(R.layout.activity_register);
        setSpinners();

        ID = findViewById(R.id.RegisterIDInput);
        mail = findViewById(R.id.RegisterEmailInput);
        pass = findViewById(R.id.RegisterPasswordInput);

        ID.setOnFocusChangeListener(new RegisterHintFocus(findViewById(R.id.RegisterIDHint), findViewById(R.id.RegisterIDLayout)));
        mail.setOnFocusChangeListener(new RegisterHintFocus(findViewById(R.id.RegisterMailHint), findViewById(R.id.RegisterEmailLayout)));
        pass.setOnFocusChangeListener(new RegisterHintFocus(findViewById(R.id.RegisterPassHint), findViewById(R.id.RegisterPasswordLayout)));

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

    private void displayError(JSONException e, TextInputLayout loginLayout) {
        e.printStackTrace();
        loginLayout.setError(getString(R.string.unknown_error));
    }


    private Date checkDate() {
        TextView hint = findViewById(R.id.RegisterDateHint);
        Spinner spinnerYear = findViewById(R.id.spinnerYear);
        int month = ((Spinner) findViewById(R.id.spinnerMonth)).getSelectedItemPosition() - 1;
        int day = ((Spinner) findViewById(R.id.spinnerDays)).getSelectedItemPosition();

        if (spinnerYear.getSelectedItemPosition() == 0 || month == -1 || day == 0) {
            hint.setError(getString(R.string.register_dob_error));
            return null;
        }

        int year = Integer.parseInt(spinnerYear.getSelectedItem().toString());

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        Calendar today = Calendar.getInstance();
        if (MyDateUtils.getDiffYears(cal, today) < 13) {
            hint.setError(getString(R.string.register_dob_error));
            return null;
        }
        hint.setError(null);
        Date dateRepresentation = cal.getTime();

        return dateRepresentation;
    }

    private void endActivity(String mail, String pass) {
        Intent intent = new Intent(this, RegisterSuccessful.class);
        intent.putExtra("mail", mail);
        startActivity(intent);
        finish();
    }

    private Date checkInput(String IDText, String mailText, String passText)
    {
        TextView CGUHint = findViewById(R.id.CGUButton);
        Boolean CGUAccepted = ((CheckBox) findViewById(R.id.CGUCheck)).isChecked();

        if (!mailText.contains("@")) {
            ((TextInputLayout) findViewById(R.id.RegisterEmailLayout)).setError(getString(R.string.register_mail_error));
            findViewById(R.id.RegisterMailHint).setVisibility(View.GONE);
            return null;
        }

        if (IDText.length() < 4) {
            ((TextInputLayout) findViewById(R.id.RegisterIDLayout)).setError(getString(R.string.register_username_error));
            findViewById(R.id.RegisterIDHint).setVisibility(View.GONE);
            return null;
        }

        if (!passText.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{5,42}$")) {
            ((TextInputLayout) findViewById(R.id.RegisterPasswordLayout)).setError(getString(R.string.register_password_error));
            findViewById(R.id.RegisterPassHint).setVisibility(View.GONE);
            return null;
        }

        if (!CGUAccepted) {
            CGUHint.setError(getString(R.string.register_cgu_error));
            return null;
        } else
            CGUHint.setError(null);

        Date date = checkDate();
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
            Log.wtf("Register", e);
            return;
        }

        button.setText(getString(R.string.register_sending));


        JsonObjectRequest jsObjRequest = new APILoginJsonObjectRequest(getApplicationContext(), mailText, Request.Method.POST, APIUrl.BASE_URL + APIUrl.REGISTER, req,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        button.setText(getString(R.string.create_account));
                        endActivity(mailText, passText);
                        //la réponse
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        button.setText(getString(R.string.create_account));
                        if (error.networkResponse != null && error.networkResponse.statusCode == APIUrl.REGISTER_ERROR_CODE)
                        {
                            if (error.networkResponse.data.toString().contains(APIUrl.REGISTER_MAIL_ERROR))
                                ((TextInputLayout) findViewById(R.id.RegisterEmailLayout)).setError(getString(R.string.register_mail_already_used));
                            if (error.networkResponse.data.toString().contains(APIUrl.REGISTER_LOGIN_ERROR))
                                ((TextInputLayout) findViewById(R.id.RegisterIDLayout)).setError(getString(R.string.register_ID_already_used));
                        }
                        else
                            ((TextInputLayout) findViewById(R.id.RegisterEmailLayout)).setError(getString(R.string.no_network));
                    }
                }
        );
        VHttp.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
    }


    public void onCGUButtonClick(View v) {
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