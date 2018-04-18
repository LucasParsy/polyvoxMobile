package com.tuxlu.polyvox.Room

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stfalcon.chatkit.messages.MessageInput
import com.tuxlu.polyvox.R
import java.util.*

/**
 * Created by tuxlu on 17/01/18.
 */
class RoomChat : Fragment(), MessageInput.InputListener {

    lateinit var rootView: View
    lateinit var chat: CustomChat
    lateinit var frag: RoomChatRecycler
    var username: String? = null

    override fun onSubmit(input: CharSequence?): Boolean {
        val message = input.toString()
        chat.sendMessage(message);

        /*
        //twitch does not send again your message, you have to put it yourself.
        val list = ArrayList<RoomChatResult>()
        list.add(RoomChatResult(username!!, message))
        frag.update(list)
        */
        return true
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_room_chat, container, false)


        username = arguments?.getString("username")
        val title = arguments?.getString("title")
        chat = CustomChat(username!!, title!!)
        rootView.findViewById<MessageInput>(R.id.input).setInputListener(this)


        frag = RoomChatRecycler()
        fragmentManager!!.beginTransaction().add(R.id.roomChatLayout, frag).commit()
        val handler = Handler()
        val delay = 2000 //2 seconds
        handler.postDelayed(object : Runnable {
            override fun run() {
                frag.update(chat.getMessages())
                handler.postDelayed(this, delay.toLong())
            }
        }, 0)

        return rootView;
    }
}