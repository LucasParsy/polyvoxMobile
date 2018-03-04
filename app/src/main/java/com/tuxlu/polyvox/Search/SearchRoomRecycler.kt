package com.tuxlu.polyvox.Search

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Room.Room
import com.tuxlu.polyvox.User.ProfilePage
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp
import com.tuxlu.polyvox.Utils.Recyclers.Adapter
import com.tuxlu.polyvox.Utils.Recyclers.IRecycler
import com.tuxlu.polyvox.Utils.Recyclers.ViewHolderBinder
import com.tuxlu.polyvox.Utils.UtilsTemp
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by tuxlu on 12/11/17.
 */

data class RoomSearchResult(var active: Boolean = true,
                            var name: String ="",
                            var imageUrl: String ="",
                            var tags: String ="",
                            var token: String ="")


open class RoomSearchBinder : ViewHolderBinder<RoomSearchResult> {

    override fun bind(holder: Adapter.ViewHolder<RoomSearchResult>, item: RoomSearchResult)
    {
        if (!item.active)
            holder.v.visibility = View.GONE;
        holder.v.findViewById<TextView>(R.id.infoRoomName).text = item.name
        holder.v.findViewById<TextView>(R.id.infoRoomSubject).text = item.tags
        val image = holder.v.findViewById<ImageView>(R.id.infoRoomPicture)
        if (!UtilsTemp.isStringEmpty(item.imageUrl))
            GlideApp.with(holder.v.context).load(item.imageUrl).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.ic_logo_carrey).into(image)
    }

    override fun setClickListener(holder: Adapter.ViewHolder<RoomSearchResult>, data: MutableList<RoomSearchResult>)
    {
        val context = holder.v.context
        val clickListener = View.OnClickListener {_ ->

            val intent = Intent(context, Room::class.java)
            val b = Bundle()
            b.putString("title", data[holder.adapterPosition].name)
            b.putString("token", data[holder.adapterPosition].token)
            intent.putExtras(b)
            context.startActivity(intent)
        }
        holder.v.findViewById<View>(R.id.infoRoomLayout).setOnClickListener(clickListener)
    }
}

class SearchRoomRecycler : IRecycler<RoomSearchResult>() {

    override val layoutListId: Int = R.layout.fragment_recycler_view
    override val layoutObjectId: Int = R.layout.info_search_room
    override val recycleId: Int = R.id.recycleView
    override val requestObjectName : String = APIUrl.SEARCH_USER_JSONOBJECT

    override val binder = RoomSearchBinder()
    override val itemDecoration= LinearItemDecoration()



    override fun fillDataObject(json: JSONObject): RoomSearchResult
    {
        val res = RoomSearchResult()
        try {
            //if (!json.has("status") || json.get("status") !=  "running")
                //res.active = false;
            res.name = json.getString("name")
            res.imageUrl = json.getString(APIUrl.SEARCH_USER_IMAGE_URL)
            var tagString = "";
            val tags = json.getJSONArray("tags")
            for (i in 0 until tags.length()) {
                tagString += (tags[i]);
                tagString += " ";
            }
            tagString = tagString.trim();
            res.tags = tagString;
            res.token = json.getString("token")

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return res
    }

    override fun setLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(activity)

}
