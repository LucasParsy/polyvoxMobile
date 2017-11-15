package com.tuxlu.polyvox.Homepage

import android.content.Context
import android.provider.Settings.Global.getString
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by tuxlu on 15/11/17.
 */

open class PagerAdapter(fm: FragmentManager, private val fragments: List<Fragment>,
                        private val tabTitles: IntArray,
                        private val context: Context) : FragmentPagerAdapter(fm) {

    override fun getPageTitle(position: Int): CharSequence {
        return context.resources.getString(tabTitles[position])
    }

    override fun getItem(position: Int): Fragment {
        return this.fragments[position]
    }

    override fun getCount(): Int {
        return this.fragments.size
    }

}
