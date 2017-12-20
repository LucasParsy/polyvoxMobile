package com.tuxlu.polyvox.Utils.UIElements

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup



/**
 * Created by tuxlu on 15/11/17.
 */

open class PagerAdapter(private val fm: FragmentManager, private val fragments: MutableList<Fragment>,
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

    /*
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val ret = super.instantiateItem(container, position)
        this.fragments[position] = ret as Fragment
        return ret
    }
   */
}

