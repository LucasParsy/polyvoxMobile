package com.tuxlu.polyvox.Room;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.tuxlu.polyvox.R;
import com.tuxlu.polyvox.Utils.Auth.AuthUtils;
import com.tuxlu.polyvox.Utils.UIElements.PagerAdapter;
import com.tuxlu.polyvox.Utils.UtilsTemp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by tuxlu on 16/09/17.
 */

public class Room extends AppCompatActivity {

    private static final int[] tabTitles = {R.string.tab_chat, R.string.tab_queue, R.string.tab_files};
    private int id;
    private String title;
    @BindView(R.id.videoPlayerLayout)
    protected FrameLayout videoPlayerLayout;
    @BindView(R.id.videoPlayerView)
    protected SimpleExoPlayerView videoPlayerView;

    @BindView(R.id.player_button_fullscreen)
    protected AppCompatImageButton fullscreenButton;

    private SimpleExoPlayer player;
    private IRCChat ircChat;

    protected void onCreate(Bundle savedInstanceState) {
        Bundle b = getIntent().getExtras();
        assert b != null;
        id = b.getInt("id");
        title = b.getString("title");
        setContentView(R.layout.activity_room);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        setVideoPlayer();
        onConfigurationChanged(this.getResources().getConfiguration());
        String username = AuthUtils.getUsername(getApplicationContext());


        Fragment roomChat = Fragment.instantiate(this, RoomChat.class.getName());
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("title", title);
        roomChat.setArguments(bundle);

        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(roomChat);
        //todo: gérer autres fragments
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), fragments, tabTitles, this);
        ViewPager pager = ((ViewPager)findViewById(R.id.pager));
        pager.setAdapter(adapter);
        ((TabLayout)findViewById(R.id.tabLayoutHome)).setupWithViewPager(pager);
    }

    private void setVideoPlayer() {
        Context context = getBaseContext();
        //Uri videoUrl = Uri.parse(APIUrl.BASE_URL + APIUrl.VIDEO_STREAM + "/" + id);
        //Uri videoUrl = Uri.parse("http://www-itec.uni-klu.ac.at/ftp/datasets/DASHDataset2014/BigBuckBunny/2sec/BigBuckBunny_2s_simple_2014_05_09.mpd");
        //Uri videoUrl = Uri.parse("http://vm2.dashif.org/livesim-dev/periods_60/xlink_30/insertad_3/testpic_2s/Manifest.mpd");
        Uri videoUrl = Uri.parse("http://yt-dash-mse-test.commondatastorage.googleapis.com/media/feelings_vp9-20130806-manifest.mpd");


        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, getResources().getString(R.string.app_name)), bandwidthMeter);
// This is the MediaSource representing the media to be played.
        DashMediaSource videoSource = new DashMediaSource(videoUrl, dataSourceFactory,
                new DefaultDashChunkSource.Factory(dataSourceFactory), null, null);
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

        player.prepare(videoSource);
        videoPlayerView.setPlayer(player);
        player.setPlayWhenReady(true);

    }

    @OnClick(R.id.player_button_share)
    public void shareStream(View v) {
        //Todo Un jour, si on devient riche, recoder tout le partage de fichiers pour faire un beau thème.
        String url = "https://polyvox.fr/stream/" + id;
        String body = getString(R.string.share_stream_body) + "\n" + title + "\n" + getString(R.string.join_me);
        UtilsTemp.shareContent(this, body, url);
    }

    @OnClick(R.id.player_button_fullscreen)
    public void setScreenOrientation(View v) {
        // orientation ? portrait : landscape;
        boolean orientation = (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
        setRequestedOrientation(orientation ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            videoPlayerLayout.getLayoutParams().height = (int) getResources().getDimension(R.dimen.room_video_player_size);
            fullscreenButton.setImageResource(R.drawable.ic_fullscreen_expand_24dp);
        }
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            videoPlayerLayout.getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
            fullscreenButton.setImageResource(R.drawable.ic_fullscreen_skrink_24dp);
            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            //WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        videoPlayerLayout.requestLayout();
    }

    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
        setRequestedOrientation(this.getResources().getConfiguration().orientation);

        /*
        pour vidéo enregistrée:
        private long position; //dans classe
        position = player.getCurrentPosition(); //ici, dans onPause
        player.seekTo(position); // dans OnResume
        */
    }

    protected void onResume() {
        super.onResume();
        player.setPlayWhenReady(true);
    }

}
