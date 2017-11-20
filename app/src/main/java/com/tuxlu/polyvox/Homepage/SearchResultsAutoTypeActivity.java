package com.tuxlu.polyvox.Homepage;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.tuxlu.polyvox.R;

import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by parsyl on 24/07/2017.
 */

public class SearchResultsAutoTypeActivity extends ISearchResult implements SearchView.OnQueryTextListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((SearchView)this.findViewById(R.id.searchView)).setOnQueryTextListener(this);
        //for search while typing.
    }


    public boolean onQueryTextChange(String query) {
        search(query);
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}