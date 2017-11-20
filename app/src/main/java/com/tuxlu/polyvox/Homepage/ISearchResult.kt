package com.tuxlu.polyvox.Homepage

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import com.tuxlu.polyvox.R
import java.util.*

/**
 * Created by tuxlu on 20/11/17.
 */


/**
 * Created by parsyl on 24/07/2017.
 */

abstract class ISearchResult: AppCompatActivity() {
    internal var adapter: PagerAdapter? = null
    internal var fragments: MutableList<Fragment>? = null
    lateinit internal var  pager: ViewPager


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL)
        setContentView(R.layout.activity_search)
        pager = findViewById(R.id.pager)
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
        if (fragments == null && !query.isEmpty())
            instanciateFragments(query)
        else if (fragments != null)
        {
            val recycler = fragments!![0] as SearchRoomRecycler
            if (query.isEmpty())
                recycler.clear()
            else
                recycler.search(query)
        }
    }

    protected fun instanciateFragments(query: String) {
        if (fragments == null) {
            fragments = Vector()
            val args = Bundle()
            args.putString("query", query)
            fragments!!.add(Fragment.instantiate(this, SearchRoomRecycler::class.java.name, args))

            val tabTitles = intArrayOf(R.string.tab_discover, R.string.tab_friends, R.string.tab_chat)
            adapter = PagerAdapter(supportFragmentManager, fragments!!, tabTitles, this)
            pager!!.adapter = adapter
            //((TabLayout) findViewById(R.id.tabLayout)).setupWithViewPager(pager);
        }
    }
}