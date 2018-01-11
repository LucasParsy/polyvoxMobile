package com.tuxlu.polyvox.Utils.UIElements;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;

import com.tuxlu.polyvox.R;

/**
 * Created by tuxlu on 06/01/18.
 */

public abstract class DialogFragmentBase extends DialogFragment {

    private String oldName = null;
    private View rootView = null;
    protected String name;
    protected String url;

    protected View setupLayout(LayoutInflater inflater, ViewGroup container, int layout)
    {
        name = getArguments().getString("name");
        url = getArguments().getString("url");


        rootView = inflater.inflate(layout, container, false);
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (getActivity() instanceof MyAppCompatActivity)
        {
            oldName = actionBar.getTitle().toString();
            actionBar.setTitle(name);
            ((View)toolbar).setVisibility(View.GONE);
            return rootView;
        }

        toolbar.setTitle(name);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        setHasOptionsMenu(true);
        return rootView;
    }

    protected void setFavorite(String url, String name) {
        ImageButton fav = rootView.findViewById(R.id.favorite);
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo: set favorite listener when API decided
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (oldName != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(oldName);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        //getActivity().getMenuInflater().inflate(R.menu.menu_ak, menu);
    }



    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            dismiss();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
