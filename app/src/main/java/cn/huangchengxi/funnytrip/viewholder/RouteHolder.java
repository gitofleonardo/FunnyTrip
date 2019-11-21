package cn.huangchengxi.funnytrip.viewholder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.huangchengxi.funnytrip.R;

public class RouteHolder extends RecyclerView.ViewHolder {
    public LinearLayout container;
    public RecyclerView recyclerView;
    public View leftBar;
    public TextView title;

    public RouteHolder(@NonNull View itemView) {
        super(itemView);
        container=itemView.findViewById(R.id.container);
        recyclerView=itemView.findViewById(R.id.positions_rv);
        leftBar=itemView.findViewById(R.id.left_bar);
        title=itemView.findViewById(R.id.route_name);
    }
}
