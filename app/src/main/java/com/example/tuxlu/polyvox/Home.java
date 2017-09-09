package com.example.tuxlu.polyvox;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.List;
import java.util.Vector;


public class Home extends AppCompatActivity {

    Discover homepage;
    PagerAdapter adapter;
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        configToolbar();


        //((ProgressBar)findViewById(R.id.progressBar)).getIndeterminateDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

        List fragments = new Vector();
        fragments.add(Fragment.instantiate(this,Discover.class.getName())); //discover
        fragments.add(Fragment.instantiate(this,Discover.class.getName())); //amis
        fragments.add(Fragment.instantiate(this,PrivateChatList.class.getName())); //chat
        int[] tabTitles = new int[]{R.string.tab_discover,
                                    R.string.tab_friends, R.string.tab_chat};

        adapter = new PagerAdapter(getSupportFragmentManager(), fragments, tabTitles);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);


        ((TabLayout) findViewById(R.id.tabLayoutHome)).setupWithViewPager(pager);
    }

    private void configToolbar()
    {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    /*
    Toolbar configuration
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        //enlève icône de recherche sur la searchBar
        ImageView magImage = (ImageView)searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        magImage.setVisibility(View.GONE);
        magImage.setImageDrawable(null);

        final MenuItem searchItem = menu.findItem(R.id.search);
        MenuItemCompat.setOnActionExpandListener(searchItem, new SearchBarExpander(searchItem, menu));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }




    public class PagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments;
        int[] tabTitles;

        public PagerAdapter(FragmentManager fm, List nfragments, int[] ntabTitles) {
            super(fm);
            this.fragments = nfragments;
            this.tabTitles = ntabTitles;
        }

        public CharSequence getPageTitle(int position) {
            return getResources().getString(tabTitles[position]);
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }

    }




}
