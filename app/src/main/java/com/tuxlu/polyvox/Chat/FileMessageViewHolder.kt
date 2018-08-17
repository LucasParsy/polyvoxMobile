package com.tuxlu.polyvox.Chat

import android.view.View
import android.widget.TextView
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.utils.DateFormatter
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.UtilsTemp


/**
 * Created by tuxlu on 10/01/18.
 */
class FileContentChecker : MessageHolders.ContentChecker<ChatMessage> {

    override fun hasContentFor(message: ChatMessage, type: Byte): Boolean {
        when (type) {
            Companion.CONTENT_TYPE_FILE -> return (message.getFileSize() != 0
                    && message.getFileUrl() != null
                    && !message.getFileUrl()!!.isEmpty())
        }
        return false
    }

    companion object {
        val CONTENT_TYPE_FILE: Byte = 1

        fun messageHolderOnBind(message: ChatMessage, itemView: View) {
            itemView.findViewById<TextView>(R.id.ckDocumentName).text = message.text
            itemView.findViewById<TextView>(R.id.ckDocumentSize).text = UtilsTemp.byteSizeToString(message.getFileSize(), true)
            itemView.findViewById<TextView>(R.id.time).text = DateFormatter.format(message.createdAt, DateFormatter.Template.TIME)
        }
    }
}

class IncomingFileMessageViewHolder(itemView: View) :
        MessageHolders.IncomingTextMessageViewHolder<ChatMessage>(itemView) {
    override fun onBind(message: ChatMessage) {
        super.onBind(message)
        FileContentChecker.messageHolderOnBind(message, itemView)
    }
}

class OutcomingFileMessageViewHolder(itemView: View) :
        MessageHolders.OutcomingTextMessageViewHolder<ChatMessage>(itemView) {
    override fun onBind(message: ChatMessage) {
        super.onBind(message)
        FileContentChecker.messageHolderOnBind(message, itemView)
    }
}


