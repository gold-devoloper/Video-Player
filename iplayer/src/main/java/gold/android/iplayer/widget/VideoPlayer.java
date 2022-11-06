package gold.android.iplayer.widget;

import android.content.Context;
import android.util.AttributeSet;
import gold.android.iplayer.base.BasePlayer;
import gold.android.iplayer.controller.VideoController;


public class VideoPlayer extends BasePlayer {

    public VideoPlayer(Context context) {
        super(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoPlayer(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initViews() {}

    @Deprecated
    public VideoController initController(){
        throw new RuntimeException("\n==================================1.0.0==================================\n" +
                "        VideoController controller = mVideoPlayer.createController();//创建一个默认控制器\n" +
                "        WidgetFactory.bindDefaultControls(controller,false,true);"
        );
    }


    public VideoController createController(){

        VideoController controller = new VideoController(getContext());
        setController(controller);
        return controller;
    }
}