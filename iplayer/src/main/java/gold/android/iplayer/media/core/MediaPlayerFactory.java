package gold.android.iplayer.media.core;

import android.content.Context;
import gold.android.iplayer.media.MediaFactory;

public class MediaPlayerFactory extends MediaFactory<MediaPlayer> {

    public static MediaPlayerFactory create() {
        return new MediaPlayerFactory();
    }

    @Override
    public MediaPlayer createPlayer(Context context) {
        return new MediaPlayer(context);
    }
}