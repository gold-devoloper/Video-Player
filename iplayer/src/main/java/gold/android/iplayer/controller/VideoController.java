package gold.android.iplayer.controller;

import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;
import gold.android.iplayer.R;
import gold.android.iplayer.model.PlayerState;
import gold.android.iplayer.utils.AnimationUtils;
import gold.android.iplayer.utils.PlayerUtils;


public class VideoController extends GestureController {

    private static final int MESSAGE_CONTROL_HIDE   = 10;//延时隐藏控制器
    private static final int MESSAGE_LOCKER_HIDE    = 11;//延时隐藏屏幕锁
    private static final int DELAYED_INVISIBLE      = 5000;//延时隐藏锁时长
    private static final int MATION_DRAUTION        = 500;//控制器、控制锁等显示\隐藏过渡动画时长(毫秒)
    private View mController;//屏幕锁
    //是否播放(试看)完成\是否开启屏幕锁
    protected boolean isCompletion,isLocked;

    public VideoController(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.player_video_controller;
    }

    @Override
    public void initViews() {
        super.initViews();
        setDoubleTapTogglePlayEnabled(true);//横屏竖屏状态下都允许双击开始\暂停播放
        mController = findViewById(R.id.controller_locker);
        mController.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                stopDelayedRunnable();
                setLocker(isLocked=!isLocked);
                ((ImageView) findViewById(R.id.controller_locker_ic)).setImageResource(isLocked?R.mipmap.ic_player_locker_true:R.mipmap.ic_player_locker_false);
                Toast.makeText(getContext(),isLocked()?getString(R.string.player_locker_true):getString(R.string.player_locker_flase),Toast.LENGTH_SHORT).show();
                if(isLocked){
                    hideWidget(true);//屏幕锁开启时隐藏其它所有控制器
                    startDelayedRunnable(MESSAGE_LOCKER_HIDE);
                }else{
                    showWidget(true);//屏幕锁关闭时显示其它所有控制器
                    startDelayedRunnable(MESSAGE_CONTROL_HIDE);
                }
            }
        });
    }

    @Override
    public void onSingleTap() {
        if(isOrientationPortrait()&&isListPlayerScene()){//竖屏&&列表模式响应单击事件直接处理为开始\暂停播放事件
            if (null != mVideoPlayerControl) mVideoPlayerControl.togglePlay();//回调给播放器
        }else{
            if(isLocked()){
                //屏幕锁显示、隐藏
                toggleLocker();
            }else{
                //控制器显示、隐藏
                toggleController();
            }
        }
    }

    @Override
    public void onDoubleTap() {
        if(!isLocked()){
            if (null != mVideoPlayerControl) mVideoPlayerControl.togglePlay();//回调给播放器
        }
    }

    @Override
    public void onPlayerState(PlayerState state, String message) {
        super.onPlayerState(state,message);
        switch (state) {
            case STATE_RESET://初始状态\播放器还原重置
            case STATE_STOP://初始\停止
                onReset();
                break;
            case STATE_PREPARE://准备中
            case STATE_BUFFER://缓冲中
                break;
            case STATE_START://首次播放
                startDelayedRunnable(MESSAGE_CONTROL_HIDE);
                if(isOrientationLandscape()){//横屏模式下首次播放显示屏幕锁
                    if(null!=mController) mController.setVisibility(View.VISIBLE);
                }
                break;
            case STATE_PLAY://缓冲结束恢复播放
            case STATE_ON_PLAY://生命周期\暂停情况下恢复播放
                startDelayedRunnable(MESSAGE_CONTROL_HIDE);
                break;
            case STATE_PAUSE://人为暂停中
            case STATE_ON_PAUSE://生命周期暂停中
                stopDelayedRunnable();
                break;
            case STATE_COMPLETION://播放结束
                stopDelayedRunnable();
                hideWidget(false);
                hideLockerView();
                break;
            case STATE_MOBILE://移动网络播放(如果设置允许4G播放则播放器内部不会回调此状态)
                break;
            case STATE_ERROR://播放失败
                setLocker(false);
                hideLockerView();
                break;
            case STATE_DESTROY://播放器回收
                onDestroy();
                break;
        }
    }

    /**
     * 竖屏状态下,如果用户设置返回按钮可见仅显示返回按钮,切换到横屏模式下播放时初始都不显示
     * @param orientation 更新控制器方向状态 0:竖屏 1:横屏
     */
    @Override
    public void onScreenOrientation(int orientation) {
        super.onScreenOrientation(orientation);
        if(null!= mController){
            if(isOrientationPortrait()){
                setLocker(false);
                mController.setVisibility(GONE);
            }else{
                setLocker(false);
                if(isPlayering()){
                    mController.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 显示\隐藏屏幕锁
     */
    private void toggleLocker(){
        if(null== mController) return;
        stopDelayedRunnable();
        if(mController.getVisibility()==View.VISIBLE){
            hideLockerView();
        }else{
            AnimationUtils.getInstance().startTranslateRightToLocat(mController, MATION_DRAUTION,null);
            startDelayedRunnable(MESSAGE_LOCKER_HIDE);
        }
    }

    /**
     * 显示\隐藏控制器
     */
    private void toggleController() {
        stopDelayedRunnable();
        if(isControllerShowing()){
            //屏幕锁
            hideLockerView();
            //其它控制器
            hideWidget(true);
        }else{
            //屏幕锁
            if(isOrientationLandscape()&&null!= mController && mController.getVisibility()!=View.VISIBLE){
                AnimationUtils.getInstance().startTranslateRightToLocat(mController, MATION_DRAUTION,null);
            }
            showWidget(true);
            startDelayedRunnable();
        }
    }

    /**
     * 结启动延时任务
     */
    @Override
    public void startDelayedRunnable() {
        startDelayedRunnable(MESSAGE_CONTROL_HIDE);
    }

    /**
     * 根据消息通道结启动延时任务
     */
    private void startDelayedRunnable(int msg) {
        super.startDelayedRunnable();
        if(null!=mExHandel){
            stopDelayedRunnable();
            Message message = mExHandel.obtainMessage();
            message.what= msg;
            mExHandel.sendMessageDelayed(message,DELAYED_INVISIBLE);
        }
    }

    /**
     * 结束延时任务
     */
    @Override
    public void stopDelayedRunnable() {
        stopDelayedRunnable(0);
    }

    /**
     * 重新开始延时任务
     */
    @Override
    public void reStartDelayedRunnable() {
        super.stopDelayedRunnable();
        stopDelayedRunnable();
        startDelayedRunnable();
    }

    /**
     * 根据消息通道取消延时任务
     * @param msg
     */
    private void stopDelayedRunnable(int msg){
        if(null!=mExHandel) {
            if(0==msg){
                mExHandel.removeCallbacksAndMessages(null);
            }else{
                mExHandel.removeMessages(msg);
            }
        }
    }

    /**
     * 使用这个Handel替代getHandel(),避免多播放器同时工作的相互影响
     */
    private ExHandel mExHandel=new ExHandel(Looper.getMainLooper()){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(MESSAGE_LOCKER_HIDE ==msg.what){//屏幕锁
                hideLockerView();
            }else if(MESSAGE_CONTROL_HIDE==msg.what){//控制器
                //屏幕锁
                if(isOrientationLandscape()){
                    hideLockerView();
                }
                hideWidget(true);
            }
        }
    };

    /**
     * 隐藏屏幕锁
     */
    private void hideLockerView(){
        if(null!= mController && mController.getVisibility()==View.VISIBLE){
            AnimationUtils.getInstance().startTranslateLocatToRight(mController, MATION_DRAUTION, new AnimationUtils.OnAnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    mController.setVisibility(GONE);
                }
            });
        }
    }

    /**
     * 设置给用户看的虚拟的视频总时长
     * @param totalDuration 单位：秒
     */
    public void setPreViewTotalDuration(String totalDuration) {
        int duration = PlayerUtils.getInstance().parseInt(totalDuration);
        if(duration>0) setPreViewTotalDuration(duration*1000);
    }

    /**
     * 是否启用屏幕锁功能(默认开启)，只在横屏状态下可用
     * @param showLocker true:启用 fasle:禁止
     */
    public void showLocker(boolean showLocker) {
        findViewById(R.id.controller_root).setVisibility(showLocker?View.VISIBLE:View.GONE);
    }

    /**
     * 重置内部状态
     */
    private void reset(){
        stopDelayedRunnable();
        if(null!=mExHandel) mExHandel.removeCallbacksAndMessages(null);
    }


    @Override
    public void onReset() {
        super.onReset();
        reset();
    }

    @Override
    public void onDestroy() {
        stopDelayedRunnable();
        super.onDestroy();
        reset();
    }
}