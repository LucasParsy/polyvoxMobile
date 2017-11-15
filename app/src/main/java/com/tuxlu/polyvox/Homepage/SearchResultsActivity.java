package com.tuxlu.polyvox.Homepage;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.tuxlu.polyvox.R;

import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by parsyl on 24/07/2017.
 */

public class SearchResultsActivity extends AppCompatActivity {
    PagerAdapter adapter;
    @BindView(R.id.pager) ViewPager pager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        handleIntent(getIntent());
        int[] tabTitles = new int[]{R.string.tab_discover,
                R.string.tab_friends, R.string.tab_chat};

        ((TabLayout) findViewById(R.id.tabLayout)).setupWithViewPager(pager);
    }

    private void configToolbar() {
        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            appBar.setExpanded(true,true);
            bar.setTitle(R.string.app_name);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);


            List<Fragment> fragments = new Vector<>();
            Bundle args = new Bundle();
            args.putString("query", query);
            fragments.add(Fragment.instantiate(this, SearchRoomRecycler.class.getName(), args));
            int[] tabTitles = new int[]{R.string.tab_discover,
                    R.string.tab_friends, R.string.tab_chat};
            adapter = new PagerAdapter(getSupportFragmentManager(), fragments, tabTitles, this);
            pager.setAdapter(adapter);
            ((TabLayout) findViewById(R.id.tabLayout)).setupWithViewPager(pager);
        }
    }
}