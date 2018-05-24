package com.tuxlu.polyvox.Search

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.UtilsTemp.Companion.dpToPixels
import kotlinx.android.synthetic.main.fragment_search_all.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by tuxlu on 13/03/18.
 */
class SearchSummary : Fragment() {
    private lateinit var rFrag : SearchRoomRecycler
    private lateinit var uFrag : SearchUserRecycler
    private var roomHeight = 0
    private var userHeight = 0

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            roomHeight = savedInstanceState.getInt("roomHeight")
            userHeight = savedInstanceState.getInt("userHeight")
            setUserHeight()
            uFrag = fragmentManager!!.getFragment(savedInstanceState, "SummaryFragment0") as SearchUserRecycler
            rFrag = fragmentManager!!.getFragment(savedInstanceState, "SummaryFragment1") as SearchRoomRecycler

            fragmentManager!!.beginTransaction()
                    .replace(R.id.roomsFragment, rFrag)
                    .replace(R.id.usersFragment, uFrag)
                    .commit()
        }
        else
        {
            uFrag = SearchUserRecycler()
            rFrag = SearchRoomRecycler()
            val fragmentManager = fragmentManager
            val fragmentTransaction = fragmentManager!!.beginTransaction()

            fragmentTransaction.add(R.id.roomsFragment, rFrag)
            fragmentTransaction.add(R.id.usersFragment, uFrag)
            fragmentTransaction.commit()
            roomsView.visibility = View.GONE
            usersView.visibility = View.GONE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_all, container, false)
    }

    private fun reduceJSONArray(arr: JSONArray): JSONArray {
        val res = JSONArray()
        var len = arr.length()
        if (len > 3)
            len = 3
        for (i in 0 until len)
            res.put(arr[i])
        return res
    }

    private fun setUserHeight()
    {
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, userHeight)
        params.topMargin = roomHeight
        usersView.layoutParams = params
    }

    fun add(obj: JSONObject) {

        roomHeight = 0

        if (obj.getJSONArray(APIUrl.SEARCH_ROOM_JSONOBJECT).length() != 0) {
            roomsView.visibility = View.VISIBLE
            val rooms = reduceJSONArray(obj.getJSONArray(APIUrl.SEARCH_ROOM_JSONOBJECT))
            roomHeight = dpToPixels(rooms.length() * (96 + 20) + 40, context!!)
            rFrag.add(rooms, true)
        } else
            roomsView.visibility = View.GONE

        if (obj.getJSONArray(APIUrl.SEARCH_USER).length() != 0) {
            usersView.visibility = View.VISIBLE
            val users = reduceJSONArray(obj.getJSONArray(APIUrl.SEARCH_USER))
            userHeight = dpToPixels(users.length() * (70 + 12) + 40, context!!)
            uFrag.add(users, true)
        } else
            usersView.visibility = View.GONE
        setUserHeight()

        if (roomsView.visibility == View.GONE && usersView.visibility == View.GONE)
            noResultsView.visibility = View.VISIBLE
        else
            noResultsView.visibility = View.INVISIBLE
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("roomHeight", roomHeight)
        outState.putInt("userHeight", userHeight)
        fragmentManager!!.putFragment(outState, "SummaryFragment0", uFrag)
        fragmentManager!!.putFragment(outState, "SummaryFragment1", rFrag)
    }

}