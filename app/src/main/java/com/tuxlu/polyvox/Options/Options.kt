package com.tuxlu.polyvox.Options

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.Auth.AuthUtils
import com.tuxlu.polyvox.Utils.UIElements.MyAppCompatActivity

@Suppress("UNUSED_PARAMETER")
/**
 * Created by tuxlu on 29/11/17.
 */

class Options : MyAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_options)
    }

    fun profileClick(v: View)
{
    startActivity(Intent(baseContext, OptionsInfoUser::class.java))
}

    fun bioClick(v: View)
    {
        startActivity(Intent(baseContext, OptionsBio::class.java))
    }

    fun photoClick(v: View)
    {
        startActivity(Intent(baseContext, OptionsPicture::class.java))
    }


    fun mailClick(v: View)
    {
        startActivity(Intent(baseContext, OptionsMailPass::class.java))
    }


    fun decoClick(v: View)
    {
        AuthUtils.logout(this)
    }
}
