package gold.android.videoplayer.ui.activity;

import android.os.Bundle;
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
import gold.android.videoplayer.utils.DataFactory;
import gold.android.videoplayer.utils.Logger;
import java.util.List;


public class VideoListPlayerActivity extends BaseActivity {

    private List<String> urls= DataFactory.getInstance().getDataSources();//视频列表集合
    private int mPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        TitleView titleView = (TitleView) findViewById(R.id.title_view);
        titleView.setTitle(getIntent().getStringExtra("title"));
        titleView.setOnTitleActionListener(new TitleView.OnTitleActionListener() {
            @Override
            public void onBack() {
                onBackPressed();
            }
        });
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
        mVideoPlayer = (VideoPlayer) findViewById(R.id.video_player);
        findViewById(R.id.player_container).getLayoutParams().height= getResources().getDisplayMetrics().widthPixels * 9 /16;
        VideoController controller = mVideoPlayer.createController();//绑定默认的控制器
        WidgetFactory.bindDefaultControls(controller);
//        controller.setPreViewTotalDuration("3600");//注意:设置虚拟总时长(一旦设置播放器内部走片段试看流程)
        //如果适用自定义解码器则必须实现setOnPlayerActionListener并返回一个多媒体解码器
        mVideoPlayer.setOnPlayerActionListener(new OnPlayerEventListener() {
            /**
             * 创建一个自定义的播放器,返回null,则内部自动创建一个默认的解码器
             * @return
             */
            @Override
            public AbstractMediaPlayer createMediaPlayer() {
                return ExoPlayerFactory.create().createPlayer(VideoListPlayerActivity.this);
            }

            @Override
            public void onPlayerState(PlayerState state, String message) {
                Logger.d(TAG,"onPlayerState-->state:"+state+",message:"+message);
                if(null!=mVideoPlayer){
                    if(PlayerState.STATE_COMPLETION==state){
                        //尝试播放下一个视频
                        String url = getUrl(mPosition);
                        Logger.d(TAG,"onPlayerState-->url:"+url);
                        if(null!=url){
                            mVideoPlayer.onReset();
                            mVideoPlayer.setDataSource(url);
                            mPosition+=1;
                            mVideoPlayer.prepareAsync();
                        }else{
                            Logger.d(TAG,"onPlayerState-->播放到底了");
                        }
                    }
                }
            }
        });
        mVideoPlayer.setLoop(false);//连续播放模式下只能设置为false
        mVideoPlayer.setProgressCallBackSpaceMilliss(300);
        controller.setTitle("测试播放地址");//视频标题(默认视图控制器横屏可见)
        mVideoPlayer.setDataSource(getUrl(mPosition));//播放地址设置
        mVideoPlayer.setContinuityPlay(true);//告诉播放器连续播放模式,此模式下播放器内部播放完成后不会自动退出全屏\小窗口\悬浮窗口等.
//        直到播放到最后一个地址的时候，需告诉播放器关闭连续播放模式
        mPosition+=1;
        mVideoPlayer.prepareAsync();//开始异步准备播放
    }

    /**
     * 根据下标返回播放地址
     * @param position
     * @return
     */
    private String getUrl(int position) {
        if(urls.size()>position){
            if((urls.size()-1)==position){
                Logger.d(TAG,"只剩最后一个了");
                mVideoPlayer.setContinuityPlay(false);
            }
            return urls.get(position);
        }
        //越界了，结束播放
        return null;
    }
}