package gold.android.videoplayer.pager.adapter;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import gold.android.videoplayer.R;
import gold.android.videoplayer.base.adapter.BaseNoimalAdapter;
import gold.android.videoplayer.base.adapter.widget.BaseViewHolder;
import gold.android.videoplayer.pager.bean.VideoBean;
import gold.android.videoplayer.utils.GlideModel;
import gold.android.videoplayer.utils.ScreenUtils;
import java.util.List;


public class VideoListAdapter extends BaseNoimalAdapter<VideoBean, BaseViewHolder> {

    private final int mItemHeight;

    public VideoListAdapter(List<VideoBean> data) {
        super(R.layout.item_video_list,data);
        mItemHeight = ((ScreenUtils.getInstance().getScreenWidth() - ScreenUtils.getInstance().dpToPxInt(3f)) / 2) * 16 /11;
    }

    @Override
    protected void initItemView(BaseViewHolder viewHolder, int position, VideoBean data) {
        ((TextView) viewHolder.getView(R.id.item_title)).setText(data.getFilterTitleStr());
        FrameLayout itemRootView = (FrameLayout) viewHolder.getView(R.id.item_root_content);
        itemRootView.getLayoutParams().height= mItemHeight;
        ImageView imageCover = (ImageView) viewHolder.getView(R.id.item_cover);
        GlideModel.getInstance().loadImage(imageCover,data.getCoverImgUrl());
    }
}