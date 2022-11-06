package gold.android.videoplayer.video.ui.widget;

import android.content.Context;
import android.view.View;
import gold.android.iplayer.base.BaseControlWidget;
import gold.android.iplayer.model.PlayerState;
import gold.android.videoplayer.R;


public class ControPerviewView extends BaseControlWidget implements View.OnClickListener {

    public ControPerviewView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.controller_perview;
    }

    @Override
    public void initViews() {
        hide();
        findViewById(R.id.btn_buy).setOnClickListener(this);
        findViewById(R.id.btn_vip_buy).setOnClickListener(this);
    }

    @Override
    public void onPlayerState(PlayerState state, String message) {
        super.onPlayerState(state, message);
        switch (state) {
            case STATE_COMPLETION:
                if(isPreViewScene()){//试看模式显示
                    show();
                }
                break;
            default:
                hide();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_buy:
                if(null!=mOnEventListener) mOnEventListener.onBuy();
                break;
            case R.id.btn_vip_buy:
                if(null!=mOnEventListener) mOnEventListener.onVipBuy();
                break;
        }
    }

    private OnEventListener mOnEventListener;

    public void setOnEventListener(OnEventListener onEventListener) {
        mOnEventListener = onEventListener;
    }

    public interface OnEventListener{
        void onBuy();
        void onVipBuy();
    }
}