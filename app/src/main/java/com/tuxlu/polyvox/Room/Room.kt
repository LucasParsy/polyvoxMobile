package com.tuxlu.polyvox.Room

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RatingBar
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.Auth.AuthUtils
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp
import com.tuxlu.polyvox.Utils.UIElements.PagerAdapter
import com.tuxlu.polyvox.Utils.UIElements.ResizeWidthAnimation
import com.tuxlu.polyvox.Utils.UtilsTemp
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.exo_stream_playback_control.*
import java.util.*


/**
 * Created by tuxlu on 16/09/17.
 */

interface DialogFragmentInterface {
    // you can define any parameter as per your requirement
    fun dialogDismiss()
}



class Room : AppCompatActivity(), DialogFragmentInterface {
    private var id: Int = 0
    private var title: String? = null

    private var chatVisibilityStatus = View.VISIBLE

    private var player: SimpleExoPlayer? = null

    private var width: Int = 0
    private var currentRatedSpeaker: String? = null
    private var currentRatedSpeakerUrl: String? = null
    private var ratingValue = -1f
    private var hasRated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val b = intent.extras!!
        id = b.getInt("id")
        title = b.getString("title")
        setContentView(R.layout.activity_room)
        ButterKnife.bind(this)
        super.onCreate(savedInstanceState)
        setVideoPlayer()

        val config = this.resources.configuration
        val displayMetrics = resources.displayMetrics
        width = if (config.orientation == Configuration.ORIENTATION_PORTRAIT) displayMetrics.heightPixels else displayMetrics.widthPixels
        //width /= displayMetrics.density;

        onConfigurationChanged(this.resources.configuration)
        val username = AuthUtils.getUsername(applicationContext)


        val roomChat = Fragment.instantiate(this, RoomChat::class.java.name)
        val bundle = Bundle()
        bundle.putString("username", username)
        bundle.putString("title", title)
        roomChat.arguments = bundle

        val filelist = Fragment.instantiate(this, FLRecycler::class.java.name)


        val fragments = ArrayList<Fragment>()
        fragments.add(roomChat)
        fragments.add(filelist)
        //todo: gérer autres fragments
        val adapter = PagerAdapter(supportFragmentManager, fragments, tabTitles, this)
        pager.adapter = adapter
        tabLayoutHome.setupWithViewPager(pager)


        ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, v, _ ->
            ratingValue = v
            closeButton!!.setImageResource(R.drawable.checkmark_valid)
            hasRated = true
        }

        showUserRating("tuxlu", "https://polyvox.fr/public/img/tuxlu42.png")

        player_room_title.text = title
        player_room_subtitle.text = "sous-titre"
    }

    private fun setVideoPlayer() {
        val context = baseContext
        //Uri videoUrl = Uri.parse(APIUrl.BASE_URL + APIUrl.VIDEO_STREAM + "/" + id);
        //Uri videoUrl = Uri.parse("http://www-itec.uni-klu.ac.at/ftp/datasets/DASHDataset2014/BigBuckBunny/2sec/BigBuckBunny_2s_simple_2014_05_09.mpd");
        //Uri videoUrl = Uri.parse("http://vm2.dashif.org/livesim-dev/periods_60/xlink_30/insertad_3/testpic_2s/Manifest.mpd");
        val videoUrl = Uri.parse("http://yt-dash-mse-test.commondatastorage.googleapis.com/media/feelings_vp9-20130806-manifest.mpd")


        val bandwidthMeter = DefaultBandwidthMeter()
        val dataSourceFactory = DefaultDataSourceFactory(context,
                Util.getUserAgent(context, resources.getString(R.string.app_name)), bandwidthMeter)
        // This is the MediaSource representing the media to be played.
        val videoSource = DashMediaSource(videoUrl, dataSourceFactory,
                DefaultDashChunkSource.Factory(dataSourceFactory), null, null)
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector)

        player!!.prepare(videoSource)
        videoPlayerView!!.player = player
        player!!.playWhenReady = true

    }

    @OnClick(R.id.player_button_share)
    fun shareStream(v: View) {
        val url = "https://polyvox.fr/stream/" + id
        val body = getString(R.string.share_stream_body) + "\n" + title + "\n" + getString(R.string.join_me)
        UtilsTemp.shareContent(this, body, url)
    }

    @OnClick(R.id.player_button_fullscreen)
    fun setScreenOrientation(v: View) {
        // orientation ? portrait : landscape;
        val orientation = this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        requestedOrientation = if (orientation) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    @OnClick(R.id.player_button_chat)
    fun setChatVisibility(v: View) {
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
            //todo: find better icons for this button
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
        }
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

            player_button_chat!!.visibility = View.VISIBLE
            rootView!!.orientation = LinearLayout.HORIZONTAL
            chatLayout!!.visibility = chatVisibilityStatus

            videoPlayerLayout!!.layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
            videoPlayerLayout!!.layoutParams.width = (width - width * 0.3).toInt()
            player_button_fullscreen!!.setImageResource(R.drawable.ic_fullscreen_skrink_24dp)
        }
        rootView!!.requestLayout()
    }

    override fun onPause() {
        super.onPause()
        player!!.playWhenReady = false
        requestedOrientation = this.resources.configuration.orientation

        /*
        pour vidéo enregistrée:
        private long position; //dans classe
        position = player.getCurrentPosition(); //ici, dans onPause
        player.seekTo(position); // dans OnResume
        */
    }

    override fun onResume() {
        super.onResume()
        player!!.playWhenReady = true
    }


    private fun showUserRating(user: String, imageUrl: String) {
        currentRatedSpeaker = user
        currentRatedSpeakerUrl = imageUrl;
        videoPlayerView!!.controllerHideOnTouch = false
        videoPlayerView!!.controllerShowTimeoutMs = 500000000;
        player_bottom_buttons_bar.visibility = View.GONE
        commentBarLayout.visibility = View.GONE
        ratingBarLayout.visibility = View.VISIBLE
        //player_bottom_rate_speaker.visibility = View.VISIBLE
        YoYo.with(Techniques.SlideInUp).duration(300).playOn(player_bottom_rate_speaker)
        infoUserName.text = user
        GlideApp.with(this).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.NONE).into(infoUserPicture)
    }

    @OnClick(R.id.closeButton)
    fun closeUserRating(v: View) {
        if (hasRated) {
            /*
            val reasonText = reasonSpinner.selectedItem.toString()
            val body = JSONObject()
            body.put(APIUrl.UPDATE_INF0_BIO, name)
            body.put(???, reasonText)
            body.put(???, input.text)
            APIRequest.JSONrequest(this, Request.Method.POST,
            APIUrl.BASE_URL + APIUrl.???, true, body, { _ ->
                UtilsTemp.showToast(this, getString(R.string.???))
            }, null)
            */

            closeButton!!.setImageResource(R.drawable.grey_cross)
            ratingBarLayout.visibility = View.GONE
            commentBarLayout.visibility = View.VISIBLE
            if (ratingValue <= 2.5)
                reportButton.visibility = View.GONE
            hasRated = false
            return;
        }
        reportButton.visibility = View.VISIBLE;
        player_bottom_buttons_bar.visibility = View.VISIBLE
        YoYo.with(Techniques.SlideOutDown).duration(300).playOn(player_bottom_rate_speaker)
        //player_bottom_rate_speaker.visibility = View.GONE
        videoPlayerView!!.controllerHideOnTouch = true
        videoPlayerView!!.controllerShowTimeoutMs = 5000;
        currentRatedSpeaker = null

    }

    private fun setFragmentArgument(frag: CommentReportBase)
    {
        val bundle = Bundle()
        bundle.putString("name", currentRatedSpeaker)
        bundle.putString("url", currentRatedSpeakerUrl)
        frag.arguments = bundle
    }

    @OnClick(R.id.reportButton)
    fun reportUser(v: View) {
        val frag = UserReportFragment()
        setFragmentArgument(frag)
        frag.show(fragmentManager, getString(R.string.report))
    }

    @OnClick(R.id.commentButton)
    fun commentUserRating(v: View) {
        val frag = UserCommentFragment()
        setFragmentArgument(frag)
        frag.show(fragmentManager, getString(R.string.send_comment))
    }

    @OnClick(R.id.player_button_back)
    fun onClose(v: View) {
        finish();
    }

    //reloads page when logging
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AuthUtils.AUTH_REQUEST_CODE && resultCode == Activity.RESULT_OK)
            this.recreate();
    }

    override fun dialogDismiss() {closeUserRating(reportButton) }


    companion object {

        private val tabTitles = intArrayOf(R.string.tab_chat, R.string.tab_files, R.string.tab_queue)
    }


}
