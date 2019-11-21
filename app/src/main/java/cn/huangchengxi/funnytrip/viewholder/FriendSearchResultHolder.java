package cn.huangchengxi.funnytrip.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.huangchengxi.funnytrip.R;

public class FriendSearchResultHolder extends RecyclerView.ViewHolder {
    public ImageView portrait;
    public TextView name;
    public LinearLayout container;

    public FriendSearchResultHolder(@NonNull View itemView) {
        super(itemView);
        portrait=itemView.findViewById(R.id.portrait);
        name=itemView.findViewById(R.id.name);
        container=itemView.findViewById(R.id.container);
    }
}
