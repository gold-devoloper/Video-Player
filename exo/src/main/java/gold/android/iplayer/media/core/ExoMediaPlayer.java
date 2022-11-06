package gold.android.iplayer.media.core;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.view.Surface;
import android.view.SurfaceHolder;
import gold.android.iplayer.base.AbstractMediaPlayer;
import gold.android.iplayer.media.IMediaPlayer;
import gold.android.iplayer.media.ExoMediaSourceHelper;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.video.VideoSize;
import java.io.IOException;
import java.util.Map;

/**
 * created by hty
 * 2022/8/1
 * Desc:EXO解码器示例
 */
public class ExoMediaPlayer extends AbstractMediaPlayer implements Player.Listener {

    private ExoPlayer mMediaPlayer;
    private boolean isPlaying;//用这个boolean标记是否首帧播放

    public ExoMediaPlayer(Context context) {
        super(context);
        isPlaying=false;
        mMediaPlayer = new ExoPlayer.Builder(context,
                new DefaultRenderersFactory(context),
                new DefaultMediaSourceFactory(context))
                .build();
        mMediaPlayer.addListener(this);
    }

    public ExoPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    @Override
    public void setLooping(boolean loop) {
        if(null!=mMediaPlayer) mMediaPlayer.setRepeatMode(loop?Player.REPEAT_MODE_ALL:Player.REPEAT_MODE_OFF);
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if(null!=mMediaPlayer) mMediaPlayer.setVolume((leftVolume+rightVolume)/2);
    }

    @Override
    public void setBufferTimeMax(float timeSecond) {
        //不支持
    }

    @Override
    public void setSurface(Surface surface) {
        if(null!=mMediaPlayer) mMediaPlayer.setVideoSurface(surface);
    }

    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {
//        if(null!=mMediaPlayer) mMediaPlayer.setDisplay(surfaceHolder);
        if(null!=surfaceHolder){
            setSurface(surfaceHolder.getSurface());
        }else{
            setSurface(null);
        }
    }

    @Override
    public void setDataSource(String dataSource) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        setDataSource(dataSource,null);
    }

    @Override
    public void setDataSource(String dataSource, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        if(null!=mMediaPlayer) mMediaPlayer.setMediaSource(ExoMediaSourceHelper.getInstance(getContext()).getMediaSource(dataSource,headers));
    }

    @Override
    public void setDataSource(AssetFileDescriptor dataSource) throws IOException, IllegalArgumentException, IllegalStateException {
        //Exo不支持
    }

    @Override
    public void setTimeout(long prepareTimeout, long readTimeout) {
        //Exo不支持
    }

    @Override
    public void setSpeed(float speed) {
        if(null!=mMediaPlayer){
            PlaybackParameters parameters=new PlaybackParameters(speed);
            mMediaPlayer.setPlaybackParameters(parameters);
        }
    }

    @Override
    public void seekTo(long msec) throws IllegalStateException {
        seekTo(msec,true);
    }

    @Override
    public void seekTo(long msec, boolean accurate) throws IllegalStateException {
        if(null!=mMediaPlayer) mMediaPlayer.seekTo(msec);
    }

    /**
     * 是否正在播放 ExoMediaPlayer 解码器这个方法只能在main线程中被调用,否则会报错误：java.lang.IllegalStateException: Player is accessed on the wrong thread.
     * @return 返回是否正在播放
     */
    @Override
    public boolean isPlaying() {
        if (mMediaPlayer == null)
            return false;
        int state = mMediaPlayer.getPlaybackState();
        switch (state) {
            case Player.STATE_BUFFERING:
            case Player.STATE_READY:
                return mMediaPlayer.getPlayWhenReady();
            case Player.STATE_IDLE:
            case Player.STATE_ENDED:
            default:
                return false;
        }
    }

    /**
     * ExoMediaPlayer 解码器这个方法只能在main线程中被调用,否则会报错误：java.lang.IllegalStateException: Player is accessed on the wrong thread.
     * @return 返回当前正在播放的位置
     */
    @Override
    public long getCurrentPosition() {
        if(null!=mMediaPlayer){
            if(null!=mListener) mListener.onBufferUpdate(this,mMediaPlayer.getBufferedPercentage());
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * ExoMediaPlayer 解码器这个方法只能在main线程中被调用,否则会报错误：java.lang.IllegalStateException: Player is accessed on the wrong thread.
     * @return
     */
    @Override
    public long getDuration() {
        if(null!=mMediaPlayer){
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getBuffer() {
        try {
            if(null!=mMediaPlayer){
                return mMediaPlayer.getBufferedPercentage();
            }
        }catch (Throwable e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void prepare() throws IOException, IllegalStateException {
        isPlaying=false;
        if(null!=mMediaPlayer) mMediaPlayer.prepare();
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        isPlaying=false;
        if(null!=mMediaPlayer) mMediaPlayer.prepare();
    }

    @Override
    public void start() {
        if(null!=mMediaPlayer) mMediaPlayer.setPlayWhenReady(true);
    }

    @Override
    public void pause() {
        if(null!=mMediaPlayer) mMediaPlayer.setPlayWhenReady(false);
    }

    @Override
    public void stop() {
        if(null!=mMediaPlayer) mMediaPlayer.stop();
    }

    @Override
    public void reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.clearMediaItems();
            mMediaPlayer.setVideoSurface(null);
        }
    }

    @Override
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.removeListener(this);
            mMediaPlayer.release();
        }
        super.release();
    }

    //==========================================EXO解码器回调=========================================

    @Override
    public void onPlaybackStateChanged(int playbackState) {
//        Logger.d(TAG,"onPlaybackStateChanged-->playbackState:"+playbackState+",isPlaying:"+isPlaying);
        switch (playbackState) {
            case Player.STATE_BUFFERING:
                if(null!=mListener) mListener.onInfo(this, IMediaPlayer.MEDIA_INFO_BUFFERING_START,0);
                break;
            case Player.STATE_READY:
                if(null!=mMediaPlayer) mMediaPlayer.setPlayWhenReady(true);
                if(null!=mListener){
                    if(isPlaying){
                        mListener.onInfo(this, IMediaPlayer.MEDIA_INFO_BUFFERING_END,0);//如果还未进行过播放,则被认为是首帧播放
                    }else{
                        mListener.onPrepared(this);
                        mListener.onInfo(this, IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START,0);//如果还未进行过播放,则被认为是首帧播放
                    }
                }
                isPlaying=true;
                break;
            case Player.STATE_ENDED:
                if(null!=mListener) mListener.onCompletion(this);
                break;
            default:
                if(null!=mListener) mListener.onInfo(this, playbackState,0);
        }
    }

    @Override
    public void onPlayerError(PlaybackException error) {
        if(null!=mListener) mListener.onError(this,error.errorCode,0);
    }

    @Override
    public void onVideoSizeChanged(VideoSize videoSize) {
        if(null!=mListener) mListener.onVideoSizeChanged(this, videoSize.width, videoSize.height,0,0);
    }
}