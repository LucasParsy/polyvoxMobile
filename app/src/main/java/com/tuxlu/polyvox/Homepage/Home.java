package com.tuxlu.polyvox.Homepage;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.squareup.leakcanary.LeakCanary;
import com.tuxlu.polyvox.R;
import com.tuxlu.polyvox.Utils.APIUrl;
import com.tuxlu.polyvox.Utils.NetworkUtils;
import com.tuxlu.polyvox.Utils.VHttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Home extends AppCompatActivity {

    private static final String TAG = "Home";
    PagerAdapter adapter;
    @BindView(R.id.pager) ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        configToolbar();

        //leaks verification, in debug builds only.
        if (LeakCanary.isInAnalyzerProcess(this)) {return;}
        LeakCanary.install(this.getApplication());

        //((ProgressBar)findViewById(R.id.progressBar)).getIndeterminateDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

        List<Fragment> fragments = new Vector<>();
        fragments.add(Fragment.instantiate(this, Discover.class.getName())); //discover
        fragments.add(Fragment.instantiate(this, Discover.class.getName())); //amis
        fragments.add(Fragment.instantiate(this, PrivateChatList.class.getName())); //chat
        int[] tabTitles = new int[]{R.string.tab_discover,
                R.string.tab_friends, R.string.tab_chat};

        adapter = new PagerAdapter(getSupportFragmentManager(), fragments, tabTitles);
        pager.setAdapter(adapter);


        ((TabLayout) findViewById(R.id.tabLayoutHome)).setupWithViewPager(pager);
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

    /*
    Toolbar configuration
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        final MenuItem profileIcon = menu.findItem(R.id.profileButton);
        profileIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override

            //TODO stupid request used to test Auth Token requests. To be modified.
            //ex: replace image getter by settings menu opener. put image Getter somewhere else.
            public boolean onMenuItemClick(MenuItem item) {
                final Context context = getBaseContext();
                JSONObject req = new JSONObject();
                try {
                    req.put("query", 42);
                } catch (JSONException e) {
                    return true;
                }
                NetworkUtils.JSONrequest(context, Request.Method.GET,
                        APIUrl.BASE_URL + APIUrl.INFO_USER,
                        true, req, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                String imageUrl;
                                try {
                                    imageUrl = response.getString("picture");
                                } catch (JSONException e) {
                                    return;
                                }
                                ImageRequest request = new ImageRequest(imageUrl,
                                        new Response.Listener<Bitmap>() {
                                            @Override
                                            public void onResponse(Bitmap bitmap) {
                                                profileIcon.setIcon(new BitmapDrawable(getResources(), bitmap));
                                            }
                                        }, 0, 0, null, null, null);
                                VHttp.getInstance(context).addToRequestQueue(request);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
                return true;
            }
        });
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        //enlève icône de recherche sur la searchBar
        ImageView magImage = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
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
        final int[] tabTitles;

        public PagerAdapter(FragmentManager fm, List<Fragment> nfragments, int[] ntabTitles) {
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
