package com.tuxlu.polyvox.User

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import com.android.volley.Request
import com.tuxlu.polyvox.Homepage.Home
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import android.content.DialogInterface
import android.support.design.widget.TabLayout
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.ImageView
import com.tuxlu.polyvox.Options.Options
import com.tuxlu.polyvox.Search.SearchUserRecycler
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.Auth.AuthUtils
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp
import com.tuxlu.polyvox.Utils.UIElements.MyAppCompatActivity


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


class ProfilePage() : MyAppCompatActivity() {
    private var user: ProfileUser = ProfileUser()
    private lateinit var toolbarIcon: MenuItem
    private lateinit var followButton: Button
    private var followerNumbers: Int = 0
    private lateinit var followerNumbersText: TextView
    private lateinit var currentUserName: String
    private var connected: Boolean = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        connected = APIRequest.isAPIConnected(this)
        followButton = findViewById(R.id.ProfileActionButton)
        followerNumbersText = findViewById(R.id.ProfileFollowerNumber)

        val userName = intent.getStringExtra("name")

        APIRequest.JSONrequest(this, Request.Method.GET,
                APIUrl.BASE_URL + APIUrl.INFO_USER + userName,
                connected, null, { response ->
            val topObj = response.getJSONObject(APIUrl.SEARCH_USER_JSONOBJECT)
            user.isCurrentUser = topObj.getBoolean("isMyProfile");
            val info = topObj.getJSONObject("info")
            user.userName = info.getString("userName")
            user.firstName = info.getString("firstName")
            user.lastName = info.getString("lastName")
            user.description = info.getString("description")
            user.picture = info.getString("picture")

            if (connected && user.isCurrentUser == false) {
                APIRequest.JSONrequest(this, Request.Method.GET, APIUrl.BASE_URL + APIUrl.INFO_CURRENT_USER,
                        true, null, { current ->
                    val obj = current.getJSONObject(APIUrl.SEARCH_USER_JSONOBJECT)
                    currentUserName = obj.getString("userName")
                    setupViewPager(topObj)
                }, null)
            } else
                setupViewPager(topObj)
        }, null)
    }


    private fun setupViewPager(obj: JSONObject) {
        val pager: ViewPager = findViewById(R.id.pager)
        val adapter: PagerAdapter
        val followers = Fragment.instantiate(this, SearchUserRecycler::class.java.name) as SearchUserRecycler
        val followed = Fragment.instantiate(this, SearchUserRecycler::class.java.name) as SearchUserRecycler

        val followersData: JSONArray = obj.getJSONArray("followers")
        followerNumbers = followersData.length()

        //check if following user
        if (connected && !user.isCurrentUser) {
            for (i in 0..(followersData.length() - 1)) {
                val item = followersData.getJSONObject(i)
                if (item.getString("userName") == currentUserName) {
                    user.following = true
                    break
                }
            }
        }

        followers.setNoResViewVisibility(false)
        followed.setNoResViewVisibility(false)

        val fragments = mutableListOf<Fragment>()
        fragments.add(followers)
        fragments.add(followed)

        val tabTitles = intArrayOf(R.string.followers, R.string.followed)
        adapter = com.tuxlu.polyvox.Utils.UIElements.PagerAdapter(supportFragmentManager, fragments, tabTitles, this)
        pager.adapter = adapter
        (findViewById<View>(R.id.tabLayoutHome) as TabLayout).setupWithViewPager(pager)

        (fragments[0] as SearchUserRecycler).add(followersData)
        (fragments[1] as SearchUserRecycler).add(obj.getJSONArray("followed"))

        setupPage()
    }


    private fun setupPage() {
        title = user.userName

        val bio: TextView = findViewById<TextView>(R.id.ProfileBio)
        if (!UtilsTemp.isStringEmpty(user.description))
            bio.text = user.description
        else
            bio.text = getString(R.string.bio_empty)

        val image = (findViewById<ImageView>(R.id.ProfileIcon))
        if (!UtilsTemp.isStringEmpty(user.picture))
            GlideApp.with(this).load(user.picture).into(image)
        else {
            image.setImageResource(R.drawable.ic_account_circle_white_24dp)
        }

        followerNumbersText.text = followerNumbers.toString()
        setButtonsText()



        followButton.setOnClickListener {
            if (user.isCurrentUser)
                startActivityForResult(Intent(baseContext, Options::class.java), 1234)
            else
                followButtonlistener()
        }
    }

    private fun setButtonsText() {
        if (user.isCurrentUser) {
            toolbarIcon.setIcon(R.drawable.logout)
            followButton.text = getString(R.string.modify_profile)
        } else {
            if (user.following) {
                toolbarIcon.setIcon(R.drawable.hearth_full)
                followButton.text = getString(R.string.unfollow)
            } else {
                toolbarIcon.setIcon(R.drawable.hearth_empty)
                followButton.text = getString(R.string.follow)
            }
        }
    }


    public fun logout() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.logout_confirm))
                .setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener { _, _ ->
                    APIRequest.JSONrequest(this, Request.Method.GET,
                            APIUrl.BASE_URL + APIUrl.LOGOUT,
                            true, null, { _ ->
                        AuthUtils.removeAccountLogout(this)
                        val nin: Intent = Intent(baseContext, Home::class.java)
                        nin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(nin)
                    }, null)
                })
                .setNegativeButton(getString(R.string.no), null)
                .show()
    }

    private fun followButtonlistener() {
        var map = HashMap<String, String>()
        map.put("userName", user.userName)
        val url = NetworkUtils.getParametrizedUrl(APIUrl.FOLLOW, map)
        if (user.following)
            unfollow(url)
        else
            follow(url)
    }

    public fun follow(url: String) {
        APIRequest.JSONrequest(this, Request.Method.POST,
                url, true, null, { _ ->
            user.following = true
            setButtonsText()
            UtilsTemp.showToast(this, user.userName + getString(R.string.is_followed), ToastType.SUCCESS)
            followerNumbers++
            followerNumbersText.text = followerNumbers.toString()
        }, null)

    }

    public fun unfollow(url: String) {
        APIRequest.JSONrequest(this, Request.Method.DELETE,
                url, true, null, { _ ->
            user.following = false
            setButtonsText()
            UtilsTemp.showToast(this, user.userName + getString(R.string.is_no_more_followed))
            followerNumbers--
            followerNumbersText.text = followerNumbers.toString()
        }, null)
    }


    public override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        toolbarIcon = menu.findItem(R.id.icon)

        toolbarIcon.setOnMenuItemClickListener {
            if (user.isCurrentUser)
                logout()
            else
                followButtonlistener()
            true
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        APIRequest.JSONrequest(this, Request.Method.GET,
                APIUrl.BASE_URL + APIUrl.INFO_USER + user.userName,
                connected, null, { response ->
            val topObj = response.getJSONObject(APIUrl.SEARCH_USER_JSONOBJECT)
            val info = topObj.getJSONObject("info")
            user.description = info.getString("description")

            val bio: TextView = findViewById<TextView>(R.id.ProfileBio)
            if (!UtilsTemp.isStringEmpty(user.description))
                bio.text = user.description
        }, null)
    }
}
