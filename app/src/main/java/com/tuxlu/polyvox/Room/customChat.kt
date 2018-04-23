package com.tuxlu.polyvox.Room

import android.util.Log
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.websockets.WebSocketClient
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.URI
import java.util.ArrayList

class CustomChat(private val channel: String, private val token : String): WebSocketClient.Listener
{
    private var messages: MutableList<RoomChatResult> = ArrayList()
    private val socket = WebSocketClient(URI(APIUrl.CHAT_URL), this, token)


    fun addSingleMessage(obj: JSONObject)
    {
        val nick = obj.getString("nick")
        val mess = obj.getString("txt")
        //obj.getLong("timestamp")
        messages.add(RoomChatResult(nick, mess))
    }

    override fun onMessage(text: String) {
        if (text.isBlank())
            return
        if (text[0] == '[')
        {
            val arr = JSONArray(text)
            for (i in 0 until arr.length())
                addSingleMessage(arr.getJSONObject(i))
        }
        else
            addSingleMessage(JSONObject(text))
    }

    override fun onMessage(data: ByteArray) {
        onMessage(String(data));
    }

    override fun onDisconnect(code: Int, reason: String?) {
        socket.send("/part")
    }

    override fun onError(error: Exception?) {
        Log.wtf("customChat", error!!.message)
    }

    override fun onConnect() {
        socket.send("/join $channel")
        socket.send("/history")
    }


    init {
        socket.connect()
    }

    fun getMessages(): List<RoomChatResult> {
        val res = ArrayList(messages)
        messages.clear()
        return res
    }

    fun sendMessage(message: String)
    {
        socket.send(message)
    }

}