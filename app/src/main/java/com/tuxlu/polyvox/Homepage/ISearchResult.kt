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
import com.android.volley.Request
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.*
import kotlinx.android.synthetic.main.fragment_search_all.*
import org.json.JSONObject
import java.io.Serializable
import java.util.*
import android.widget.Toast
import android.R.attr.data
import org.json.JSONArray
import kotlin.collections.HashMap
import android.view.Gravity
import android.widget.TextView
import org.w3c.dom.Text


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

    internal var adapter: PagerAdapter? = null
    //lateinit private var roomFragment: SearchUserRecycler
    //lateinit private var globalFragment: GlobalSearchFragment
    lateinit private var usersFragment: SearchUserRecycler
    lateinit internal var pager: ViewPager


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL)
        setContentView(R.layout.activity_search)
        instanciateFragments()
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
        NetworkUtils.JSONrequest(this, Request.Method.GET, url, false, null,
                { result ->
                    val debugResponse = result
                    val debugSure = 1+1
                    if (result.has(APIUrl.SEARCH_USER_JSONOBJECT))
                    {
                        usersFragment.add(result.getJSONArray(APIUrl.SEARCH_USER_JSONOBJECT), true)
                    }
                    //todo: gérer autres fragments
                }, { error ->
            error.printStackTrace()
        });
    }

    protected fun instanciateFragments() {
        pager = findViewById(R.id.pager)
        var fragments = mutableListOf<Fragment>()
        usersFragment = Fragment.instantiate(this, SearchUserRecycler::class.java.name) as SearchUserRecycler
        fragments.add(usersFragment)
        val tabTitles = intArrayOf(R.string.tab_discover, R.string.tab_friends, R.string.tab_chat)
        adapter = PagerAdapter(supportFragmentManager, fragments, tabTitles, this)
        pager.adapter = adapter
        //((TabLayout) findViewById(R.id.tabLayout)).setupWithViewPager(pager);

    }
}