package com.tuxlu.polyvox.Room

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.cast.framework.CastContext
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Room.Chat.RoomChat
import com.tuxlu.polyvox.Utils.Auth.AuthUtils
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp
import com.tuxlu.polyvox.Utils.UIElements.PagerAdapter
import com.tuxlu.polyvox.Utils.UIElements.ResizeWidthAnimation
import com.tuxlu.polyvox.Utils.UtilsTemp
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.exo_stream_playback_control.*
import kotlin.collections.ArrayList


/**
 * Created by tuxlu on 16/09/17.
 */


abstract class RoomBase : AppCompatActivity() {
    protected var token: String = ""
    protected var title: String = ""
    protected var imageUrl: String = ""

    protected lateinit var fileList: FLRecycler

    protected lateinit var mInterstitialAd: InterstitialAd //setup this in child onCreate with InterstitialAd(this)
    protected lateinit var streaming: Streaming
    private lateinit var castContext: CastContext

    protected open val tabTitles = intArrayOf(R.string.tab_queue, R.string.tab_chat, R.string.tab_files)
    protected open val tabIcons = intArrayOf(R.drawable.ic_mic_black_48dp, R.drawable.ic_forum_black_48dp, R.drawable.document_black)


    private var chatVisibilityStatus = View.VISIBLE
    private var width: Int = 0
    private var transitionHandler = Handler()
    abstract var hasHistory: Boolean;

    fun getRoomChat(username: String): Fragment {
        val roomChat = Fragment.instantiate(this, RoomChat::class.java.name)
        val bundle = Bundle()
        bundle.putString("username", username)
        bundle.putString("token", token)
        bundle.putBoolean("history", hasHistory)
        roomChat.arguments = bundle
        return roomChat
    }

    fun getFileList(): Fragment {
        return Fragment.instantiate(this, FLRecycler::class.java.name)
    }

    fun setUpFragments(fragments: ArrayList<Fragment>) {
        val adapter = PagerAdapter(supportFragmentManager, fragments, tabTitles, this)
        pager.adapter = adapter
        pager.offscreenPageLimit = 3

        tabLayoutHome.setupWithViewPager(pager)
        for (i in 0..(fragments.size - 1)) {
            //Log.wtf("PolyRoom", "FRAGMENT NAME: " + fragments[0]::class::simpleName)
            val tab = tabLayoutHome.getTabAt(i)
            tab!!.setIcon(tabIcons[i])
            tab.icon!!.setColorFilter(ContextCompat.getColor(this, R.color.cornflower_blue_two_24), PorterDuff.Mode.SRC_ATOP)
        }

        if (fragments.size == 2) //sad hack to remove history
        {
            val tab = tabLayoutHome.getTabAt(1)
            tab!!.setIcon(tabIcons[2])
            tab.setText(tabTitles[2])
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportPostponeEnterTransition()
        setupAd()
        castContext = CastContext.getSharedInstance(this)
        val b = intent.extras!!
        title = b.getString("title")!!
        imageUrl = b.getString("imageUrl")!!
        token = b.getString("token")!!
        setContentView(R.layout.activity_room)
        streaming = Streaming(this, castContext, title, imageUrl)
        super.onCreate(savedInstanceState)
        //setVideoPlayer(token)
        setClicklisteners()

        val config = this.resources.configuration
        val displayMetrics = resources.displayMetrics
        width = if (config.orientation == Configuration.ORIENTATION_PORTRAIT) displayMetrics.heightPixels else displayMetrics.widthPixels
        //width /= displayMetrics.density;

        onConfigurationChanged(this.resources.configuration)

        setTransition(title, imageUrl)
        player_room_title.text = title
        //player_room_subtitle.text = "sous-titre"
    }

    private fun setupAd() {
        mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        //mInterstitialAd.adUnitId = "ca-app-pub-4121964947351781/9439952508" //vrai
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                mInterstitialAd.loadAd(AdRequest.Builder().build())
            }
        }

    }

    fun showAd() {
        if (mInterstitialAd.isLoaded && !AuthUtils.getPremiumStatus(this))
            mInterstitialAd.show()
        //todo: comment when sharing,ads are annoying...
    }


    private fun transitionCallback(): Boolean {
        supportStartPostponedEnterTransition()
        transitionHandler.postDelayed({
            roomWaitingTitle.visibility = View.VISIBLE
            YoYo.with(Techniques.SlideInDown).duration(300).playOn(roomWaitingTitle)
        }, 500)
        return false
    }

    private fun setTransition(title: String, imageUrl: String) {
        roomWaitingTitle.text = title
        ViewCompat.setTransitionName(roomWaitingPicture, title)
        if (!UtilsTemp.isStringEmpty(imageUrl)) {
            GlideApp.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .dontAnimate()
                    .listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            return transitionCallback()
                        }

                        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            return transitionCallback()
                        }
                    })
                    .into(roomWaitingPicture)
        } else {
            roomWaitingPicture.setImageDrawable(ContextCompat.getDrawable(this.baseContext, R.drawable.logo_grey))
            roomWaitingPicture.scaleType = ImageView.ScaleType.CENTER_CROP
            transitionCallback()
        }
    }

    private fun setClicklisteners() {
        player_button_share.setOnClickListener { _ -> shareStream() }
        player_button_fullscreen.setOnClickListener { _ -> setScreenOrientation() }
        player_button_chat.setOnClickListener { _ -> setChatVisibility() }
        player_button_back.setOnClickListener { _ -> finish() }
    }

    private fun shareStream() {
        val url = "https://polyvox.fr/stream/$token"
        val body = getString(R.string.share_stream_body) + "\n" + title + "\n" + getString(R.string.join_me)
        UtilsTemp.shareContent(this, body, url)
    }

    private fun setChatVisibility() {
        val animWidth: Int

        if (chatVisibilityStatus == View.VISIBLE) {
            chatVisibilityStatus = View.GONE
            chatLayout!!.visibility = View.GONE
            videoPlayerLayout!!.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            player_button_chat!!.setImageResource(android.R.drawable.stat_notify_chat)
            animWidth = width
        } else {
            chatVisibilityStatus = View.VISIBLE
            chatLayout!!.visibility = View.VISIBLE
            //todo: find better icon for this button
            player_button_chat!!.setImageResource(android.R.drawable.ic_menu_revert)
            animWidth = (width - width * 0.3).toInt()
        }
        val anim = ResizeWidthAnimation(videoPlayerLayout, animWidth)
        anim.duration = 150
        videoPlayerLayout!!.startAnimation(anim)

        //OU ANCIENNE MÉTHODE, SANS ANIM:
        //videoPlayerLayout.getLayoutParams().width = animWidth;

        //ou plus Yolo: transformer rootView en FrameLayout,
        //challayout height = heigh - videoPlayer height
        //et chatLayout Gravity = bottom
        //quand rotation écran: Gravity = right
        //chat apparait alors en surimpression sur le stream
    }

    private fun setScreenOrientation() {
        // orientation ? portrait : landscape;
        val orientation = this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

        if (orientation) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        requestedOrientation = if (orientation) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onConfigurationChanged(config: Configuration) {
        super.onConfigurationChanged(config)
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)

            player_button_chat!!.visibility = View.GONE
            rootView!!.orientation = LinearLayout.VERTICAL
            chatLayout!!.visibility = View.VISIBLE
            chatLayout!!.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            chatLayout!!.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT

            videoPlayerLayout!!.layoutParams.height = resources.getDimension(R.dimen.room_video_player_size).toInt()
            videoPlayerLayout!!.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            player_button_fullscreen!!.setImageResource(R.drawable.ic_fullscreen_expand_24dp)


            for (i in 0..(tabLayoutHome.tabCount - 1))
                tabLayoutHome.getTabAt(i)!!.text = getString(tabTitles[i])

            chatVisibilityStatus = View.VISIBLE
            player_button_chat!!.setImageResource(android.R.drawable.ic_menu_revert)
        }
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

            player_button_chat!!.visibility = View.VISIBLE
            rootView!!.orientation = LinearLayout.HORIZONTAL
            chatLayout!!.visibility = chatVisibilityStatus

            videoPlayerLayout!!.layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT
            videoPlayerLayout!!.layoutParams.width = (width - width * 0.3).toInt()
            player_button_fullscreen!!.setImageResource(R.drawable.ic_fullscreen_skrink_24dp)

            for (i in 0..(tabLayoutHome.tabCount - 1))
                tabLayoutHome.getTabAt(i)!!.text = ""
        }
        rootView!!.requestLayout()
    }

    override fun onPause() {
        super.onPause()
        streaming.setPlayOnReady(false)
        //requestedOrientation = this.resources.configuration.orientation
        /*
        pour vidéo enregistrée:
        private long position; //dans classe
        position = player.getCurrentPosition(); //ici, dans onPause
        player.seekTo(position); // dans OnResume
        */
    }

    override fun onBackPressed() {
        super.onBackPressed()
        videoPlayerView.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        streaming.setPlayOnReady(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        transitionHandler.removeCallbacksAndMessages(null)
    }

    //reloads page when logging
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AuthUtils.AUTH_REQUEST_CODE && resultCode == Activity.RESULT_OK)
            this.recreate()
    }
}
