package com.tuxlu.polyvox.Homepage

import android.content.Intent
import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.VolleyError
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Room.Room
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp
import com.tuxlu.polyvox.Utils.NetworkLibraries.VHttp
import com.tuxlu.polyvox.Utils.Recyclers.Adapter
import com.tuxlu.polyvox.Utils.Recyclers.IRequestRecycler
import com.tuxlu.polyvox.Utils.Recyclers.ViewHolderBinder
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by tuxlu on 15/11/17.
 */


data class DiscoverBox(var name: String ="",
                       var imageUrl: String ="",
                       var viewers: Int = -1,
                       var roomID: Int = -1)


class SpaceItemDecoration : RecyclerView.ItemDecoration() {

    private val spaceHeight = 8

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State?) {
        outRect.right = spaceHeight
        outRect.left = outRect.right
        outRect.bottom = outRect.left
        outRect.top = outRect.bottom
    }
}

open class DiscoverBinder : ViewHolderBinder<DiscoverBox> {

    override fun bind(holder: Adapter.ViewHolder<DiscoverBox>, item: DiscoverBox)
    {
        holder.v.findViewById<TextView>(R.id.infoRoomName).text = item.name
        holder.v.findViewById<TextView>(R.id.infoRoomViewers).text = (item.viewers.toString())

        var image = holder.v.findViewById<ImageView>(R.id.infoRoomPicture)
        GlideApp.with(holder.v.context).load(item.imageUrl).into(image)
    }

    override fun setClickListener(holder: Adapter.ViewHolder<DiscoverBox>, data: MutableList<DiscoverBox>)
    {
        val context = holder.v.getContext()
        val clickListener = View.OnClickListener {_ ->

            val intent = Intent(context, Room::class.java)

            intent.putExtra("id", data[holder.adapterPosition].roomID)
            intent.putExtra("title", data[holder.adapterPosition].name)
            context.startActivity(intent)
        }
        holder.v.findViewById<View>(R.id.infoRoomLayout).setOnClickListener(clickListener);
    }
}


open class DiscoverRecycler() : IRequestRecycler<DiscoverBox>()
{
    override val layoutListId: Int = R.layout.fragment_recycler_view
    override val layoutObjectId: Int = R.layout.info_discover_room
    override val recycleId: Int = R.id.recycleView
    override val requestUrl : String = APIUrl.DISCOVER_ROOMS
    override val requestBody: JSONObject = JSONObject()
    override val usesAPI : Boolean = false
    override val requestObjectName : String = "rooms"

    override val binder = DiscoverBinder()
    override val itemDecoration= SpaceItemDecoration()


    override fun errorListener(error: VolleyError)
    {
        Log.wtf(tag, error);
    }

    override fun fillDataObject(json: JSONObject): DiscoverBox
    {
        var rb = DiscoverBox()
        try {
            rb.name = json.getString("title")
            rb.imageUrl = json.getString("picture")
            rb.viewers = json.getInt("viewers")
            rb.roomID = json.getInt("roomID")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return rb
    }

    override fun setLayoutManager(): RecyclerView.LayoutManager =
            GridLayoutManager(activity, resources.getInteger(R.integer.homepage_rooms_row_number))

}