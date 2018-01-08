package com.tuxlu.polyvox.Chat

import com.stfalcon.chatkit.commons.models.IUser



/**
 * Created by tuxlu on 08/01/18.
 */

class Author(val username: String, val url: String) : IUser {

    /*...*/

    override fun getId(): String {
        return username
    }

    override fun getName(): String {
        return username
    }

    override fun getAvatar(): String {
        return url
    }
}