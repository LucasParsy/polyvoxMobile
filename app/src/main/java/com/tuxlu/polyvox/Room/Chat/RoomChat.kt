package com.tuxlu.polyvox.Room.Chat

import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stfalcon.chatkit.messages.MessageInput
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.Auth.AuthUtils
import kotlinx.android.synthetic.main.fragment_room_chat.*
import com.google.android.exoplayer2.offline.Downloader


/**
 * Created by tuxlu on 17/01/18.
 */
class RoomChat : Fragment(), MessageInput.InputListener {

    lateinit var rootView: View
    var chat: CustomChat? = null
    lateinit var frag: RoomChatRecycler
    val handler = Handler()


    override fun onSubmit(input: CharSequence?): Boolean {
        val message = input.toString()
        chat?.sendMessage(message)

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

        val title = arguments?.getString("title")
        rootView.findViewById<MessageInput>(R.id.input).setInputListener(this)


        frag = RoomChatRecycler()
        fragmentManager!!.beginTransaction().add(R.id.roomChatLayout, frag).commit()
        val delay = 2000 //2 seconds

        var token: String? = null
        AsyncTask.execute {
            token = AuthUtils.getToken(context)
        }

        handler.postDelayed(object : Runnable {
            override fun run() {
                if (chat == null)
                    setupChat(title!!, token)

                frag.update(chat!!.getMessages())
                handler.postDelayed(this, delay.toLong())
            }
        }, 1500)
    }

    fun setupChat(title: String, nToken: String?) {
        var token = nToken
        if (token == null) {
            token = ""
            input.visibility = View.GONE
        }
        chat = CustomChat(title, token)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

}