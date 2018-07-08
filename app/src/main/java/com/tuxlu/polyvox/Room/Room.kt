package com.tuxlu.polyvox.Room

//import com.google.android.exoplayer2.source.dash.DashMediaSource
//import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.android.volley.Request
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
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


class Room : AppCompatActivity(), DialogFragmentInterface {
    private var token: String = "";
    private var title: String? = null

    private var chatVisibilityStatus = View.VISIBLE

    private var player: SimpleExoPlayer? = null
    private lateinit var dataSourceFactory: DefaultDataSourceFactory;

    private var width: Int = 0

    private var manifestHandler: Handler = Handler()
    private val manifestRunnable: Runnable = Runnable { getManifest() };
    private var streamUrl: String = ""

    public lateinit var userRate: UserRating
    private lateinit var waitlist: RoomWaitlist
    private lateinit var fileList: FLRecycler

    override fun onCreate(savedInstanceState: Bundle?) {
        supportPostponeEnterTransition();

        val b = intent.extras!!
        title = b.getString("title")
        val imageUrl = b.getString("imageUrl")
        token = b.getString("token")
        setContentView(R.layout.activity_room)
        super.onCreate(savedInstanceState)
        manifestHandler.post(manifestRunnable)
        //setVideoPlayer(token)
        userRate = UserRating(this, token)
        setClicklisteners()

        val config = this.resources.configuration
        val displayMetrics = resources.displayMetrics
        width = if (config.orientation == Configuration.ORIENTATION_PORTRAIT) displayMetrics.heightPixels else displayMetrics.widthPixels
        //width /= displayMetrics.density;

        onConfigurationChanged(this.resources.configuration)
        val username = AuthUtils.getUsername(applicationContext)

        setTransition(title!!, imageUrl)

        val roomChat = Fragment.instantiate(this, RoomChat::class.java.name)
        val bundle = Bundle()
        bundle.putString("username", username)
        bundle.putString("title", token)
        roomChat.arguments = bundle


        fileList = Fragment.instantiate(this, FLRecycler::class.java.name) as FLRecycler


        val bundle2 = Bundle()
        bundle2.putString("token", token)
        waitlist = Fragment.instantiate(this, RoomWaitlist::class.java.name) as RoomWaitlist
        waitlist.arguments = bundle2


        val fragments = ArrayList<Fragment>()
        fragments.add(roomChat)
        fragments.add(fileList)
        fragments.add(waitlist)
        val adapter = PagerAdapter(supportFragmentManager, fragments, tabTitles, this)
        pager.adapter = adapter
        pager.offscreenPageLimit = 3
        tabLayoutHome.setupWithViewPager(pager)


        player_room_title.text = title
        player_room_subtitle.text = "sous-titre"
    }

    private fun getManifest() {
        APIRequest.JSONrequest(baseContext, Request.Method.GET,
                APIUrl.BASE_URL + APIUrl.ROOM + token + APIUrl.ROOM_MANIFEST, false, null, { response ->
            val data = response.getJSONObject("data")
            try {
                waitlist.update(data);
                fileList.add(data, true)
            } catch (e: Exception) {
            }


            //userRate.showUserRating("tuxlu42", "https://polyvox.fr/public/img/tuxlu42.png");


            val nUrl = data.getString("videosData");
            if (!UtilsTemp.isStringEmpty(nUrl) && nUrl != streamUrl) {
                videoPlayerView.visibility = View.VISIBLE
                setVideoPlayer(nUrl)
            }
            manifestHandler.postDelayed(manifestRunnable, 2000)
        },
                {
                    manifestHandler.postDelayed(manifestRunnable, 2000)
                })
    }


    private fun setVideoPlayer(token: String) {
        val context = baseContext
        //Uri videoUrl = Uri.parse(APIUrl.BASE_URL + APIUrl.VIDEO_STREAM + "/" + id);
        //Uri videoUrl = Uri.parse("http://www-itec.uni-klu.ac.at/ftp/datasets/DASHDataset2014/BigBuckBunny/2sec/BigBuckBunny_2s_simple_2014_05_09.mpd");
        //Uri videoUrl = Uri.parse("http://vm2.dashif.org/livesim-dev/periods_60/xlink_30/insertad_3/testpic_2s/Manifest.mpd");
        var videoUrl: Uri = if (token == "black")
            Uri.parse("http://yt-dash-mse-test.commondatastorage.googleapis.com/media/feelings_vp9-20130806-manifest.mpd")
        else
            Uri.parse(token);
        //Uri.parse(APIUrl.BASE_URL + APIUrl.ROOM + token + APIUrl.ROOM_STREAM_SUFFIX);
        streamUrl = token;

        if (player == null) {
            val bandwidthMeter = DefaultBandwidthMeter()
            dataSourceFactory = DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, resources.getString(R.string.app_name)), bandwidthMeter)
            // This is the MediaSource representing the media to be played.
            //val videoSource =  HlsMediaSource(videoUrl, dataSourceFactory,
            //DefaultDashChunkSource.Factory(dataSourceFactory), null, null)
            val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
            val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
            player = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
            videoPlayerView!!.player = player
        }
        val videoSource = HlsMediaSource(videoUrl, dataSourceFactory,
                null, null)
        player!!.prepare(videoSource)
        player!!.playWhenReady = true

        val listener = object : Player.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}
            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {}
            override fun onPlayerError(error: ExoPlaybackException?) {}
            override fun onLoadingChanged(isLoading: Boolean) {}
            override fun onPositionDiscontinuity() {}
            override fun onRepeatModeChanged(repeatMode: Int) {}
            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {}
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

                if (playWhenReady && playbackState == Player.STATE_READY) {//playing {
                    videoPlayerView.visibility = View.VISIBLE
                    roomWaitingLayout.visibility = View.INVISIBLE
                } else if (playWhenReady) //buffering
                {

                } else {
                    videoPlayerView.visibility = View.INVISIBLE
                    roomWaitingLayout.visibility = View.VISIBLE

                }
            }
        }
        player!!.addListener(listener)
    }

    private fun setTransition(title: String, imageUrl: String)
    {
        roomWaitingTitle.text = title
        ViewCompat.setTransitionName(roomWaitingPicture, title);
        if (!UtilsTemp.isStringEmpty(imageUrl)) {
            GlideApp.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .dontAnimate()
                    .listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            supportStartPostponedEnterTransition();
                            return false;
                        }

                        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            supportStartPostponedEnterTransition();
                            return false;
                        }
                    })
                    .into(roomWaitingPicture)
        } else {
            roomWaitingPicture.setImageDrawable(resources.getDrawable(R.drawable.logo_grey))
            roomWaitingPicture.scaleType = ImageView.ScaleType.CENTER_CROP
            supportStartPostponedEnterTransition()
        }
    }

    public fun stopPlayer() {
        if (player != null)
            player!!.stop()
    }

    private fun setClicklisteners() {
        player_button_share.setOnClickListener({ v -> shareStream(v) })
        player_button_fullscreen.setOnClickListener({ v -> setScreenOrientation(v) })
        player_button_chat.setOnClickListener({ v -> setChatVisibility(v) })
        player_button_back.setOnClickListener({ _ -> finish() })
    }

    fun shareStream(v: View) {
        val url = "https://polyvox.fr/stream/$token"
        val body = getString(R.string.share_stream_body) + "\n" + title + "\n" + getString(R.string.join_me)
        UtilsTemp.shareContent(this, body, url)
    }

    private fun setChatVisibility(v: View) {
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

    fun setScreenOrientation(v: View) {
        // orientation ? portrait : landscape;
        val orientation = this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
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
        }
        rootView!!.requestLayout()
    }

    override fun onPause() {
        super.onPause()
        player?.playWhenReady = false
        requestedOrientation = this.resources.configuration.orientation

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
        player?.playWhenReady = true
    }

    override fun onDestroy() {
        super.onDestroy()
        manifestHandler.removeCallbacks(manifestRunnable)
    }

    //reloads page when logging
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AuthUtils.AUTH_REQUEST_CODE && resultCode == Activity.RESULT_OK)
            this.recreate();
    }

    override fun dialogDismiss() {
        userRate.closeUserRating(reportButton)
    }


    companion object {

        private val tabTitles = intArrayOf(R.string.tab_chat, R.string.tab_files, R.string.tab_queue)
    }


}
