package com.tuxlu.polyvox.Search

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.View
import com.android.volley.Request
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.NetworkUtils
import com.tuxlu.polyvox.Utils.UIElements.PagerAdapter
import kotlinx.android.synthetic.main.activity_search.*

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


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL)
        setContentView(R.layout.activity_search)
        instanciateFragments(savedInstanceState)
        setupSearchView()
    }


    private fun setupSearchView() {
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
        val map = HashMap<String, String>()
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
                }, null)
    }



    private fun instanciateFragments(savedInstanceState: Bundle?) {
        val fragments = mutableListOf<Fragment>()
        if (savedInstanceState != null) {
            //val frags = supportFragmentManager.fragments
            fragments.add(supportFragmentManager.getFragment(savedInstanceState, "SearchFragment0"))
            adapter = PagerAdapter(supportFragmentManager, fragments, tabTitles, this)
        } else {
            fragments.add(Fragment.instantiate(this, SearchUserRecycler::class.java.name))
            //todo: gérer autres fragments
            adapter = PagerAdapter(supportFragmentManager, fragments, tabTitles, this)
        }

        pager.adapter = adapter
        tabLayoutSearch.setupWithViewPager(pager)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onBackButtonClick(v:View)
    {
        onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        supportFragmentManager.putFragment(outState, "SearchFragment0", adapter?.getItem(0))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        rootView.requestFocus()
        super.onConfigurationChanged(newConfig)
    }


}