package cn.huangchengxi.funnytrip.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.huangchengxi.funnytrip.R;

public class ClockListHolder extends RecyclerView.ViewHolder {
    public TextView location;
    public TextView time;
    public View leftDecorator;

    public ClockListHolder(@NonNull View itemView) {
        super(itemView);
        location=itemView.findViewById(R.id.clock_location);
        time=itemView.findViewById(R.id.clock_time);
        leftDecorator=itemView.findViewById(R.id.left_bar);
    }
}
