package com.tuxlu.polyvox.Room.Chat

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.tuxlu.polyvox.Chat.Author
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.Auth.AuthUtils
import com.tuxlu.polyvox.Utils.websockets.WebSocketClient
import org.json.JSONObject
import java.lang.Exception
import java.net.URI
import java.util.ArrayList

object CustomChat : WebSocketClient.Listener {
    data class UserMessageInfo(var hasHistory: Boolean, val messages: MutableList<RoomChatResult>)

    private var listUsers: MutableList<Author> = ArrayList()
    private var userMessages: HashMap<String, UserMessageInfo> = HashMap()
    private var roomMessages: MutableList<RoomChatResult> = ArrayList()
    private lateinit var socket: WebSocketClient
    private var isAuth = false
    private var myUserName: String = ""
    private var lastHistoryRequested = ""

    fun setupSocket(context: Context) {
        AsyncTask.execute {
            var authToken = ""
            if (AuthUtils.hasAccount(context)) {
                authToken = AuthUtils.getToken(context)
                myUserName = AuthUtils.getUsername(context)
            }
            isAuth = (authToken != "")
            if (::socket.isInitialized) {
                socket.disconnect()
                listUsers.clear()
                userMessages.clear()
                roomMessages.clear()
                lastHistoryRequested = ""
                Thread.sleep(3500)
            }
            socket = WebSocketClient(URI(APIUrl.CHAT_URL), this, authToken)
            socket.connect()
            socket.disconnect()
        }
    }

    private fun addSingleMessage(obj: JSONObject): RoomChatResult {
        val nick = obj.getString("nick")
        val mess = obj.getString("txt")
        val time = obj.getLong("timestamp")
        return RoomChatResult(nick, mess, time)
    }

    private fun getSingleUser(obj: JSONObject): Author {
        val name = obj.getString("userName")
        var pic = obj.getString("picture")
        if (pic.isNullOrBlank() || pic == "null")
            pic = ""
        return Author(name, pic)
    }

    override fun onMessage(text: String) {
        if (text.isBlank())
            return
        val obj = JSONObject(text)
        val cmd = obj.getString("cmd")
        when (cmd) {
            "history", "msg_list" -> {
                val arr = obj.getJSONArray("data")
                //Log.wtf("ROOMCHAT", arr.toString())
                if (cmd == "history") roomMessages.clear() else listUsers.clear()
                for (i in 0 until arr.length()) {
                    if (cmd == "history") {
                        roomMessages.add(addSingleMessage(arr.getJSONObject(i).getJSONObject("data")))
                    } else if (cmd == "msg_list") {
                        listUsers.add(getSingleUser(arr.getJSONObject(i).getJSONObject("userinfo")))
                    }
                }
                //Log.wtf("ROOMCHAT", "len ListUser = ${listUsers.size}")
            }
            "broadcast" -> roomMessages.add(addSingleMessage(obj.getJSONObject("data")))
            "msg" -> onPrivateMsg(obj)
            "msg_history" -> onPrivateHistoryMsg(obj)
        }
    }

    private fun checkUserCreation(username: String) {
        if (!userMessages.containsKey(username))
            userMessages[username] = UserMessageInfo(false, ArrayList())
    }

    private fun onPrivateMsg(obj: JSONObject) {
        val data = addSingleMessage(obj.getJSONObject("data"))
        checkUserCreation(data.username)
        userMessages[data.username]!!.messages.add(data)
    }

    private fun onPrivateHistoryMsg(obj: JSONObject) {
        val data = obj.getJSONObject("data")
        //val privateUser = getSingleUser(data.getJSONObject("info"))
        val arr = data.getJSONArray("data")
        val histList = ArrayList<RoomChatResult>()
        checkUserCreation(lastHistoryRequested)
        if (userMessages[lastHistoryRequested]!!.hasHistory)
            userMessages[lastHistoryRequested]!!.messages.clear()
        for (i in 0 until arr.length())
            histList.add(addSingleMessage(arr.getJSONObject(i)))
        userMessages[lastHistoryRequested]!!.messages.addAll(0, histList)
        userMessages[lastHistoryRequested]!!.hasHistory = true
    }

    override fun onMessage(data: ByteArray) {
        onMessage(String(data))
    }

    override fun onDisconnect(code: Int, reason: String?) = quitRoom()

    override fun onError(error: Exception?) {
        Log.wtf("customChat", error!!.message)
    }

    override fun onConnect() {
        if (isAuth)
            requestListUsers()
/*
        if (initialCommand.isNotEmpty())
            socket.send(initialCommand)
        if (channel.isNotEmpty())
            socket.send("/join $channel")
        //socket.send("/history") //only on replay
*/
    }


    fun getUserMessages(username: String): List<RoomChatResult> {
        if (!userMessages.containsKey(username))
            return ArrayList()
        val res = userMessages[username]
        //userMessages[username]!!.messages.clear()
        return ArrayList(res!!.messages)
    }


    fun getRoomMessages(): List<RoomChatResult> {
        val res = ArrayList(roomMessages)
        roomMessages.clear()
        return res
    }

    fun getListUsers(): List<Author> {
        val res = ArrayList(listUsers)
        //listUsers.clear()
        return res
    }

    fun joinRoom(token: String, history: Boolean = false) {
        socket.send("/join $token")
        roomMessages.clear()
        //if (history) //todo: reactivate if does not nake the server crash anymore
            //socket.send("/history $token 0")
    }

    fun quitRoom() {
        socket.send("/part")
    }

    fun requestListUsers() = socket.send("/msg_list")

    fun requestUserMessages(user: String) {
        socket.send("/msg_history $user 0")
        lastHistoryRequested = user
    }

    fun sendRoomMessage(message: String) = socket.send("/broadcast $message")

    fun sendUserMessage(user: String, message: String) = socket.send("/msg $user $message")
}