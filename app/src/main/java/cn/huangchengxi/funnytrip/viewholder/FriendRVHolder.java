package cn.huangchengxi.funnytrip.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.huangchengxi.funnytrip.R;

public class FriendRVHolder extends RecyclerView.ViewHolder {
    public ImageView portrait;
    public TextView friendName;
    public TextView letter;
    public LinearLayout friendItem;

    public FriendRVHolder(@NonNull View itemView) {
        super(itemView);
        portrait=itemView.findViewById(R.id.portrait);
        friendName=itemView.findViewById(R.id.name);
        letter=itemView.findViewById(R.id.letter_item);
        friendItem=itemView.findViewById(R.id.friend_item);
    }
}
