package gold.android.iplayer.media;

import android.content.Context;
import gold.android.iplayer.base.AbstractMediaPlayer;

public abstract class MediaFactory<M extends AbstractMediaPlayer> {

    /**
     * 构造播放器解码器
     * @param context 上下文
     * @return 继承自AbstractMediaPlayer的解码器
     */
    public abstract M createPlayer(Context context);
}