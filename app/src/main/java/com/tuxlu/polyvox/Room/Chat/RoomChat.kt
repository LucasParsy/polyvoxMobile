package com.tuxlu.polyvox.Room.Chat

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stfalcon.chatkit.messages.MessageInput
import com.tuxlu.polyvox.R


/**
 * Created by tuxlu on 17/01/18.
 */
class RoomChat : Fragment(), MessageInput.InputListener {

    lateinit var rootView: View
    var chat = CustomChat
    lateinit var frag: RoomChatRecycler
    val handler = Handler()


    override fun onSubmit(input: CharSequence?): Boolean {
        val message = input.toString()
        chat.sendRoomMessage(message)

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
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = arguments!!.getString("token")
        val hasHistory = arguments!!.getBoolean("history")
        rootView.findViewById<MessageInput>(R.id.input).setInputListener(this)


        frag = RoomChatRecycler()
        fragmentManager!!.beginTransaction().add(R.id.roomChatLayout, frag).commit()
        val delay = 2000 //2 seconds
        chat.joinRoom(token!!, hasHistory)

        handler.postDelayed(object : Runnable {
            override fun run() {
                frag.update(chat.getRoomMessages())
                handler.postDelayed(this, delay.toLong())
            }
        }, 1500)
    }

    override fun onDestroy() {
        super.onDestroy()
        chat.quitRoom()
        handler.removeCallbacksAndMessages(null)
    }

}