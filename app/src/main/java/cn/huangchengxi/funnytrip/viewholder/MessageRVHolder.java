package cn.huangchengxi.funnytrip.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.huangchengxi.funnytrip.R;

public class MessageRVHolder extends RecyclerView.ViewHolder {
    public ImageView portrait;
    public TextView name;
    public TextView time;
    public TextView content;
    public LinearLayout container;

    public MessageRVHolder(@NonNull View itemView) {
        super(itemView);
        portrait=itemView.findViewById(R.id.portrait);
        name=itemView.findViewById(R.id.name);
        time=itemView.findViewById(R.id.time);
        content=itemView.findViewById(R.id.content);
        container=itemView.findViewById(R.id.container);
    }
}
