package com.tuxlu.polyvox.Room

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.Recyclers.IRecycler
import com.tuxlu.polyvox.Utils.Recyclers.IRequestRecycler
import com.tuxlu.polyvox.Utils.UIElements.LoadingUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


/**
 * Created by tuxlu on 19/01/18.
 */
open class FLRecycler : IRecycler<FlUser>() {

    override val layoutListId: Int = R.layout.fragment_recycler_view
    override val recycleId: Int = R.id.recycleView

    override val requestObjectName: String = "data"

    //unused params
    override val layoutObjectId: Int = R.layout.info_discover_room
    override val binder = null;
    override val itemDecoration = null

    private lateinit var recycler: RecyclerView;


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(layoutListId, container, false)
        LoadingUtils.EndLoadingView(rootView)
        noResView = rootView.findViewById<View>(R.id.noResultsLayout)
        recycler = rootView.findViewById<RecyclerView>(recycleId)
        recycler.setHasFixedSize(true)
        val layoutManager = setLayoutManager()
        recycler.layoutManager = layoutManager
        LoadingUtils.StartLoadingView(rootView, context)
        return rootView
    }



    override fun fillDataObject(json: JSONObject): FlUser {
        try {
            val name = "documents" //getString(R.string.tab_files)
            val imageUrl = ""
            //val name = json.getString("userName")
            //val imageUrl = json.getString("url")
            val list: ArrayList<FlFile> = arrayListOf()

            val jsonList = json.getJSONArray("fileUploadList")
            var file: JSONObject;
            for (i in 0 until jsonList.length()) {
                file = jsonList.getJSONObject(i)
                list.add(FlFile(file.getString("filename"),
                        file.getString("path"),
                        file.getString("size")))
            }
            return FlUser(name, imageUrl, list)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return FlUser("", "", arrayListOf());
    }

    fun add(data: JSONObject, replace: Boolean)
    {
        //todo: redo everything without parent/child recycler if not used.
        val list = ArrayList<FlUser>()
        list.add(fillDataObject(data))

        if (replace || list.size == 0)
            recycler.adapter = null
        if (list.size != 0) {
            val adapter = FilelistAdapter(list, activity!!)
            for (i in adapter.groups.size - 1 downTo 0) {
                if (!adapter.isGroupExpanded(adapter.groups[i]));
                    adapter.toggleGroup(adapter.groups[i])
            }
            recycler.adapter = adapter
        }
    }

    override fun clear()
    {
        recycler.adapter = null
    }

    override fun setLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(activity)

}
