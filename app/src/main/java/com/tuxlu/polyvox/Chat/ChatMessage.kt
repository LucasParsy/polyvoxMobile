package com.tuxlu.polyvox.Chat

import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import com.stfalcon.chatkit.commons.models.MessageContentType
import java.util.*

/**
 * Created by tuxlu on 08/01/18.
 */

class ChatMessage(private val nText: String,
                  private val nUser: Author,
                  private val date: Date,
                  private val iUrl: String? = null,
                  private val fileSize: Int = 0,
                  private val fileUrl: String? = null) : IMessage,
        MessageContentType,
        MessageContentType.Image {
    override fun getImageUrl(): String? {
        return iUrl
    }

    private val id: String = date.time.toString()

    override fun getId(): String {
        return id
    }

    override fun getCreatedAt(): Date {
        return date
    }

    override fun getUser(): IUser {
        return nUser
    }

    override fun getText(): String {
        return nText
    }

    fun getFileSize(): Int {
        return fileSize
    }

    fun getFileUrl(): String? {
        return fileUrl
    }

}