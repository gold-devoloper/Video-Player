package gold.android.videoplayer.pager.widget;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import gold.android.iplayer.base.BaseControlWidget;
import gold.android.iplayer.model.PlayerState;
import gold.android.videoplayer.R;

public class ControlLiveView extends BaseControlWidget implements View.OnClickListener{

    public ControlLiveView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.controller_live;
    }

    @Override
    public void initViews() {
        findViewById(R.id.live_mute).setOnClickListener(this);
        findViewById(R.id.live_fullscreen).setOnClickListener(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((ImageView) findViewById(R.id.live_mute)).setImageResource(isSoundMute()?R.mipmap.ic_live_mute_true:R.mipmap.ic_live_mute_false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.live_mute:
                toggleMute();
                break;
            case R.id.live_fullscreen:
                toggleFullScreen();
                break;
        }
    }

    @Override
    public void onPlayerState(PlayerState state, String message) {
        super.onPlayerState(state, message);
        if(PlayerState.STATE_START==state){
            findViewById(R.id.live_controller).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMute(boolean isMute) {
        super.onMute(isMute);
        ((ImageView) findViewById(R.id.live_mute)).setImageResource(isMute?R.mipmap.ic_live_mute_true:R.mipmap.ic_live_mute_false);
    }
}
