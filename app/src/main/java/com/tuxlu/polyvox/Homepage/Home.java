package com.tuxlu.polyvox.Homepage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;
import com.tuxlu.polyvox.BuildConfig;
import com.tuxlu.polyvox.R;
import com.tuxlu.polyvox.Search.SearchResultsActivity;
import com.tuxlu.polyvox.User.ProfilePage;
import com.tuxlu.polyvox.Utils.API.APIRequest;
import com.tuxlu.polyvox.Utils.Auth.AuthUtils;
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp;
import com.tuxlu.polyvox.Utils.UIElements.PagerAdapter;
import com.tuxlu.polyvox.Utils.UtilsTemp;

import java.util.List;
import java.util.Vector;

import io.fabric.sdk.android.Fabric;


public class Home extends AppCompatActivity {

    private DiscoverRoomRecycler discover;
    private HistoricRoomRecycler historic;

    private ImageView profileImage = null;
    private Boolean infoLoaded = false;
    private static final String TAG = "Home";
    Handler handler = new Handler();
    PagerAdapter adapter;
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        configToolbar();
        //leaks verification, in debug builds only.

        //todo: reactivate leak check
        //if (LeakCanary.isInAnalyzerProcess(this)) {return;}
        //LeakCanary.install(this.getApplication());

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713"); //test
        //MobileAds.initialize(this, "ca-app-pub-4121964947351781~9215757073"); //true


        if (!BuildConfig.DEBUG)
            Fabric.with(this, new Crashlytics());

        //((ProgressBar)findViewById(R.id.progressBar)).getIndeterminateDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

        List<Fragment> fragments = new Vector<>();

        discover = (DiscoverRoomRecycler) Fragment.instantiate(this, DiscoverRoomRecycler.class.getName());
        fragments.add(discover); //discover

        if (AuthUtils.hasAccount(this))
        {
            historic = (HistoricRoomRecycler) Fragment.instantiate(this, HistoricRoomRecycler.class.getName());
            fragments.add(historic); //discover
        }

        //TODO: disable this tab for the Playstore release
        //fragments.add(Fragment.instantiate(this, ChatList.class.getName())); //chat
        //int[] tabTitles = new int[]{R.string.tab_discover, R.string.tab_chat};
        int[] tabTitles = new int[]{R.string.tab_discover, R.string.tab_historic};
        pager = findViewById(R.id.pager);
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
        if (!AuthUtils.hasAccount(context))
            return;
        String imageUrl = AuthUtils.getPictureUrl(context);
        if (!UtilsTemp.isStringEmpty(imageUrl))
            GlideApp.with(context).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.ic_account_circle_black_24dp).into(image);
        //placeholder(R.drawable.ic_account_circle_black_24dp)
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

        final Activity activity = this;
        actionView.setOnClickListener(new View.OnClickListener() {
            boolean clicked = false;

            @Override
            public void onClick(View view) {
                if (clicked)
                    return;
                if (!AuthUtils.hasAccount(getApplicationContext())) {
                    APIRequest.startLoginActivity(activity);
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
            public boolean onMenuItemClick(MenuItem menuItem) {
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
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AuthUtils.AUTH_REQUEST_CODE && resultCode == Activity.RESULT_OK)
            this.recreate();
    }


}
