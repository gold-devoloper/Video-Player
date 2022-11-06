package gold.android.iplayer.utils;

import android.content.Context;
import android.media.AudioManager;

public class AudioFocus {


    private int mVolumeWhenFocusLossTransientCanDuck;
    private AudioManager mAudioManager;

    public AudioFocus(){
        this(PlayerUtils.getInstance().getContext());
    }

    public AudioFocus(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }


    public int requestAudioFocus(OnAudioFocusListener focusListener) {
        this.mFocusListener=focusListener;
        if(null!=mAudioManager){
            int requestAudioFocus = mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

            return requestAudioFocus;
        }
        return 1;
    }


    public void releaseAudioFocus() {
        if(null!=mAudioManager&&null!=onAudioFocusChangeListener){
            mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }

    }

    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener=new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {

            int volume;
            switch (focusChange) {

                case AudioManager.AUDIOFOCUS_GAIN:
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:

                    volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    if (mVolumeWhenFocusLossTransientCanDuck > 0 && volume == mVolumeWhenFocusLossTransientCanDuck / 2) {
                        // 恢复音量
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeWhenFocusLossTransientCanDuck,
                                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                    }

                    if(null!=mFocusListener){
                        mFocusListener.onFocusStart();
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS:

                    if(null!=mFocusListener){
                        mFocusListener.onFocusStop();
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:

                    if(null!=mFocusListener){
                        mFocusListener.onFocusStop();
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:

                    if(null!=mFocusListener){
                        volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        if (volume > 0) {
                            mVolumeWhenFocusLossTransientCanDuck = volume;
                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeWhenFocusLossTransientCanDuck / 2,
                                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                        }
                        mFocusListener.onFocusStart();
                    }
                    break;
            }
        }
    };

    public interface OnAudioFocusListener{
        void onFocusStart();
        void onFocusStop();
    }

    public OnAudioFocusListener mFocusListener;

    public void onDestroy(){
        releaseAudioFocus();
        mAudioManager=null;mFocusListener=null;
    }
}