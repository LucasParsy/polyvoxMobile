package com.tuxlu.polyvox.Chat

import com.stfalcon.chatkit.commons.models.IDialog
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import com.stfalcon.chatkit.messages.MessagesList
import java.util.*

/**
 * Created by tuxlu on 08/01/18.
 */
class ChatDialog(private val friend : Author, val unread: Int, val message: IMessage?) : IDialog<IMessage> {


    override fun getDialogName(): String {
        return friend.name
    }

    override fun getId(): String {
        return friend.name
    }

    override fun getUsers(): MutableList<out IUser> {
        return arrayListOf(friend)
    }

    override fun getLastMessage(): IMessage {
       if (message == null)
           return ChatMessage("", friend, Date())
        return message
    }

    override fun getDialogPhoto(): String {
        return friend.url
    }

    override fun setLastMessage(nMessage: IMessage?) {
        this.setLastMessage(nMessage)
    }

    override fun getUnreadCount(): Int {
        return unread
    }



}