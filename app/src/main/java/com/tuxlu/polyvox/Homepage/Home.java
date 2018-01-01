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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.crash.FirebaseCrash;
import com.squareup.leakcanary.LeakCanary;
import com.tuxlu.polyvox.R;
import com.tuxlu.polyvox.Search.SearchResultsActivity;
import com.tuxlu.polyvox.User.ProfilePage;
import com.tuxlu.polyvox.Utils.API.APIRequest;
import com.tuxlu.polyvox.Utils.API.APIUrl;
import com.tuxlu.polyvox.Utils.Auth.AuthUtils;
import com.tuxlu.polyvox.Utils.UIElements.PagerAdapter;
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp;
import com.tuxlu.polyvox.Utils.UtilsTemp;
import org.json.JSONException;
import org.json.JSONObject;
//import io.fabric.sdk.android.Fabric;
//import com.crashlytics.android.Crashlytics;
//import com.crashlytics.android.core.CrashlyticsCore;

import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Home extends AppCompatActivity {

    private ImageView profileImage = null;
    private static final String TAG = "Home";
    PagerAdapter adapter;
    @BindView(R.id.pager)
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        configToolbar();
        //Fabric.with(this, new Crashlytics());
        //leaks verification, in debug builds only.

        if (LeakCanary.isInAnalyzerProcess(this)) {return;}
        LeakCanary.install(this.getApplication());

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
        AppBarLayout appBar = findViewById(R.id.appbar);
        Toolbar myToolbar = findViewById(R.id.toolbar);
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
                            imageUrl = obj.getString("picture");
                        } catch (JSONException e) {
                            return;
                        }
                        if (!UtilsTemp.isStringEmpty(imageUrl))
                             GlideApp.with(context).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.ic_account_circle_black_24dp).into(image);
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
        b.putString("name", AuthUtils.getUsername(getBaseContext()));
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        final MenuItem profileIcon = menu.findItem(R.id.profileButton);
        FrameLayout actionView = (FrameLayout) profileIcon.getActionView();
        profileImage = actionView.findViewById(R.id.infoUserPicture);
        setUserIcon(profileImage);
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
                startProfileIntent();
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
    protected void onRestart() {
        super.onRestart();
        if (profileImage != null)
        setUserIcon(profileImage);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
