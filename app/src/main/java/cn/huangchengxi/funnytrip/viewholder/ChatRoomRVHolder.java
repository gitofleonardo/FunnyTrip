package cn.huangchengxi.funnytrip.viewholder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.huangchengxi.funnytrip.R;

public class ChatRoomRVHolder extends RecyclerView.ViewHolder {
    public FrameLayout leftLayout;
    public FrameLayout rightLayout;
    public ImageView leftPortrait;
    public ImageView rightPortrait;
    public TextView leftText;
    public TextView rightText;
    public TextView leftName;
    public TextView rightName;

    public ChatRoomRVHolder(@NonNull View itemView) {
        super(itemView);
        leftLayout=itemView.findViewById(R.id.left);
        rightLayout=itemView.findViewById(R.id.right);
        leftPortrait=itemView.findViewById(R.id.sender_portrait);
        rightPortrait=itemView.findViewById(R.id.my_portrait);
        leftText=itemView.findViewById(R.id.sender_text);
        rightText=itemView.findViewById(R.id.my_text);
        leftName=itemView.findViewById(R.id.left_name);
        rightName=itemView.findViewById(R.id.right_name);
    }
}
