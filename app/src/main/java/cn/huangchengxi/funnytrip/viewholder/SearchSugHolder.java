package cn.huangchengxi.funnytrip.viewholder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.huangchengxi.funnytrip.R;

public class SearchSugHolder extends RecyclerView.ViewHolder {
    public TextView address;
    public TextView detail;
    public LinearLayout container;

    public SearchSugHolder(@NonNull View itemView) {
        super(itemView);
        address=itemView.findViewById(R.id.name);
        detail=itemView.findViewById(R.id.detail);
        container=itemView.findViewById(R.id.container);
    }
}
