package cn.huangchengxi.funnytrip.viewholder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.huangchengxi.funnytrip.R;

public class TeamInviteFriendRVHodler extends RecyclerView.ViewHolder {
    public ImageView portrait;
    public TextView name;
    public FrameLayout delete;

    public TeamInviteFriendRVHodler(@NonNull View itemView) {
        super(itemView);
        portrait=itemView.findViewById(R.id.portrait);
        name=itemView.findViewById(R.id.name);
        delete=itemView.findViewById(R.id.delete);
    }
}
