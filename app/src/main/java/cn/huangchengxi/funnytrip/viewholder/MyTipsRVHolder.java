package cn.huangchengxi.funnytrip.viewholder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.huangchengxi.funnytrip.R;

public class MyTipsRVHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public TextView url;
    public LinearLayout container;

    public MyTipsRVHolder(@NonNull View itemView) {
        super(itemView);
        name=itemView.findViewById(R.id.name);
        url=itemView.findViewById(R.id.url);
        container=itemView.findViewById(R.id.container);
    }
}
