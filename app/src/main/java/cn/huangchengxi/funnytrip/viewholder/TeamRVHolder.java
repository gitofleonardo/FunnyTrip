package cn.huangchengxi.funnytrip.viewholder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.huangchengxi.funnytrip.R;

public class TeamRVHolder extends RecyclerView.ViewHolder {
    public LinearLayout container;
    public View leftBar;
    public TextView teamName;
    public TextView hasMsg;
    public TextView latestMsg;

    public TeamRVHolder(@NonNull View itemView) {
        super(itemView);
        container=itemView.findViewById(R.id.team_container);
        leftBar=itemView.findViewById(R.id.left_bar);
        teamName=itemView.findViewById(R.id.team_name);
        hasMsg=itemView.findViewById(R.id.has_msg);
        latestMsg=itemView.findViewById(R.id.latest_msg);
    }
}
