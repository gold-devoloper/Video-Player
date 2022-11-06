package gold.android.videoplayer.video.presenter;

import gold.android.videoplayer.base.BasePresenter;
import gold.android.videoplayer.net.BaseEngin;
import gold.android.videoplayer.net.OnResultCallBack;
import gold.android.videoplayer.video.bean.OpenEyesIndexInfo;
import gold.android.videoplayer.video.contract.VideoListContract;
import gold.android.videoplayer.video.model.IndexVideoEngin;

/**
 * TinyHung@Outlook.com
 * 2019/5/6
 * Index Video Presenter
 */

public class VideoListPersenter extends BasePresenter<VideoListContract.View, IndexVideoEngin> implements VideoListContract.Presenter<VideoListContract.View>{

    //已经加载得页眉
    private int mPage;

    @Override
    protected IndexVideoEngin createEngin() {
        return new IndexVideoEngin();
    }

    /**
     * 获取视频列表
     * @param isRestart 是否重新开始？
     */
    @Override
    public void getIndexVideos(boolean isRestart) {
        if(isRequsting()){
           return;
        }
        mPage++;
        if(isRestart){
            mPage=0;
        }
        if(null!=mViewRef&&null!=mViewRef.get()){
            if(0==mPage){
                mViewRef.get().showLoading();
            }
            getNetEngin().get().getIndexVideos(mPage, new OnResultCallBack<OpenEyesIndexInfo>() {

                @Override
                public void onResponse(OpenEyesIndexInfo data) {
                    if(null!=mViewRef&&null!=mViewRef.get()){
                        if(null!=data.getItemList()&&data.getItemList().size()>0){
                            mViewRef.get().showVideos(data.getItemList(),0==mPage);
                        }else{
                            mViewRef.get().showError(BaseEngin.API_RESULT_EMPTY,BaseEngin.API_EMPTY);
                        }
                    }
                }

                @Override
                public void onError(int code, String errorMsg) {
                    if(code!=BaseEngin.API_RESULT_EMPTY){
                        if(mPage>-1){
                            mPage--;
                        }
                    }
                    if(null!=mViewRef&&null!=mViewRef.get()){
                        mViewRef.get().showError(code,errorMsg);
                    }
                }
            });
        }
    }

    /**
     * 根据URL获取视频列表
     * @param url url
     * @param isRestart 是否从第一页开始加载的
     */
    @Override
    public void getVideosByUrl(String url, boolean isRestart) {
        if(isRequsting()){
            return;
        }
        mPage++;
        if(isRestart){
            mPage=0;
        }
        if(null!=mViewRef&&null!=mViewRef.get()){
            if(0==mPage){
                mViewRef.get().showLoading();
            }
            getNetEngin().get().getVideosByUrl(url, mPage, new OnResultCallBack<OpenEyesIndexInfo>() {

                @Override
                public void onResponse(OpenEyesIndexInfo data) {
                    if(null!=mViewRef&&null!=mViewRef.get()){
                        if(null!=data.getVideoList()&&data.getVideoList().size()>0){
                            mViewRef.get().showVideos(data.getVideoList(),0==mPage);
                        }else{
                            mViewRef.get().showError(BaseEngin.API_RESULT_EMPTY,BaseEngin.API_EMPTY);
                        }
                    }
                }

                @Override
                public void onError(int code, String errorMsg) {
                    if(code!=BaseEngin.API_RESULT_EMPTY){
                        if(mPage>-1){
                            mPage--;
                        }
                    }
                    if(null!=mViewRef&&null!=mViewRef.get()){
                        mViewRef.get().showError(code,errorMsg);
                    }
                }
            });
        }
    }

    /**
     * 根据视频ID获取视频列表
     * @param videoID 视频ID
     */
    @Override
    public void getVideosByVideo(String videoID) {
        if(null!=mViewRef&&null!=mViewRef.get()){
            mViewRef.get().showLoading();

            getNetEngin().get().getVideosByVideo(videoID, new OnResultCallBack<OpenEyesIndexInfo>() {
                @Override
                public void onResponse(OpenEyesIndexInfo data) {
                    if(null!=mViewRef&&null!=mViewRef.get()){
                        if(null!=data.getItemList()&&data.getItemList().size()>0){
                            mViewRef.get().showVideos(data.getItemList(),false);
                        }else{
                            mViewRef.get().showError(BaseEngin.API_RESULT_EMPTY,BaseEngin.API_EMPTY);
                        }
                    }
                }

                @Override
                public void onError(int code, String errorMsg) {
                    if(null!=mViewRef&&null!=mViewRef.get()){
                        mViewRef.get().showError(code,errorMsg);
                    }
                }
            });
        }
    }
}