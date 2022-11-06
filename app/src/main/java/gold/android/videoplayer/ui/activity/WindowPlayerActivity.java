package gold.android.videoplayer.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import gold.android.iplayer.base.AbstractMediaPlayer;
import gold.android.iplayer.controller.VideoController;
import gold.android.iplayer.listener.OnPlayerEventListener;
import gold.android.iplayer.media.core.ExoPlayerFactory;
import gold.android.iplayer.model.PlayerState;
import gold.android.iplayer.widget.VideoPlayer;
import gold.android.iplayer.widget.WidgetFactory;
import gold.android.videoplayer.R;
import gold.android.videoplayer.base.BaseActivity;
import gold.android.videoplayer.base.BasePresenter;
import gold.android.videoplayer.ui.widget.TitleView;
import gold.android.videoplayer.utils.Logger;
import gold.android.videoplayer.utils.ScreenUtils;

public class WindowPlayerActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_window_player);
        TitleView titleView = (TitleView) findViewById(R.id.title_view);
        titleView.setTitle(getIntent().getStringExtra("title"));
        titleView.setOnTitleActionListener(new TitleView.OnTitleActionListener() {
            @Override
            public void onBack() {
                onBackPressed();
            }
        });
        findViewById(R.id.btn_mini_window).setVisibility(View.VISIBLE);
        initPlayer();
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    /**
     * 播放器初始化及调用示例
     */
    private void initPlayer() {
        mVideoPlayer = new VideoPlayer(this);
        findViewById(R.id.player_container).getLayoutParams().height= getResources().getDisplayMetrics().widthPixels * 9 /16;
        VideoController controller = mVideoPlayer.createController();//绑定默认的控制器
        WidgetFactory.bindDefaultControls(controller);
        //如果适用自定义解码器则必须实现setOnPlayerActionListener并返回一个多媒体解码器
        mVideoPlayer.setOnPlayerActionListener(new OnPlayerEventListener() {
            /**
             * 创建一个自定义的播放器,返回null,则内部自动创建一个默认的解码器
             * @return
             */
            @Override
            public AbstractMediaPlayer createMediaPlayer() {
                return ExoPlayerFactory.create().createPlayer(WindowPlayerActivity.this);
            }

            @Override
            public void onPlayerState(PlayerState state, String message) {
                Logger.d(TAG,"onPlayerState-->state:"+state+",message:"+message);
            }
        });
//        mVideoPlayer.setPreViewTotalDuration("3600");//注意:设置虚拟总时长(一旦设置播放器内部走片段试看流程)
        mVideoPlayer.setLoop(false);
        mVideoPlayer.setProgressCallBackSpaceMilliss(300);
        controller.setTitle("测试播放地址");//视频标题(默认视图控制器横屏可见)
        mVideoPlayer.setDataSource(MP4_URL2);//播放地址设置
        FrameLayout playerParent = (FrameLayout) findViewById(R.id.player_container_parent);
        playerParent.removeAllViews();
        playerParent.addView(mVideoPlayer, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));
        mVideoPlayer.prepareAsync();//开始异步准备播放
    }

    /**
     * 开启画中画播放
     * @param view
     */
    public void startWindow(View view) {
        if(null!=mVideoPlayer){
            mVideoPlayer.startWindow(ScreenUtils.getInstance().dpToPxInt(3f), Color.parseColor("#99000000"));
        }
    }
}