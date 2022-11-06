package gold.android.iplayer.interfaces;

import android.content.Context;
import gold.android.iplayer.base.AbstractMediaPlayer;
import gold.android.iplayer.base.BasePlayer;
import gold.android.iplayer.model.PlayerState;


public interface IBasePlayer {

    /**
     * 返回播放器的上下文
     * @return
     */
    Context getContext();

    /**
     * 返回一个继承自AbstractMediaPlayer的播放器解码器
     * @return
     */
    AbstractMediaPlayer getMediaPlayer();

    /**
     * 返回一个实现了IRenderView接口的自定义画面渲染器
     * @return 画面渲染器实体类
     */
    IRenderView getRenderView();

    /**
     * 宿主返回一个装载视频播放器的容器
     * @return
     */
    BasePlayer getVideoPlayer();

    /**
     * 播放内部各种事件
     * @param state 播放器内部状态
     * @param message 状态说明
     */
    void onPlayerState(PlayerState state, final String message);

    /**
     * 缓冲进度 主线程回调
     * @param percent 百分比
     */
    void onBuffer(int percent);

    /**
     * 视频宽高
     * @param width 视频宽
     * @param height 视频高
     */
    void onVideoSizeChanged(int width, int height);

    /**
     * 播放进度 主线程回调
     * @param currentDurtion 当前播放位置,单位：总进度的毫秒进度
     * @param totalDurtion 总时长,单位：毫秒
     */
    void onProgress(long currentDurtion, long totalDurtion);
}