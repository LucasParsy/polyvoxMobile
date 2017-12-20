package com.tuxlu.polyvox.Utils.Recyclers

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.UIElements.LoadingUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.ArrayList

/**
 * Created by tuxlu on 12/11/17.
 */


abstract class IRecycler<T: Any>() : Fragment() {

    abstract val layoutListId: Int
    abstract val layoutObjectId: Int
    abstract val recycleId: Int
    abstract val requestObjectName : String

    abstract val itemDecoration: RecyclerView.ItemDecoration
    abstract val binder: ViewHolderBinder<T>

    private var adapter: Adapter<T>? = null;

    abstract fun fillDataObject(json: JSONObject): T
    abstract fun setLayoutManager(): RecyclerView.LayoutManager

    lateinit var rootView : View
    var noResView : View? = null
    var isNoResViewVisible : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //retainInstance = true  //seems to leak
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        rootView = inflater.inflate(layoutListId, container, false)
        LoadingUtils.EndLoadingView(rootView)
        noResView = rootView.findViewById<View>(R.id.noResultsLayout)
        val recycler = rootView.findViewById<View>(recycleId) as RecyclerView
        recycler.setHasFixedSize(true)


        val layoutManager = setLayoutManager()
        recycler.addItemDecoration(itemDecoration)
        recycler.layoutManager = layoutManager

        adapter = Adapter(context!!, ArrayList(), layoutObjectId, binder)
        recycler.adapter = adapter
        return rootView
    }

    fun setNoResViewVisibility(nvis : Boolean)
    {
        isNoResViewVisible = nvis
    }

    fun add(data: JSONArray, replace: Boolean=false)
    {
        var list : MutableList<T> = parseJSON(data)

        if (noResView != null) {
            if (list.size == 0 && isNoResViewVisible)
                noResView!!.visibility = View.VISIBLE
            else
                noResView!!.visibility = View.INVISIBLE
        }

        if (replace || list.size == 0)
            adapter?.clear()
        if (list.size != 0)
            adapter?.add(parseJSON(data))
        adapter?.notifyDataSetChanged()
    }

    fun clear()
    {
        adapter?.clear()
        adapter?.notifyDataSetChanged()
    }

    internal fun parseJSON(jArray: JSONArray): MutableList<T> {
        val data = ArrayList<T>()
        if (jArray.length() == 0)
            return data
        for (i in 0 until jArray.length()) {
            val obj : T
            try {
                val json = jArray.getJSONObject(i)
                obj = fillDataObject(json);
                data.add(obj)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return data
    }
}
