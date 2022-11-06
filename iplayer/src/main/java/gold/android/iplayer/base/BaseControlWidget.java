package gold.android.iplayer.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import gold.android.iplayer.controller.ControlWrapper;
import gold.android.iplayer.interfaces.IControllerView;
import gold.android.iplayer.interfaces.IVideoController;
import gold.android.iplayer.model.PlayerState;


public abstract class BaseControlWidget extends FrameLayout implements IControllerView {

    protected static final String TAG="BaseControlWidget";
    protected ControlWrapper mControlWrapper;//自定义组件与控制器、播放器之间的交互
    private String mTarget;

    public BaseControlWidget(Context context) {
        this(context,null);
    }

    public BaseControlWidget(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BaseControlWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context,getLayoutId(),this);
        initViews();
    }

    public abstract int getLayoutId();

    public abstract void initViews();

    /**
     * 返回控制器当前正处于什么场景
     * @return 播放器\控制器场景 0：常规状态(包括竖屏、横屏)，1：activity小窗口，2：全局悬浮窗窗口，3：列表，4：Android8.0的画中画 其它：自定义场景
     */
    protected int getPlayerScene() {
        if(null!=mControlWrapper) {
            return mControlWrapper.getPlayerScene();
        }
        return 0;
    }

    /**
     * 播放器\控制器是否处于竖屏状态
     * @return true:竖屏 false:其它:包括横屏、窗口、列表
     */
    protected boolean isOrientationPortrait() {
        if(null!=mControlWrapper){
            return mControlWrapper.isOrientationPortrait();
        }
        return true;
    }

    /**
     * 返回播放器\控制器是否处于竖屏状态
     * @return true:处于竖屏状态 false:非竖屏状态
     */
    protected boolean isOrientationLandscape() {
        if(null!=mControlWrapper){
            return mControlWrapper.isOrientationLandscape();
        }
        return false;
    }

    /**
     * 返回播放器\控制器是否处于常规的竖屏或横屏状态状态
     * @return true:处于竖屏状态 false:非竖屏状态
     */
    protected boolean isNoimalScene() {
        return IVideoController.SCENE_NOIMAL==getPlayerScene();
    }

    /**
     * 返回播放器\控制器是否处于常规的竖屏或横屏状态状态
     * @param scene 当前场景
     * @return true:处于竖屏状态 false:非竖屏状态
     */
    protected boolean isNoimalScene(int scene) {
        return IVideoController.SCENE_NOIMAL==scene;
    }

    /**
     * 返回播放器是否正处于Activity window\全局悬浮窗\画中画 场景
     * @return true:正处于 Activity window\全局悬浮窗\画中画 窗口模式下播放 false:非窗口模式下播放
     */
    protected boolean isWindowScene(){
        return isWindowScene(getPlayerScene());
    }

    /**
     * 返回播放器是否正处于Activity window\全局悬浮窗\画中画 场景
     * @param scene 场景
     * @return true:正处于 Activity window\全局悬浮窗\画中画 窗口模式下播放 false:非窗口模式下播放
     */
    protected boolean isWindowScene(int scene){
        return isWindowActivityScene(scene)||isWindowGlobalScene(scene)||isWindowPipScene(scene);
    }

    /**
     * 返回播放器是否在Activity级别悬浮窗窗口模式下
     * @return true:正处于窗口模式下播放 false:非窗口模式下播放
     */
    protected boolean isWindowActivityScene(){
        return isWindowActivityScene(getPlayerScene());
    }

    /**
     * 返回播放器是否在Activity级别悬浮窗窗口模式下
     * @param scene 场景
     * @return true:正处于窗口模式下播放 false:非窗口模式下播放
     */
    protected boolean isWindowActivityScene(int scene){
        return IVideoController.SCENE_ACTIVITY_WINDOW == scene;
    }

    /**
     * 返回播放器是否在全局悬浮窗窗口模式下
     * @return true:正处于全局悬浮窗窗口模式下播放 false:非全局悬浮窗窗口模式下播放
     */
    protected boolean isWindowGlobalScene(){
        return isWindowGlobalScene(getPlayerScene());
    }

    /**
     * 返回播放器是否在全局悬浮窗窗口模式下
     * @param scene 场景
     * @return true:正处于全局悬浮窗窗口模式下播放 false:非全局悬浮窗窗口模式下播放
     */
    protected boolean isWindowGlobalScene(int scene){
        return IVideoController.SCENE_GLOBAL_WINDOW==scene;
    }

    /**
     * 返回播放器是否在画中画窗口模式下
     * @return true:正处于画中画窗口模式下播放 false:非全局悬浮窗窗口模式下播放
     */
    protected boolean isWindowPipScene(){
        return isWindowPipScene(getPlayerScene());
    }

    /**
     * 返回播放器是否在画中画窗口模式下
     * @param scene 场景
     * @return true:正处于画中画窗口模式下播放 false:非全局悬浮窗窗口模式下播放
     */
    protected boolean isWindowPipScene(int scene){
        return IVideoController.SCENE_PIP_WINDOW ==scene;
    }

    /**
     * 返回播放器是否在列表模式下工作
     * @return true:正处于列表模式下播放 false:非列表模式下播放
     */
    protected boolean isListPlayerScene(){
        return isListPlayerScene(getPlayerScene());
    }

    /**
     * 返回播放器是否在列表模式下工作
     * @param scene 场景
     * @return true:正处于列表模式下播放 false:非列表模式下播放
     */
    protected boolean isListPlayerScene(int scene){
        return IVideoController.SCENE_LISTS==scene;
    }

    /**
     * 返回播放器的试看模式下的虚拟总时长
     * @return 视频时间，单位：毫秒
     */
    protected long getPreViewTotalTime(){
        if(null!=mControlWrapper){
            return mControlWrapper.getPreViewTotalDuration();
        }
        return 0;
    }

    /**
     * 返回播放器场景是否是预览场景
     * @return 视频时间，单位：毫秒
     */
    protected boolean isPreViewScene(){
        if(null!=mControlWrapper){
            return mControlWrapper.getPreViewTotalDuration()>0;
        }
        return false;
    }

    /**
     * 返回当前组件是否正在显示中
     * @return true:可见状态中 false:不可见状态中
     */
    protected boolean isVisible(){
        return getVisibility()==View.VISIBLE;
    }

    /**
     * 返回视频文件总时长
     * @return 单位：毫秒
     */
    protected long getDuration(){
        if(null!=mControlWrapper){
            return mControlWrapper.getDuration();
        }
        return 0;
    }

    /**
     * 返回正在播放的位置
     * @return 单位：毫秒
     */
    protected long getCurrentPosition(){
        if(null!=mControlWrapper){
            return mControlWrapper.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 返回视频分辨率-宽
     * @return 单位：像素
     */
    protected int getVideoWidth(){
        if(null!=mControlWrapper){
            return mControlWrapper.getVideoWidth();
        }
        return 0;
    }

    /**
     * 返回视频分辨率-高
     * @return 单位：像素
     */
    protected int getVideoHeight(){
        if(null!=mControlWrapper){
            return mControlWrapper.getVideoHeight();
        }
        return 0;
    }

    /**
     * 返回当前视频缓冲的进度
     * @return 单位：百分比
     */
    protected int getBuffer(){
        if(null!=mControlWrapper){
            return mControlWrapper.getBuffer();
        }
        return 0;
    }

    /**
     * 快进\快退
     * @param msec 毫秒进度条
     */
    protected void seekTo(long msec){
        if(null!=mControlWrapper){
            mControlWrapper.seekTo(msec);
        }
    }

    /**
     * 是否播放完成
     * @return true:播放完成 false:未播放完成
     */
    protected boolean isCompletion() {
        if(null!=mControlWrapper){
            return mControlWrapper.isCompletion();
        }
        return false;
    }
    /**
     * 返回播放器内部的播放状态
     * @return 是否正处于播放中(准备\开始播放\播放中\缓冲\) true:播放中 false:不处于播放中状态
     */
    protected boolean isPlaying(){
        if(null!=mControlWrapper){
            return mControlWrapper.isPlaying();
        }
        return false;
    }

    /**
     * 播放器是否正在工作中
     * @return 播放器是否正处于工作状态(准备\开始播放\缓冲\手动暂停\生命周期暂停) true:工作中 false:空闲状态
     */
    protected boolean isWorking() {
        if(null!=mControlWrapper) {
            return mControlWrapper.isWorking();
        }
        return false;
    }

    /**
     * 返回ID对应的字符串
     * @param resId 资源ID
     * @return
     */
    protected String getString(int resId){
        return getContext().getResources().getString(resId);
    }

    /**
     * 开始\暂停播放
     */
    protected void togglePlay() {
        if(null!=mControlWrapper) mControlWrapper.togglePlay();
    }

    /**
     * 结束播放
     */
    protected void stopPlay() {
        if(null!=mControlWrapper) mControlWrapper.stopPlay();
    }

    /**
     * 开启全屏播放
     */
    protected void startFullScreen() {
        if(null!=mControlWrapper) mControlWrapper.startFullScreen();
    }

    /**
     * 开启\退出全屏播放
     */
    protected void toggleFullScreen() {
        if(null!=mControlWrapper) mControlWrapper.toggleFullScreen();
    }

    /**
     * 是否开启了静音
     * @return true:已开启静音 false:系统音量
     */
    protected boolean isSoundMute() {
        if(null!=mControlWrapper) {
            return mControlWrapper.isSoundMute();
        }
        return false;
    }

    /**
     * 设置\取消静音
     * @param soundMute true:静音 false:系统音量
     * @return true:已开启静音 false:系统音量
     */
    protected boolean setSoundMute(boolean soundMute) {
        if(null!=mControlWrapper) {
            return mControlWrapper.setSoundMute(soundMute);
        }
        return false;
    }

    /**
     * 静音、取消静音
     */
    protected boolean toggleMute() {
        if(null!=mControlWrapper) {
            return mControlWrapper.toggleMute();
        }
        return false;
    }

    /**
     * 镜像、取消镜像
     */
    protected boolean toggleMirror() {
        if(null!=mControlWrapper) {
            return mControlWrapper.toggleMirror();
        }
        return false;
    }

    /**
     * 通知控制器重新开始延时任务
     */
    protected void reStartDelayedRunnable(){
        if(null!=mControlWrapper) mControlWrapper.reStartDelayedRunnable();
    }

    /**
     * 请求其它所有UI组件隐藏自己的控制器
     * @param isAnimation 请求其它所有UI组件隐藏自己的控制器,是否开启动画
     */
    protected void hideAllController(boolean isAnimation){
        if(null!=mControlWrapper) {
            mControlWrapper.hideAllController(isAnimation);
        }
    }

    //Handel
    protected class BaseHandel extends Handler {
        public BaseHandel(Looper looper){
            super(looper);
        }
    }

    protected long getAnimationDuration() {
        if(null!=mControlWrapper){
            return mControlWrapper.getAnimationDuration();
        }
        return IVideoController.MATION_DRAUTION;
    }

    //==================下面这些方法时不常用的，子类如果需要处理下列方法,请复写实现自己的逻辑====================

    /**
     * UI组件与控制器和播放器之间的桥梁
     * @param controlWrapper 控制器+播放器代理中间人
     */
    @Override
    public void attachControlWrapper(ControlWrapper controlWrapper) {
        this.mControlWrapper=controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    /**
     * 组件视图已经创建并绑定到了控制器，此时mControlWrapper不为空，可在这个方法初始化自己的组件逻辑
     */
    @Override
    public void onCreate() {}

    /**
     * 显示组件
     */
    @Override
    public void show() {
        if(getVisibility()!=View.VISIBLE){
            setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏组件
     */
    @Override
    public void hide() {
        if(getVisibility()!=View.GONE){
            setVisibility(View.GONE);
        }
    }

    @Override
    public void setTarget(String tag) {
        this.mTarget=tag;
    }

    @Override
    public String getTarget() {
        if(TextUtils.isEmpty(mTarget)) return "";
        return mTarget;
    }

    /**
     * 显示控制器-单击控制器时需要关心控制器显示交互的复写此方法
     * @param isAnimation 控制器显示,是否开启动画
     */
    @Override
    public void showControl(boolean isAnimation) {}

    /**
     * 隐藏控制器-单击控制器时需要关心控制器隐藏交互的复写此方法
     * @param isAnimation 控制器隐藏,是否开启动画
     */
    @Override
    public void hideControl(boolean isAnimation) {}

    /**
     * 返回此组件是否正在显示中，这个方法在控制器收到了单击事件时会调用，理论上只有底部的seek控制器组件返回自己的显示状态，其它组件都返回false
     * @return
     */
    @Override
    public boolean isSeekBarShowing() {
        return false;
    }

    //播放器工作状态
    @Override
    public void onPlayerState(PlayerState state, String message) {}

    //播放器\控制器的方向
    @Override
    public void onOrientation(int direction) {}

    //当前播放器所处的场景，场景描述请参考方法：getPlayerScene();
    @Override
    public void onPlayerScene(int playerScene) {}

    //视频标题
    @Override
    public void setTitle(String title) {}

    //播放进度
    @Override
    public void onProgress(long currentDurtion, long totalDurtion) {}

    //缓冲进度
    @Override
    public void onBuffer(int bufferPercent) {}

    //静音状态发生了变化
    @Override
    public void onMute(boolean isMute) {}

    //镜像模式发生了变化
    @Override
    public void onMirror(boolean isMirror) {}

    //缩放模式发生了变化
    @Override
    public void onZoomModel(int zoomModel) {}

    //生命周期可见
    @Override
    public void onResume() {}

    //生命周期不可见
    @Override
    public void onPause() {}

    //生命周期重置
    @Override
    public void onReset() {}

    //生命周期销毁
    @Override
    public void onDestroy() {}
}