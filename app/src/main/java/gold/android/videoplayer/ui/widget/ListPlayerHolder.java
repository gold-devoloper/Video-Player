package gold.android.videoplayer.ui.widget;

import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import gold.android.videoplayer.R;
import gold.android.videoplayer.base.adapter.widget.BaseViewHolder;


public class ListPlayerHolder extends BaseViewHolder {

    private FrameLayout mPlayerContainer;

    public ListPlayerHolder(@NonNull View itemView) {
        super(itemView);
        if(null!=itemView&&null!=itemView.findViewById(R.id.item_player_container)){
            mPlayerContainer=itemView.findViewById(R.id.item_player_container);
        }
        itemView.setTag(this);
    }

    public FrameLayout getPlayerContainer() {
        return mPlayerContainer;
    }
}