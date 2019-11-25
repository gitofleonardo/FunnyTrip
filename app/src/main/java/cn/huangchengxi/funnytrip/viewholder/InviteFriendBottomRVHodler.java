package cn.huangchengxi.funnytrip.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.huangchengxi.funnytrip.R;

public class InviteFriendBottomRVHodler extends RecyclerView.ViewHolder {
    public ImageView portrait;
    public TextView name;
    public ImageView check;
    public LinearLayout friendContainer;

    public InviteFriendBottomRVHodler(@NonNull View itemView) {
        super(itemView);
        portrait=itemView.findViewById(R.id.friend_portrait);
        name=itemView.findViewById(R.id.friend_name);
        check=itemView.findViewById(R.id.is_friend_selected);
        friendContainer=itemView.findViewById(R.id.friend_container);
    }
}
