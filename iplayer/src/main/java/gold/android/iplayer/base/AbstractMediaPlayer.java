package gold.android.iplayer.base;

import android.content.Context;
import gold.android.iplayer.media.IMediaPlayer;
import gold.android.iplayer.listener.OnMediaEventListener;


public abstract class AbstractMediaPlayer implements IMediaPlayer {

    protected final String TAG = AbstractMediaPlayer.class.getSimpleName();
    protected Context mContext ;
    protected OnMediaEventListener mListener;//播放器监听器

    public AbstractMediaPlayer(Context context){
        this.mContext=context;
    }

    protected Context getContext() {
        return mContext;
    }

    @Override
    public void setMediaEventListener(OnMediaEventListener listener) {
        this.mListener=listener;
    }

    @Override
    public void release() {
        mListener=null;
    }
}