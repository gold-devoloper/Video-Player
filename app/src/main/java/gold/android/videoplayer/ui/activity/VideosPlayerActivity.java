package gold.android.videoplayer.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import gold.android.iplayer.manager.IVideoManager;
import gold.android.videoplayer.R;
import gold.android.videoplayer.base.BaseActivity;
import gold.android.videoplayer.base.BasePresenter;
import gold.android.videoplayer.ui.widget.CorePlayerView;
import gold.android.videoplayer.ui.widget.TitleView;
import java.util.ArrayList;
import java.util.List;

public class VideosPlayerActivity extends BaseActivity {

    private LinearLayout mPlayerContainer;
    private List<CorePlayerView> mCorePlayerViews=new ArrayList<>();
    private NestedScrollView mScrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setFullScreen(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos_player);
        /**
         * 多播放器同时工作必须设置： 允许多播放器同时播放
         */
        IVideoManager.getInstance().setInterceptTAudioFocus(false);

        TitleView titleView = (TitleView) findViewById(R.id.title_view);
        titleView.setTitle(getIntent().getStringExtra("title"));
        titleView.setOnTitleActionListener(new TitleView.OnTitleActionListener() {
            @Override
            public void onBack() {
                onBackPressed();
            }
        });

        mScrollView = (NestedScrollView) findViewById(R.id.scroll_view);

        mPlayerContainer = (LinearLayout) findViewById(R.id.player_container);
        mPlayerContainer.setOrientation(LinearLayout.VERTICAL);
        mPlayerContainer.removeAllViews();

        //默认添加一个播放器到容器中
        mCorePlayerViews=new ArrayList<>();
        CorePlayerView corePlayerView=new CorePlayerView(mPlayerContainer.getContext());
        mCorePlayerViews.add(corePlayerView);
        mPlayerContainer.addView(corePlayerView);
        corePlayerView.start(null);

        findViewById(R.id.add_player).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCorePlayerViews.size()>=10){
                    Toast.makeText(getApplicationContext(),"播放器同时添加过多会引起内存溢出噢~",Toast.LENGTH_SHORT).show();
                }
                CorePlayerView corePlayerView=new CorePlayerView(mPlayerContainer.getContext());
                //每次布局发生了变化都自动滚动到底部
                mPlayerContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mPlayerContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mScrollView.fullScroll(NestedScrollView.FOCUS_DOWN);//滚动到底部
                        //mScrollView.fullScroll(NestedScrollView.FOCUS_UP);//滚动到顶部
                    }
                });
                mCorePlayerViews.add(corePlayerView);
                mPlayerContainer.addView(corePlayerView);
                corePlayerView.start("http://vfx.mtime.cn/Video/2019/02/04/mp4/190204084208765161.mp4");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(null!=mCorePlayerViews){
            for (CorePlayerView corePlayerView : mCorePlayerViews) {
                corePlayerView.onResume();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null!=mCorePlayerViews){
            for (CorePlayerView corePlayerView : mCorePlayerViews) {
                corePlayerView.onPause();
            }
        }
    }

    @Override
    public void onBackPressed() {
        boolean isBackPressed=true;
        if(null!=mCorePlayerViews){
            for (CorePlayerView corePlayerView : mCorePlayerViews) {
                boolean backPressed = corePlayerView.isBackPressed();
                if(!backPressed){
                    isBackPressed=false;
                }
            }
        }
        if(isBackPressed){
            super.onBackPressed();
        }
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IVideoManager.getInstance().setInterceptTAudioFocus(true);//禁止多播放器同时播放
        if(null!=mCorePlayerViews){
            for (CorePlayerView corePlayerView : mCorePlayerViews) {
                corePlayerView.onDestroy();
            }
            mCorePlayerViews.clear();
        }
    }
}