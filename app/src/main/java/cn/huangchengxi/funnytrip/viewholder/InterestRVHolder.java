package cn.huangchengxi.funnytrip.viewholder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.huangchengxi.funnytrip.R;

public class InterestRVHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public ImageView check;
    public FrameLayout container;

    public InterestRVHolder(@NonNull View itemView) {
        super(itemView);
        name=itemView.findViewById(R.id.name);
        check=itemView.findViewById(R.id.check_img);
        container=itemView.findViewById(R.id.container);
    }
}
