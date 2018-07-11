package com.tuxlu.polyvox.Search

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.tuxlu.polyvox.Chat.Chat
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Room.Room
import com.tuxlu.polyvox.Utils.Recyclers.Adapter
import com.tuxlu.polyvox.Utils.UtilsTemp
import okhttp3.internal.Util
import org.w3c.dom.Text

/**
 * Created by tuxlu on 12/11/17.
 */


open class FollowerUserBinder : UserSearchBinder() {

    override fun bind(holder: Adapter.ViewHolder<UserSearchResult>, item: UserSearchResult) {
        super.bind(holder, item)

        holder.v.findViewById<TextView>(R.id.infoUserName).height = UtilsTemp.dpToPixels(40, holder.v.context)
        if (item.roomName.isNotEmpty()) {
            holder.v.findViewById<LinearLayout>(R.id.infoCurrentRoomView).visibility = View.VISIBLE
            holder.v.findViewById<TextView>(R.id.infoCurrentRoomText).text = item.roomName
            holder.v.findViewById<Button>(R.id.joinButton).setOnClickListener {
                val intent = Intent(holder.v.context, Room::class.java)
                val b = Bundle()
                b.putString("title", item.name)
                b.putString("token", item.roomUrl)
                b.putString("imageUrl", "")
                intent.putExtras(b)
                holder.v.context.startActivity(intent)
            }
        }
        /*
        val button = holder.v.findViewById<ImageButton>(R.id.chatButton)
        button.visibility = View.VISIBLE
        button.setOnClickListener {
            val intent = Intent(holder.v.context, Chat::class.java)
            val b = Bundle()
            b.putString("name", item.name)
            b.putString("url", item.imageUrl)
            intent.putExtras(b)
            holder.v.context.startActivity(intent)
        }
        */
    }
}


class FollowerUserRecycler : SearchUserRecycler() {

    override val binder = FollowerUserBinder()
}
