package gold.android.videoplayer.base.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import gold.android.videoplayer.base.adapter.widget.BaseViewHolder;
import java.util.List;

public abstract class BaseNoimalAdapter<T,VH extends BaseViewHolder> extends BaseAdapter <T,VH> {

    private int mRLayoutRes;//布局ID

    public BaseNoimalAdapter(@LayoutRes int resource){
        this(resource,null);
    }

    public BaseNoimalAdapter(List<T> data){
        this(0,data);
    }

    public BaseNoimalAdapter(@LayoutRes int resource, List<T> data){
        super(data);
        super.mData=data;
        this.mRLayoutRes=resource;
    }

    @Override
    protected View getItemTypeView(@NonNull ViewGroup viewGroup, int itemType) {
        return LayoutInflater.from(viewGroup.getContext()).inflate(mRLayoutRes, viewGroup, false);
    }
}