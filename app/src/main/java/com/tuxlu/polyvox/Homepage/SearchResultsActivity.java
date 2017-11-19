package com.tuxlu.polyvox.Homepage;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class SearchResultsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    PagerAdapter adapter;

    SearchView searchView= null;
    List<Fragment> fragments = null;
    Bundle args = new Bundle();
    @BindView(R.id.pager) ViewPager pager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL); //
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        handleIntent(getIntent());
    }

    private void configToolbar() {
        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            appBar.setExpanded(false,false);
            bar.setTitle(R.string.app_name);
        }
    }


    public boolean onQueryTextChange(String query) {
        SearchRoomRecycler recycler = (SearchRoomRecycler) fragments.get(0);
        if (query.isEmpty()) {
            recycler.clear();
        }
        else {
            recycler.search(query);
        }
        return false;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        final MenuItem profileIcon = menu.findItem(R.id.profileButton);
        profileIcon.setVisible(false);

        final MenuItem searchItem = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.requestFocus();
        searchView.setSubmitButtonEnabled(true);

        //enlève icône de recherche sur la searchBar
        ImageView magImage = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        magImage.setVisibility(View.GONE);
        magImage.setImageDrawable(null);
        searchItem.setOnActionExpandListener(new SearchBarExpander(searchItem, menu, this));


        /*TODO: décommenter cette ligne active la recherche dès qu'on tape,
        mais ne marche bien que si on lance directement une nouvelle activité,
        quelques problemes dessus.
        d_s */
        //searchItem.expandActionView();
        //searchView.setOnQueryTextListener(this);
        return true;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query.isEmpty())
                return;
            if (fragments == null) {
                fragments = new Vector<>();
                Bundle args = new Bundle();
                args.putString("query", query);
                fragments.add(Fragment.instantiate(this, SearchRoomRecycler.class.getName(), args));
                int[] tabTitles = new int[]{R.string.tab_discover,
                        R.string.tab_friends, R.string.tab_chat};
                adapter = new PagerAdapter(getSupportFragmentManager(), fragments, tabTitles, this);
                pager.setAdapter(adapter);
                //((TabLayout) findViewById(R.id.tabLayout)).setupWithViewPager(pager);
            }

            else {
                SearchRoomRecycler recycler = (SearchRoomRecycler) fragments.get(0);
                recycler.search(query);
            }

        }
    }
}