package com.tuxlu.polyvox.Homepage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tuxlu.polyvox.R;
import com.tuxlu.polyvox.Search.SearchResultsActivity;
import com.tuxlu.polyvox.User.ProfilePage;
import com.tuxlu.polyvox.Utils.API.APIRequest;
import com.tuxlu.polyvox.Utils.API.APIUrl;
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Home extends AppCompatActivity {

    private static final String TAG = "Home";
    private String username = "";
    PagerAdapter adapter;
    @BindView(R.id.pager)
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        configToolbar();

        //leaks verification, in debug builds only.
        /*
        if (LeakCanary.isInAnalyzerProcess(this)) {return;}
        LeakCanary.install(this.getApplication());
        */
        //((ProgressBar)findViewById(R.id.progressBar)).getIndeterminateDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

        List<Fragment> fragments = new Vector<>();

        fragments.add(Fragment.instantiate(this, DiscoverRecycler.class.getName())); //discover
        fragments.add(Fragment.instantiate(this, DiscoverRecycler.class.getName())); //amis
        fragments.add(Fragment.instantiate(this, PrivateChatList.class.getName())); //chat
        int[] tabTitles = new int[]{R.string.tab_discover, R.string.tab_friends, R.string.tab_chat};

        adapter = new PagerAdapter(getSupportFragmentManager(), fragments, tabTitles, this);
        pager.setAdapter(adapter);


        ((TabLayout) findViewById(R.id.tabLayoutHome)).setupWithViewPager(pager);
    }

    private void configToolbar() {
        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            appBar.setExpanded(true, true);
            bar.setTitle(R.string.app_name);
        }
    }

    /*
    Toolbar configuration
    */

    void setUserIcon(final ImageView image) {
        final Context context = getBaseContext();
        if (!APIRequest.isAPIConnected(context))
            return;
        APIRequest.JSONrequest(context, Request.Method.GET,
                APIUrl.BASE_URL + APIUrl.INFO_CURRENT_USER,
                true, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String imageUrl;
                        try {
                            JSONObject obj = response.getJSONObject(APIUrl.SEARCH_USER_JSONOBJECT);
                            username = obj.getString("userName");
                            imageUrl = obj.getString("picture");
                        } catch (JSONException e) {
                            return;
                        }
                        if (!imageUrl.isEmpty() && (imageUrl != "null"))
                             GlideApp.with(context).load(imageUrl).placeholder(R.drawable.ic_account_circle_black_24dp).into(image);
                        //placeholder(R.drawable.ic_account_circle_black_24dp)
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
    }

    private void startProfileIntent() {
        Intent intent = new Intent(this, ProfilePage.class);
        Bundle b = new Bundle();
        b.putString("name", username);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        final MenuItem profileIcon = menu.findItem(R.id.profileButton);
        FrameLayout actionView = (FrameLayout) profileIcon.getActionView();
        final ImageView image = actionView.findViewById(R.id.infoUserPicture);
        setUserIcon(image);
        final MenuItem searchItem = menu.findItem(R.id.search);

        actionView.setOnClickListener(new View.OnClickListener() {
            boolean clicked = false;

            @Override
            public void onClick(View view) {
                if (clicked)
                    return;
                if (!APIRequest.isAPIConnected(getApplicationContext())) {
                    APIRequest.startLoginActivity(getApplicationContext());
                    return;
                }

                if (!username.isEmpty())
                    startProfileIntent();
                else {
                    clicked = true;
                    APIRequest.JSONrequest(getApplicationContext(), Request.Method.GET,
                            APIUrl.BASE_URL + APIUrl.INFO_CURRENT_USER,
                            true, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONObject obj = response.getJSONObject(APIUrl.SEARCH_USER_JSONOBJECT);
                                        username = obj.getString("userName");
                                    } catch (JSONException e) {
                                        return;
                                    }
                                    startProfileIntent();
                                    clicked = false;
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    clicked = false;
                                    error.printStackTrace();
                                }
                            });
                }
            }
        });

    //For search in different activity
    final Context that = this;
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()

    {
        @Override
        public boolean onMenuItemClick (MenuItem menuItem){
        startActivity(new Intent(that, SearchResultsActivity.class));
        return true;
    }
    });


        /*
        //Research on this activity instead of new one.

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        //enlève icône de recherche sur la searchBar
        ImageView magImage = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        magImage.setVisibility(View.GONE);
        magImage.setImageDrawable(null);
        searchItem.setOnActionExpandListener(new SearchBarExpander(searchItem, menu, this));
        */

        return true;
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
