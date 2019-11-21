package cn.huangchengxi.funnytrip.viewholder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.huangchengxi.funnytrip.R;

public class RoutePositionHolder extends RecyclerView.ViewHolder {
    public View rect;
    public View top;
    public View bottom;
    public TextView position;
    public FrameLayout topContainer;
    public FrameLayout bottomContainer;

    public RoutePositionHolder(@NonNull View itemView) {
        super(itemView);
        rect=itemView.findViewById(R.id.rect);
        top=itemView.findViewById(R.id.round_top);
        bottom=itemView.findViewById(R.id.round_bottom);
        position=itemView.findViewById(R.id.position);
        topContainer=itemView.findViewById(R.id.top_container);
        bottomContainer=itemView.findViewById(R.id.bottom_container);
    }
}
