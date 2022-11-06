package gold.android.videoplayer.pager.fragment;

import android.view.View;
import android.widget.ImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import gold.android.videoplayer.R;
import gold.android.videoplayer.base.BaseFragment;
import gold.android.videoplayer.base.BasePresenter;
import gold.android.videoplayer.base.adapter.interfaces.OnItemClickListener;
import gold.android.videoplayer.pager.activity.PagerPlayerActivity;
import gold.android.videoplayer.pager.adapter.VideoListAdapter;
import gold.android.videoplayer.pager.bean.VideoBean;
import gold.android.videoplayer.utils.DataFactory;
import gold.android.videoplayer.utils.ScreenUtils;
import java.util.List;


public class VideoListFragment extends BaseFragment {

    private VideoListAdapter mAdapter;


    @Override
    protected int getLayoutID() {
        return R.layout.fragment_video_list;
    }

    @Override
    protected void initViews() {
        ImageView status_bar = (ImageView) findViewById(R.id.status_bar);
        status_bar.getLayoutParams().height= ScreenUtils.getInstance().getStatusBarHeight(getContext())+ScreenUtils.getInstance().dpToPxInt(49f);
        status_bar.setImageResource(R.mipmap.ic_title_bg);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        //列表适配器初始化
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new VideoListAdapter(null);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long itemId) {
                if(getActivity() instanceof PagerPlayerActivity){
                    PagerPlayerActivity pagerPlayerActivity = (PagerPlayerActivity) getActivity();
//                    List<VideoBean> data = mAdapter.getData();
//                    String videoJson = new Gson().toJson(data);
                    pagerPlayerActivity.navigationPlayer(mAdapter.getData(),position);
                }
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        //加载数据
        DataFactory.getInstance().getTikTopVideo(new DataFactory.OnCallBackListener() {
            @Override
            public void onList(List<VideoBean> data) {
                if(null!=mAdapter) mAdapter.setNewData(data);
            }
        });
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }
}