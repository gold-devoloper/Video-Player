package gold.android.videoplayer.render;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import gold.android.iplayer.base.AbstractMediaPlayer;
import gold.android.iplayer.interfaces.IRenderView;
import gold.android.iplayer.media.IMediaPlayer;


public class CoustomSurfaceView extends SurfaceView implements IRenderView, SurfaceHolder.Callback {

    private AbstractMediaPlayer mMediaPlayer;
    private SurfaceHolder mSurfaceHolder;
    private  int    mVideoWidth;
    private  int    mVideoHeight;
    private  int    mVideoSarNum;
    private  int    mVideoSarDen;
    private  int    mMeasureWidth;
    private  int    mMeasureHeight;
    protected int mScaleMode = IMediaPlayer.MODE_NOZOOM_TO_FIT;//默认是原始大小
    private  int    mDegree;
    private  boolean mMirror = false;
    private  boolean mVerticalOrientation;
    boolean  mUseSettingRatio = false;
    private  float  mHOffset = 0.0f;
    private  float  mVOffset = 0.0f;
    private Matrix mMatrix = new Matrix();

    private  int  mLayoutWidth;
    private  int  mLayoutHeight;
    private float mCenterPointX;
    private float mCenterPointY;
    private float mDeltaX;
    private float mDeltaY;
    private float mCurrentVideoWidth;
    private float mCurrentVideoHeight;
    private float mTotalTranslateX;
    private float mTotalTranslateY;
    private float mTotalRatio = 1.0f;
    private float mScaledRatio;
    private float mInitRatio;

    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_ZOOM = 2;
    public static final int STATUS_MOVE = 3;
    private int mCurrentDispStatus = STATUS_NORMAL;

    public CoustomSurfaceView(Context context) {
        this(context,null);
    }

    public CoustomSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public CoustomSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //自定义解码器注意这里的设置
        setSaveFromParentEnabled(true);
        setDrawingCacheEnabled(false);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.RGBA_8888);
    }

    //======================================自定义解码器需要关心的回调===================================

    @Override
    public void attachMediaPlayer(AbstractMediaPlayer mediaPlayer) {
        this.mMediaPlayer=mediaPlayer;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setVideoSize(int width, int height){
        mVideoWidth = width;
        mVideoHeight = height;
    }

    @Override
    public void setZoomMode(int zoomMode){
        mScaleMode = zoomMode;
        mUseSettingRatio = false;
        mCurrentDispStatus = STATUS_NORMAL;
        requestLayout();
    }

    @Override
    public void setDegree(int degree){
        mDegree =  degree;
        mCurrentDispStatus = STATUS_NORMAL;
        requestLayout();
    }

    @Override
    public void setViewRotation(int rotation) {
        setRotation(rotation);
    }

    @Override
    public void setSarSize(int sarNum,int sarDen){
        mVideoSarNum = sarNum;
        mVideoSarDen = sarDen;
    }

    @Override
    public boolean setMirror(boolean mirror){
        mMirror = mirror;
        setScaleX(mirror ? -1.0F : 1.0F);
        return mMirror;
    }

    @Override
    public boolean toggleMirror(){
        mMirror = !mMirror;
        setScaleX(mMirror ? -1.0F : 1.0F);
        return mMirror;
    }

    @Override
    public void requestDrawLayout() {
        requestLayout();
    }

    @Override
    public void release() {
        try {
            if(null!=mSurfaceHolder){
                mSurfaceHolder.removeCallback(this);
            }
        }catch (Throwable e){
            e.printStackTrace();
        }finally {
            mSurfaceHolder=null;mMediaPlayer=null;
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {}

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if(null!=mMediaPlayer) mMediaPlayer.setDisplay(surfaceHolder);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    private void Zoom() {
        Matrix matrix   = mMatrix;
        int videoWidth  = mVideoWidth;
        int videoHeight = mVideoHeight;

        if (mMirror){
            mCenterPointX = mLayoutWidth-mCenterPointX;
        }
        if (mVideoSarNum > 0 && mVideoSarDen > 0) {
            videoWidth = videoWidth * mVideoSarNum / mVideoSarDen;
        }

        float scaleX = (float)videoWidth/mLayoutWidth;
        float scaleY = (float)videoHeight/mLayoutHeight;
        if (mScaleMode == IMediaPlayer.MODE_NOZOOM_TO_FIT) {
            if ((mDegree / 90) % 2 != 0) {
                scaleX = (float) mLayoutHeight / mLayoutWidth;
                scaleY = (float) mLayoutWidth / mLayoutHeight;
            } else {
                scaleX = 1.0f;
                scaleY = 1.0f;
            }
        }

        matrix.reset();
        matrix.postScale(mTotalRatio*scaleX, mTotalRatio*scaleY);
        matrix.postRotate(mDegree);

        float scaledWidth = mLayoutWidth * mTotalRatio * scaleX;
        float scaledHeight = mLayoutHeight * mTotalRatio * scaleY;
        if ((mDegree / 90) %2 != 0){
            scaledWidth = mLayoutHeight * mTotalRatio * scaleY;
            scaledHeight = mLayoutWidth * mTotalRatio * scaleX;
        }
        float translateX = 0f;
        float translateY = 0f;

        translateX = mTotalTranslateX * mScaledRatio + mCenterPointX * (1 - mScaledRatio);
        translateY = mTotalTranslateY  * mScaledRatio + mCenterPointY * (1 - mScaledRatio);

        switch (mDegree){
            case 0:
                if( scaledWidth < mLayoutWidth){
                    translateX = ((mLayoutWidth - scaledWidth) / 2f);
                }
                else {
                    if (translateX > 0) {
                        translateX = 0;
                    } else if (scaledWidth + translateX < mLayoutWidth) {
                        translateX = mLayoutWidth - scaledWidth;
                    }
                }

                if (scaledHeight < mLayoutHeight){
                    translateY = ((mLayoutHeight - scaledHeight) / 2f);
                }
                else {
                    if (translateY > 0) {
                        translateY = 0;
                    }
                    else if (scaledHeight + translateY < mLayoutHeight){
                        translateY = mLayoutHeight - scaledHeight;
                    }
                }
                break;
            case -90:
                if( scaledWidth < mLayoutWidth){
                    translateX = ((mLayoutWidth - scaledWidth) / 2f);
                }
                else {
                    if (translateX > 0) {
                        translateX = 0;
                    } else if (scaledWidth + translateX < mLayoutWidth) {
                        translateX = mLayoutWidth - scaledWidth;
                    }
                }

                if (scaledHeight < mLayoutHeight){
                    translateY = ((mLayoutHeight + scaledHeight) / 2f);
                }
                else {
                    if (translateY > scaledHeight) {
                        translateY = scaledHeight;
                    }
                    else if ( translateY < mLayoutHeight){
                        translateY = mLayoutHeight ;
                    }
                }
                break;
            case -180:
                if( scaledWidth < mLayoutWidth){
                    translateX = ((mLayoutWidth + scaledWidth) / 2f);
                }
                else {
                    if (translateX > scaledWidth) {
                        translateX = scaledWidth;
                    } else if (translateX < mLayoutWidth) {
                        translateX = mLayoutWidth;
                    }
                }

                if (scaledHeight < mLayoutHeight){
                    translateY = ((mLayoutHeight + scaledHeight) / 2f);
                }
                else {
                    if (translateY > scaledHeight) {
                        translateY = scaledHeight;
                    }
                    else if ( translateY < mLayoutHeight){
                        translateY = mLayoutHeight ;
                    }
                }
                break;
            case -270:
                if( scaledWidth < mLayoutWidth){
                    translateX = ((mLayoutWidth + scaledWidth) / 2f);
                }
                else {
                    if (translateX > scaledWidth) {
                        translateX = scaledWidth;
                    } else if (translateX < mLayoutWidth) {
                        translateX = mLayoutWidth;
                    }
                }

                if (scaledHeight < mLayoutHeight){
                    translateY = ((mLayoutHeight - scaledHeight) / 2f);
                }
                else {
                    if( translateY > 0){
                        translateY = 0;
                    }
                    else if (scaledHeight + translateY < mLayoutHeight){
                        translateY = mLayoutHeight - scaledHeight;
                    }
                }
                break;
        }

//            translateX += mHOffset * mLayoutWidth/2;
//            translateY +=  - mVOffset* mLayoutHeight/2;
        matrix.postTranslate(translateX , translateY);

        mTotalTranslateX = translateX;
        mTotalTranslateY = translateY;
        mCurrentVideoWidth = scaledWidth;
        mCurrentVideoHeight = scaledHeight;

    }

    private void Move() {
        Matrix matrix   = mMatrix;
        int videoWidth  = mVideoWidth;
        int videoHeight = mVideoHeight;

        if (mMirror){
            mDeltaX = -mDeltaX;
        }
        if (mVideoSarNum > 0 && mVideoSarDen > 0) {
            videoWidth = videoWidth * mVideoSarNum / mVideoSarDen;
        }

        float scaleX = (float)videoWidth/mLayoutWidth;
        float scaleY = (float)videoHeight/mLayoutHeight;
        if (mScaleMode == IMediaPlayer.MODE_NOZOOM_TO_FIT) {
            if ((mDegree / 90) % 2 != 0) {
                scaleX = (float) mLayoutHeight / mLayoutWidth;
                scaleY = (float) mLayoutWidth / mLayoutHeight;
            } else {
                scaleX = 1.0f;
                scaleY = 1.0f;
            }
        }

        matrix.reset();
        matrix.postScale(mTotalRatio*scaleX, mTotalRatio*scaleY);
        matrix.postRotate(mDegree);

        float xoffset = 0.f;
        float yoffset = 0.f;
        switch (mDegree){
            case 0:
                xoffset = ((mLayoutWidth - mCurrentVideoWidth) / 2f);
                yoffset = ((mLayoutHeight - mCurrentVideoHeight) / 2f);
                break;
            case -90:
                xoffset = (mLayoutWidth - mCurrentVideoWidth)/2;
                yoffset = (mLayoutHeight + mCurrentVideoHeight)/2;
                break;
            case -180:
                xoffset = (mLayoutWidth + mCurrentVideoWidth)/2;
                yoffset = (mLayoutHeight + mCurrentVideoHeight)/2;
                break;
            case -270:
                xoffset = ((mLayoutWidth + mCurrentVideoWidth) / 2f);
                yoffset = ((mLayoutHeight - mCurrentVideoHeight) / 2f);
                break;
        }

        if (mTotalTranslateX + mDeltaX  > xoffset + (mCurrentVideoWidth - mLayoutWidth)/2) {
            mDeltaX = 0;
        } else if ( mTotalTranslateX + mDeltaX < xoffset - (mCurrentVideoWidth - mLayoutWidth)/2) {
            mDeltaX = 0;
        }

        if (mTotalTranslateY + mDeltaY > yoffset + (mCurrentVideoHeight - mLayoutHeight)/2){
            mDeltaY = 0;
        } else if ( mTotalTranslateY + mDeltaY < yoffset - (mCurrentVideoHeight - mLayoutHeight)/2){
            mDeltaY = 0;
        }

        float translateX = mTotalTranslateX + mDeltaX;
        float translateY = mTotalTranslateY + mDeltaY;

        matrix.postTranslate(translateX , translateY );
        mTotalTranslateX = translateX;
        mTotalTranslateY = translateY;
    }

    private void Normal(int widthSpecMode, int heightSpecMode){
        float ratio = 1.0f;
        float hOffset = 0.0f;
        float vOffset = 0.0f;
        int videoWidth  = mVideoWidth;
        int videoHeight = mVideoHeight;
        int width       = mLayoutWidth;
        int height      = mLayoutHeight;
        Matrix matrix   = mMatrix;

        if (mVideoSarNum > 0 && mVideoSarDen > 0) {
            videoWidth = videoWidth * mVideoSarNum / mVideoSarDen;
        }

        float scaleX = (float)videoWidth/mLayoutWidth;
        float scaleY = (float)videoHeight/mLayoutHeight;

        if ((mDegree / 90) %2 != 0){
            videoHeight = mVideoWidth;
            videoWidth = mVideoHeight;

            if (mVideoSarNum > 0 && mVideoSarDen > 0) {
                videoHeight = videoHeight * mVideoSarNum / mVideoSarDen;
            }
        }

        mInitRatio =  Math.min((float) width / videoWidth, (float) height / videoHeight);

        switch (mScaleMode) {
            case IMediaPlayer.MODE_ZOOM_CROPPING:
                ratio = Math.max((float) width / videoWidth, (float) height / videoHeight);
                hOffset = vOffset = 0.0f;
                mTotalRatio  = ratio;
                break;

            case IMediaPlayer.MODE_ZOOM_TO_FIT:
                ratio = Math.min((float) width / videoWidth, (float) height / videoHeight);
                hOffset = mHOffset;
                vOffset = mVOffset;
                mTotalRatio  = ratio;
                break;
            case IMediaPlayer.MODE_NOZOOM_TO_FIT:
                if ((mDegree / 90) %2 != 0) {
                    scaleX = (float) height/width;
                    scaleY = (float) width/height;
                }
                else {
                    scaleX = 1.0f;
                    scaleY = 1.0f;
                }
                mTotalRatio = mInitRatio = ratio;
                break;
        }


        if ((mDegree / 90) %2 != 0){
            mCurrentVideoWidth = height * scaleY * ratio;
            mCurrentVideoHeight = width * scaleX * ratio;
        }
        else
        {
            mCurrentVideoWidth = width * scaleX * ratio;
            mCurrentVideoHeight = height * scaleY * ratio;
        }

        matrix.reset();

        matrix.postScale(ratio*scaleX, ratio*scaleY);
        matrix.postRotate(mDegree);

        float translateX = 0.0f;
        float translateY = 0.0f;


        switch (mDegree){
            case 0:
                translateX = ((width - mCurrentVideoWidth) / 2f);
                translateY = ((height - mCurrentVideoHeight) / 2f);
                break;
            case -90:
                translateX = (width - mCurrentVideoWidth)/2;
                translateY = (height + mCurrentVideoHeight)/2;
                break;
            case -180:
                translateX = (width + mCurrentVideoWidth)/2;
                translateY = (height + mCurrentVideoHeight)/2;
                break;
            case -270:
                translateX = ((width + mCurrentVideoWidth) / 2f);
                translateY = ((height - mCurrentVideoHeight) / 2f);
                break;
        }
        mTotalTranslateX = translateX + hOffset*width/2;
        mTotalTranslateY = translateY - vOffset* height/2;

        matrix.postTranslate(mTotalTranslateX, mTotalTranslateY);
//            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY)
//                matrix.postTranslate(mHOffset*width/2,-mVOffset* height/2);
//            else if (widthSpecMode == MeasureSpec.EXACTLY){
//                matrix.postTranslate(mHOffset*width/2,0);
//            }
//            else if (heightSpecMode == MeasureSpec.EXACTLY){
//                matrix.postTranslate(0,-mVOffset* height/2);
//            }

        mMeasureWidth = (int) (width * ratio * scaleX );
        mMeasureHeight =(int) (height * ratio * scaleY );

    }

    private void Measure(int widthMeasureSpec, int heightMeasureSpec){
//        MideaUtils.getInstance().log(TAG,"Measure-->widthMeasureSpec:"+widthMeasureSpec+",heightMeasureSpec:"+heightMeasureSpec+",mVideoWidth:"+mVideoWidth+",mVideoHeight:"+mVideoHeight);
        if(mVideoWidth == 0 || mVideoHeight == 0) return;
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        mLayoutWidth = widthSpecSize;
        mLayoutHeight = heightSpecSize;

        if (mCurrentDispStatus == STATUS_NORMAL){
            Normal(widthSpecMode, heightSpecMode);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setAnimationMatrix(mMatrix);//setTransform方法SurfaceView不支持,使用setAnimationMatrix代替
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Measure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    public void setVideoScaleRatio(float ratio, float x ,float y){
        //if ( (ratio < mTotalRatio && ratio < mInitRatio) || (ratio > mTotalRatio && ratio > 100*mInitRatio))
        if (( ratio < 0.25) || ( ratio > 100 ))
            return ;

        if (( mScaleMode == IMediaPlayer.MODE_ZOOM_TO_FIT)&&
                (mHOffset > 0.0f  ||
                        mHOffset < 0.0f ||
                        mVOffset > 0.0f ||
                        mVOffset < 0.0f))
            return ;

        mScaledRatio = ratio/mTotalRatio;
        mTotalRatio = ratio;
        mCenterPointX = x;
        mCenterPointY = y;
        mCurrentDispStatus = STATUS_ZOOM;
        Zoom();
        requestLayout();
    }

    public float getVideoScaleRatio(){
        return mTotalRatio;
    }

    public  void setVerticalOrientation(boolean vertical){
        mVerticalOrientation = vertical;
        mCurrentDispStatus = STATUS_NORMAL;
        requestLayout();
    }

    void setVideoOffset(float horizontal, float vertical){
        mHOffset = horizontal;
        mVOffset = vertical;
        mCurrentDispStatus = STATUS_NORMAL;
        requestLayout();
    }

    void moveVideo(float deltaX, float deltaY){
        if (( mScaleMode == IMediaPlayer.MODE_ZOOM_TO_FIT)&&
                (mHOffset > 0.0f  ||
                        mHOffset < 0.0f ||
                        mVOffset > 0.0f ||
                        mVOffset < 0.0f))
            return ;

        mDeltaX = deltaX;
        mDeltaY = deltaY;
        mCurrentDispStatus = STATUS_MOVE;
        Move();
        requestLayout();
    }

    public int getMeasureWidth(){
        return  mMeasureWidth;
    }

    public int getMeasureHeight(){
        return  mMeasureHeight;
    }
}