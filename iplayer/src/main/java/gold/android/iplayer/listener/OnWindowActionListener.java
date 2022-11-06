package gold.android.iplayer.listener;

import gold.android.iplayer.base.BasePlayer;

public interface OnWindowActionListener {

    /**
     * 持续移动中
     * @param x
     * @param y
     */
    void onMovie(float x,float y);

    /**
     * 点击悬浮窗回调
     * @param basePlayer 播放器实例
     * @param coustomParams 自定义参数
     */
    void onClick(BasePlayer basePlayer, Object coustomParams);


    /**
     * 关闭事件
     */
    void onClose();
}