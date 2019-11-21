package cn.huangchengxi.funnytrip.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.huangchengxi.funnytrip.R;

public class PositionRVHolder extends RecyclerView.ViewHolder {
    public TextView position;

    public PositionRVHolder(@NonNull View itemView) {
        super(itemView);
        position=itemView.findViewById(R.id.position);
    }
}
