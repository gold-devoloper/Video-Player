package gold.android.videoplayer.pager.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import gold.android.videoplayer.pager.interfaces.IViewPager;

public abstract class BaseViewPager  extends FrameLayout implements IViewPager {

    protected static final String TAG="BaseViewPager";
    protected int mPosition;//当前的ITEM位置

    public BaseViewPager(@NonNull Context context) {
        this(context,null);
    }

    public BaseViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BaseViewPager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context,getLayoutId(),this);
        initViews();
    }

    /**
     * 返回布局文件ID
     * @return 布局文件ID
     */
    protected abstract int getLayoutId();

    /**
     * 初始化View
     */
    protected abstract void initViews();

    protected int getPosition() {
        return mPosition;
    }

    protected String getPositionStr(){
        return ",position:"+ mPosition;
    }
}