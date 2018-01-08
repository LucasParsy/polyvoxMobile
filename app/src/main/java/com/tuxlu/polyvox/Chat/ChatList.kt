package com.tuxlu.polyvox.Chat

import android.app.Dialog
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.dialogs.DialogsList
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.User.ProfilePage
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.MyDateUtils
import com.tuxlu.polyvox.Utils.UIElements.LoadingUtils
import com.tuxlu.polyvox.Utils.UtilsTemp
import kotlinx.android.synthetic.main.activity_user_options_info_user.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by tuxlu on 08/01/18.
 */
class ChatList : android.support.v4.app.Fragment() , DialogsListAdapter.OnDialogClickListener<ChatDialog> {
    override fun onDialogClick(dialog: ChatDialog) {
        val name = dialog.dialogName
        val intent = Intent(this.activity, ProfilePage::class.java) //todo: replace with good fragment name
        val b = Bundle()
        b.putString("name", name)
        intent.putExtras(b)
        startActivity(intent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_private_chat_list, container, false)

        val dialogsListAdapter= DialogsListAdapter<ChatDialog>(ImageLoader(this.activity!!))
        dialogsListAdapter.setOnDialogClickListener(this);
        view.findViewById<DialogsList>(R.id.dialogsList).setAdapter(dialogsListAdapter)

        var list = ArrayList<ChatDialog>()

        val url = APIUrl.FAKE_BASE_URL + APIUrl.LIST_USERS_CHAT
        APIRequest.JSONrequest(activity, Request.Method.GET, url,
                true, null, { current ->
            LoadingUtils.EndLoadingView(view)
            val topObj = current.getJSONObject(APIUrl.SEARCH_USER_JSONOBJECT)
            val usersArray = topObj.getJSONArray("users")
            for (i in 0..(usersArray.length() - 1))
            {
                val elem = usersArray.getJSONObject(i)
                val author = Author(elem.getString("userName"),
                        elem.getString("picture"))
                val unread = elem.getInt("unread")
                var message: ChatMessage? = null
                val messStr = elem.getString("lastMessage")
                if (!UtilsTemp.isStringEmpty(messStr)) {
                    val ft = SimpleDateFormat("yyyy-MM-dd HH:mm")
                    val date: Date = ft.parse(elem.getString("lastMessageDate"))
                    message = ChatMessage(messStr, author, date, null)
                }
                list.add(ChatDialog(author, unread, message))
            }
            if (list.isEmpty())
                view.findViewById<View>(R.id.noFriendsView).visibility = View.VISIBLE
            dialogsListAdapter.setItems(list)
        }, {_ -> LoadingUtils.EndLoadingView(view)
        })

        return view
    }

}