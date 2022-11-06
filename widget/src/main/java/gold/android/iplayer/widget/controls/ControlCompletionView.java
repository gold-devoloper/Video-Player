package gold.android.iplayer.widget.controls;

import android.content.Context;
import android.view.View;
import gold.android.iplayer.widget.R;
import gold.android.iplayer.base.BaseControlWidget;
import gold.android.iplayer.model.PlayerState;

public class ControlCompletionView extends BaseControlWidget {

    public ControlCompletionView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.player_control_completion;
    }

    @Override
    public void initViews() {
        hide();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null!=mControlWrapper) mControlWrapper.togglePlay();
            }
        });
    }

    @Override
    public void onPlayerState(PlayerState state, String message) {
        switch (state) {
            case STATE_COMPLETION://播放结束
                if(!isWindowScene()&&!isPreViewScene()){//窗口播放模式/试看模式不显示
                    show();
                }
                break;
            default:
                hide();
        }
    }

    @Override
    public void onOrientation(int direction) {}
}