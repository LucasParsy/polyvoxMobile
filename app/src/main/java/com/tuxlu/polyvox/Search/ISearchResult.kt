package com.tuxlu.polyvox.Search

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import com.android.volley.Request
import com.tuxlu.polyvox.Homepage.DiscoverRecycler
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.*
import kotlin.collections.HashMap
import com.tuxlu.polyvox.Utils.UIElements.PagerAdapter
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl


/**
 * Created by tuxlu on 20/11/17.
 */


/**
 * Created by parsyl on 24/07/2017.
 */

abstract class ISearchResult : AppCompatActivity() {
/*todo: gérer multiples fragments, avec recherche room ET fragment "global",
todo: qui inclut plusieurs fragment DONC Différent type des deux autres, exception, galère, chiant.
*/

    private val tabTitles = intArrayOf(R.string.users, R.string.tab_friends, R.string.tab_chat)
    internal var adapter: PagerAdapter? = null
    //lateinit private var roomFragment: SearchRoomRecycler
    //lateinit private var globalFragment: GlobalSearchFragment
    @BindView(R.id.pager) lateinit internal var pager: ViewPager


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL)
        setContentView(R.layout.activity_search)
        ButterKnife.bind(this)
        instanciateFragments(savedInstanceState)
        setupSearchView()
    }


    fun setupSearchView() {
        val searchView = this.findViewById<SearchView>(R.id.searchView) as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.isSubmitButtonEnabled = true
        searchView.isIconified = false
        searchView.requestFocusFromTouch()
        /*
        //((TabLayout) findViewById(R.id.tabLayout)).setupWithViewPager(pager);
        //enlève icône de recherche sur la searchBar
        ImageView magImage = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        magImage.setVisibility(View.GONE);
        magImage.setImageDrawable(null);
        */
    }

    fun search(query: String) {
        var map = HashMap<String, String>()
        map.put(APIUrl.SEARCH_PARAM1, query)
        val url = NetworkUtils.getParametrizedUrl(APIUrl.SEARCH, map)
        APIRequest.JSONrequest(this, Request.Method.GET, url, false, null,
                { result ->
                    if (result.has(APIUrl.SEARCH_USER_JSONOBJECT))
                    {
                        val usersFragment =  adapter?.getItem(0) as SearchUserRecycler
                        usersFragment.add(result.getJSONArray(APIUrl.SEARCH_USER_JSONOBJECT), true)
                    }
                    //todo: gérer autres fragments
                }, null);
    }



    protected fun instanciateFragments(savedInstanceState: Bundle?) {
        var fragments = mutableListOf<Fragment>()
        if (savedInstanceState == null) {
            fragments.add(Fragment.instantiate(this, SearchUserRecycler::class.java.name))
            //todo: gérer autres fragments
            adapter = PagerAdapter(supportFragmentManager, fragments, tabTitles, this)
        }
        else {
            val frags = supportFragmentManager.fragments
            fragments.add(supportFragmentManager.getFragment(savedInstanceState, "SearchFragment0"))
            adapter = PagerAdapter(supportFragmentManager, fragments, tabTitles, this)
        }

        pager.adapter = adapter
        findViewById<TabLayout>(R.id.tabLayoutSearch).setupWithViewPager(pager)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onBackButtonClick(v:View)
    {
        onBackPressed();
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        supportFragmentManager.putFragment(outState, "SearchFragment0", adapter?.getItem(0));
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        findViewById<View>(R.id.rootView).requestFocus()
        super.onConfigurationChanged(newConfig)
    }


}