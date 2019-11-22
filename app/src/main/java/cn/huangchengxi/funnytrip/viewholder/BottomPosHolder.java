package cn.huangchengxi.funnytrip.viewholder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.huangchengxi.funnytrip.R;

public class BottomPosHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public TextView position;
    public TextView index;
    public FrameLayout touchFrame;

    public BottomPosHolder(@NonNull View itemView) {
        super(itemView);
        name=itemView.findViewById(R.id.name);
        position=itemView.findViewById(R.id.position);
        index=itemView.findViewById(R.id.index);
        touchFrame=itemView.findViewById(R.id.touch_frame);
    }
}
