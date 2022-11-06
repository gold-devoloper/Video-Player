package gold.android.iplayer.widget.controls;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import gold.android.iplayer.widget.R;
import gold.android.iplayer.base.BaseControlWidget;
import gold.android.iplayer.model.PlayerState;


public class ControlLoadingView extends BaseControlWidget {

    private ProgressBar mLoadingView;//加载中
    private View mControllerPlay;//播放按钮

    public ControlLoadingView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.player_control_loading;
    }

    @Override
    public void initViews() {
        mLoadingView = findViewById(R.id.controller_loading);
        mControllerPlay = findViewById(R.id.controller_play);
        mControllerPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePlay();
            }
        });
    }

    @Override
    public void onPlayerState(PlayerState state, String message) {
        switch (state) {
            case STATE_RESET://初始状态\播放器还原重置
            case STATE_STOP://初始\停止
            case STATE_PAUSE://人为暂停中
            case STATE_ON_PAUSE://生命周期暂停中
                changedUi(View.GONE,View.VISIBLE);
                break;
            case STATE_PREPARE://准备中
            case STATE_BUFFER://缓冲中
                changedUi(View.VISIBLE,View.GONE);
                break;
            default:
                changedUi(View.GONE,View.GONE);
        }
    }

    @Override
    public void onOrientation(int direction) {}

    @Override
    public void onPlayerScene(int playerScene) {
        if(isWindowScene(playerScene)){
            hide();
        }else{
            show();
        }
    }

    /**
     * 改变UI状态
     * @param loading 加载中
     * @param playIcon 播放按钮
     */
    private void changedUi(int loading, int playIcon) {
        if(null!= mLoadingView) mLoadingView.setVisibility(loading);
        if(null!=mControllerPlay) mControllerPlay.setVisibility(playIcon);
    }
}
