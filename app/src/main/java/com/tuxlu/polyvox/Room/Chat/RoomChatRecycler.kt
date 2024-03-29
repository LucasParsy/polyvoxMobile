package com.tuxlu.polyvox.Room.Chat

import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.Recyclers.Adapter
import com.tuxlu.polyvox.Utils.Recyclers.IRecycler
import com.tuxlu.polyvox.Utils.Recyclers.ViewHolderBinder
import com.tuxlu.polyvox.Utils.UIElements.LoadingUtils
import org.json.JSONObject
import java.util.*


/**
 * Created by tuxlu on 12/11/17.
 */

data class RoomChatResult(var username: String = "",
                          var message: String = "",
                          var timestamp: Long = 0)


open class RoomChatBinder : ViewHolderBinder<RoomChatResult> {

    //todo: change these random colors and set them in XML, or get them from user preference
    val colors = intArrayOf(Color.parseColor("#2176ff"),
            Color.parseColor("#d91ae0"),
            Color.parseColor("#f48c30"),
            Color.parseColor("#c42929"),
            Color.parseColor("#2b9921"))

    override fun bind(holder: Adapter.ViewHolder<RoomChatResult>, item: RoomChatResult) {
        val rand = Random(item.username.hashCode().toLong())
        val message = "<b><font color='" + colors[rand.nextInt(colors.size)] + "'>" + item.username + "</font></b>:" + item.message

        val result: Spanned
        result = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY)
        else
            Html.fromHtml(message)
        holder.v.findViewById<TextView>(R.id.message).text = result
    }

    override fun setClickListener(holder: Adapter.ViewHolder<RoomChatResult>, data: MutableList<RoomChatResult>) {
        //i dunno, what can you do here?
    }
}


class RoomChatRecycler : IRecycler<RoomChatResult>() {

    override val layoutListId: Int = R.layout.fragment_recycler_view
    override val layoutObjectId: Int = R.layout.info_room_chat
    override val recycleId: Int = R.id.recycleView
    override val requestObjectName: String = APIUrl.SEARCH_USER_JSONOBJECT

    override val binder = RoomChatBinder()
    override val itemDecoration = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val res = super.onCreateView(inflater, container, savedInstanceState)
        res!!.findViewById<View>(R.id.recycleView)?.setBackgroundColor(context?.resources!!.getColor(R.color.white))
        return res
    }


    fun update(data: List<RoomChatResult>) {
        LoadingUtils.EndLoadingView(rootView)
        adapter?.add(data)
        adapter?.notifyDataSetChanged()
    }

    override fun fillDataObject(json: JSONObject): RoomChatResult {
        return RoomChatResult("stub", "not needed")
    }

    override fun setLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(activity)

}
