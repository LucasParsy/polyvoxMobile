package com.tuxlu.polyvox.User

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import com.android.volley.Request
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tuxlu.polyvox.Options.Options
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Search.SearchUserRecycler
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.Auth.AuthUtils
import com.tuxlu.polyvox.Utils.Auth.AuthUtils.logout
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp
import com.tuxlu.polyvox.Utils.NetworkUtils
import com.tuxlu.polyvox.Utils.ToastType
import com.tuxlu.polyvox.Utils.UIElements.MyAppCompatActivity
import com.tuxlu.polyvox.Utils.UtilsTemp
import kotlinx.android.synthetic.main.activity_user.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


/**
 * Created by tuxlu on 25/11/17.
 */

data class ProfileUser(var isCurrentUser: Boolean = false,
                       var userName: String = "",
                       var firstName: String = "",
                       var lastName: String = "",
                       var description: String = "",
                       var picture: String = "",
                       var following: Boolean = false)


class ProfilePage : MyAppCompatActivity() {
    private var user: ProfileUser = ProfileUser()
    private lateinit var toolbarIcon: MenuItem
    private var followerNumbers: Int = 0
    private var connected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        setLayoutOrientation(resources.configuration.orientation)
        connected = APIRequest.isAPIConnected(this)

        val userName = intent.getStringExtra("name")
        val map = HashMap<String, String>()
        map.put("userName", userName)
        val url = NetworkUtils.getParametrizedUrl(APIUrl.INFO_USER, map)

        APIRequest.JSONrequest(this, Request.Method.GET, url,
                connected, null, { response ->
            val topObj = response.getJSONObject(APIUrl.SEARCH_USER_JSONOBJECT)
            user.isCurrentUser = topObj.getBoolean("isMyProfile")
            val info = topObj.getJSONObject("info")
            user.userName = info.getString("userName")
            user.firstName = info.getString("firstName")
            user.lastName = info.getString("lastName")
            user.description = info.getString("description")
            user.picture = info.getString("picture")
            setupViewPager(topObj)
        }, null)
    }


    private fun checkFollowed(followersData: JSONArray) {
        for (i in 0..(followersData.length() - 1)) {
            val item = followersData.getJSONObject(i)
            if (item.getString("userName") == AuthUtils.getUsername(baseContext)) {
                user.following = true
                break
            }
        }
    }

    private fun setupViewPager(obj: JSONObject) {
        val adapter: PagerAdapter
        val followersData: JSONArray = obj.getJSONArray("followers")
        val followers = Fragment.instantiate(this, SearchUserRecycler::class.java.name) as SearchUserRecycler
        val followed = Fragment.instantiate(this, SearchUserRecycler::class.java.name) as SearchUserRecycler

        followers.setNoResViewVisibility(false)
        followed.setNoResViewVisibility(false)

        val fragments = mutableListOf<Fragment>()
        fragments.add(followers)
        fragments.add(followed)
        val tabTitles = intArrayOf(R.string.followers, R.string.followed)
        adapter = com.tuxlu.polyvox.Utils.UIElements.PagerAdapter(supportFragmentManager, fragments, tabTitles, this)
        pager.adapter = adapter
        tabLayoutHome.setupWithViewPager(pager)


        followerNumbers = followersData.length()
        ProfileFollowerNumber.text = followerNumbers.toString()
        if (connected && !user.isCurrentUser)
            checkFollowed(followersData)

        (fragments[0] as SearchUserRecycler).add(followersData)
        (fragments[1] as SearchUserRecycler).add(obj.getJSONArray("followed"))
        setupPage()
    }


    private fun setupPage() {
        title = user.userName

        if (!UtilsTemp.isStringEmpty(user.description))
            ProfileBio.text = user.description
        else
            ProfileBio.text = getString(R.string.bio_empty)

        if (!UtilsTemp.isStringEmpty(user.picture))
            GlideApp.with(this).load(user.picture).diskCacheStrategy(DiskCacheStrategy.NONE).into(ProfileIcon)
        else
            ProfileIcon.setImageResource(R.drawable.ic_account_circle_white_24dp)
        setButtonsText()



        ProfileActionButton.setOnClickListener {
            if (user.isCurrentUser)
                startActivityForResult(Intent(baseContext, Options::class.java), 1234)
            else
                follow(!user.following)
        }
    }

    private fun setButtonsText() {
        if (user.isCurrentUser) {
            toolbarIcon.setIcon(R.drawable.logout)
            ProfileActionButton.text = getString(R.string.modify_profile)
        } else {
            if (user.following) {
                toolbarIcon.setIcon(R.drawable.hearth_full)
                ProfileActionButton.text = getString(R.string.unfollow)
            } else {
                toolbarIcon.setIcon(R.drawable.hearth_empty)
                ProfileActionButton.text = getString(R.string.follow)
            }
        }
    }

    private fun follow(follow: Boolean) {
        val message: Int
        val method: Int
        val increaseVar: Int
        if (follow) {
            method = Request.Method.POST
            increaseVar = 1
            message = R.string.is_followed
        } else {
            method = Request.Method.DELETE
            increaseVar = -1
            message = R.string.is_no_more_followed
        }

        val map = HashMap<String, String>()
        map.put("userName", user.userName)
        val url = NetworkUtils.getParametrizedUrl(APIUrl.FOLLOW, map)
        APIRequest.JSONrequest(this, method,
                url, true, null, { _ ->
            user.following = follow
            setButtonsText()
            followerNumbers += increaseVar
            UtilsTemp.showToast(this, user.userName + getString(message), ToastType.SUCCESS)
            ProfileFollowerNumber.text = followerNumbers.toString()
        }, null)

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        toolbarIcon = menu.findItem(R.id.icon)

        toolbarIcon.setOnMenuItemClickListener {
            if (user.isCurrentUser)
                logout(this)
            else {
                follow(!user.following)
            }
            true
        }
        return true
    }

    private fun setLayoutOrientation(orientation: Int) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rootView.orientation = LinearLayout.HORIZONTAL
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            rootView.orientation = LinearLayout.VERTICAL
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setLayoutOrientation(newConfig.orientation)
        // Checks the orientation of the screen
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val map = HashMap<String, String>()
        map.put("userName", user.userName)
        val url = NetworkUtils.getParametrizedUrl(APIUrl.INFO_USER, map)

        APIRequest.JSONrequest(this, Request.Method.GET, url,
                connected, null, { response ->
            val topObj = response.getJSONObject(APIUrl.SEARCH_USER_JSONOBJECT)
            val info = topObj.getJSONObject("info")
            user.description = info.getString("description")

            if (!UtilsTemp.isStringEmpty(user.description))
                ProfileBio.text = user.description
        }, null)
    }
}
