package gold.android.iplayer.widget.controls;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;
import gold.android.iplayer.widget.R;
import gold.android.iplayer.base.BaseControlWidget;
import gold.android.iplayer.media.IMediaPlayer;
import gold.android.iplayer.model.PlayerState;
import gold.android.iplayer.utils.AnimationUtils;
import gold.android.iplayer.utils.PlayerUtils;
import gold.android.iplayer.widget.view.BatteryView;

public class ControlToolBarView extends BaseControlWidget implements View.OnClickListener {

    private View mController;//控制器
    //记录用户的设置，是否显示返回按钮\投屏按钮\悬浮窗按钮\菜单按钮
    private boolean showBack,showTv,showWindow,showMenu;

    public ControlToolBarView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.player_control_toolar;
    }

    @Override
    public void initViews() {
        hide();
        findViewById(R.id.controller_title_back).setOnClickListener(this);
        findViewById(R.id.controller_title_tv).setOnClickListener(this);
        findViewById(R.id.controller_title_window).setOnClickListener(this);
        findViewById(R.id.controller_title_menu).setOnClickListener(this);
        mController=findViewById(R.id.controller_title_bar);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.controller_title_back) {
            if(null!=mControlWrapper){
                if(mControlWrapper.isOrientationPortrait()){
                    if(null!= mOnToolBarActionListener) mOnToolBarActionListener.onBack();
                }else{
                    mControlWrapper.quitFullScreen();
                }
            }
        }else if (view.getId() == R.id.controller_title_tv) {
            reStartDelayedRunnable();
            if(null!= mOnToolBarActionListener) mOnToolBarActionListener.onTv();
        }else if (view.getId() == R.id.controller_title_window) {
            reStartDelayedRunnable();
            if(null!= mOnToolBarActionListener) mOnToolBarActionListener.onWindow();
        }else if (view.getId() == R.id.controller_title_menu) {
            reStartDelayedRunnable();
            if(null!= mOnToolBarActionListener) mOnToolBarActionListener.onMenu();
        }
    }

    /**
     * @param isAnimation 控制器显示,是否开启动画
     */
    @Override
    public void showControl(boolean isAnimation) {
        if(null!=mController){
            if(mController.getVisibility()!=View.VISIBLE){
                if(isAnimation){
                    AnimationUtils.getInstance().startTranslateTopToLocat(mController, getAnimationDuration(),null);
                }else{
                    mController.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * @param isAnimation 控制器隐藏,是否开启动画
     */
    @Override
    public void hideControl(boolean isAnimation) {
        if(null!=mController){
            if(mController.getVisibility()!=View.GONE){
                if(isAnimation){
                    AnimationUtils.getInstance().startTranslateLocatToTop(mController, getAnimationDuration(), new AnimationUtils.OnAnimationListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mController.setVisibility(GONE);
                        }
                    });
                }else{
                    mController.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onPlayerState(PlayerState state, String message) {
        switch (state) {
            case STATE_RESET://初始状态\播放器还原重置
            case STATE_STOP://初始\停止
                onReset();
                break;
            case STATE_START://首帧渲染
                //渲染第一帧时，竖屏和横屏都显示
                if(isNoimalScene()){
                    show();
                }
                showControl(true);
                break;
            case STATE_PREPARE://准备中
                hide();
                break;
        }
    }

    @Override
    public void onOrientation(int direction) {
        if(null==mController) return;
        if(IMediaPlayer.ORIENTATION_LANDSCAPE==direction){
            show();
            findViewById(R.id.controller_title).setVisibility(View.VISIBLE);//视频标题
            findViewById(R.id.controller_title_back).setVisibility(View.VISIBLE);//返回按钮
            findViewById(R.id.controller_menus).setVisibility(View.GONE);//菜单栏(投屏\悬浮窗\菜单按钮)
            //添加系统时间\电池电量组件
            FrameLayout controllerBattery = (FrameLayout) findViewById(R.id.controller_battery);
            controllerBattery.setVisibility(View.VISIBLE);
            controllerBattery.addView(new BatteryView(getContext()));
            //竖屏下处理标题栏和控制栏的左右两侧缩放
            int margin = PlayerUtils.getInstance().dpToPxInt(22f);
            mController.setPadding(margin,0,margin,0);
            if(isPlaying()) reStartDelayedRunnable();
        }else{
            findViewById(R.id.controller_title).setVisibility(View.GONE);//视频标题
            findViewById(R.id.controller_title_back).setVisibility(showBack?View.VISIBLE:View.GONE);//返回按钮
            findViewById(R.id.controller_menus).setVisibility(View.VISIBLE);//菜单栏(投屏\悬浮窗\菜单按钮)
            //移除系统时间\电池电量组件
            FrameLayout controllerBattery = (FrameLayout) findViewById(R.id.controller_battery);
            controllerBattery.removeAllViews();
            controllerBattery.setVisibility(View.GONE);
            mController.setPadding(0,0,0,0);
            if(isNoimalScene()){
                show();
            }else{
                //非常规情况下不处理
                hide();
            }
        }
    }

    @Override
    public void onPlayerScene(int playerScene) {
        //当播放器和控制器在专场播放、场景发生变化时，仅当在常规模式下并且正在播放才显示控制器
        if(isNoimalScene()){
            show();
            if(isPlaying()){
                showControl(false);
                reStartDelayedRunnable();
            }
        }else{
            hide();
        }
    }

    @Override
    public void setTitle(String title) {
        ((TextView) findViewById(R.id.controller_title)).setText(PlayerUtils.getInstance().formatHtml(title));
    }

    /**
     * 是否显示返回按钮，仅限竖屏情况下，横屏模式下强制显示
     * @param showBack 返回按钮是否显示
     */
    public void showBack(boolean showBack) {
        this.showBack=showBack;
        findViewById(R.id.controller_title_back).setVisibility(isOrientationLandscape()?View.VISIBLE:showBack?View.VISIBLE:View.GONE);//返回按钮
    }

    /**
     * 是否显示投屏\悬浮窗\功能等按钮，仅限竖屏情况下，横屏模式下强制不显示
     * @param showTv 投屏按钮是否显示
     * @param showWindow 悬浮窗按钮是否显示
     * @param showMenu 菜单按钮是否显示
     */
    public void showMenus(boolean showTv,boolean showWindow,boolean showMenu) {
        this.showTv=showTv;this.showWindow=showWindow;this.showMenu=showMenu;
        findViewById(R.id.controller_title_tv).setVisibility(isOrientationLandscape()?View.GONE:showTv?View.VISIBLE:View.GONE);//投屏
        findViewById(R.id.controller_title_window).setVisibility(isOrientationLandscape()?View.GONE:showWindow?View.VISIBLE:View.GONE);//悬浮窗
        findViewById(R.id.controller_title_menu).setVisibility(isOrientationLandscape()?View.GONE:showMenu?View.VISIBLE:View.GONE);//菜单按钮
    }

    public abstract static class OnToolBarActionListener {
        public void onBack(){}
        public void onTv(){}
        public void onWindow(){}
        public void onMenu(){}
    }

    private OnToolBarActionListener mOnToolBarActionListener;

    public void setOnToolBarActionListener(OnToolBarActionListener onToolBarActionListener) {
        mOnToolBarActionListener = onToolBarActionListener;
    }

    @Override
    public void onReset() {
        super.onReset();
        hideControl(false);
    }
}