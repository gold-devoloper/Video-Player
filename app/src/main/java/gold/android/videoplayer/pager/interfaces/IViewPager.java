package gold.android.videoplayer.pager.interfaces;


public interface IViewPager {

    void prepare();//缓冲中

    void resume();//开始\恢复播放

    void pause();//暂停播放

    void stop();//停止播放

    void error();//播放失败了

    void onRelease();//生命周期-释放
}