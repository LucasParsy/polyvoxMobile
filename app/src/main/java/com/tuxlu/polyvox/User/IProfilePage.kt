package com.tuxlu.polyvox.User

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tuxlu.polyvox.R

/**
 * Created by tuxlu on 25/11/17.
 */

abstract class IProfilePage() : AppCompatActivity()
{
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
    }
}