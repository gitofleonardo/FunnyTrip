package cn.huangchengxi.funnytrip.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.huangchengxi.funnytrip.R;

public class HomeMomentsRVHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    public TextView name;
    public TextView time;
    public TextView content;

    public HomeMomentsRVHolder(@NonNull View itemView) {
        super(itemView);
        imageView=itemView.findViewById(R.id.hm_portrait);
        name=itemView.findViewById(R.id.hm_name);
        time=itemView.findViewById(R.id.hm_time);
        content=itemView.findViewById(R.id.hm_content);
    }
}
