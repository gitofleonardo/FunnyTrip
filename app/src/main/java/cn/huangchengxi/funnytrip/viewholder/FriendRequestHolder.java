package cn.huangchengxi.funnytrip.viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.huangchengxi.funnytrip.R;

public class FriendRequestHolder extends RecyclerView.ViewHolder {
    public ImageView portrait;
    public TextView name;
    public TextView message;
    public Button agreeButton;

    public FriendRequestHolder(@NonNull View itemView) {
        super(itemView);
        portrait=itemView.findViewById(R.id.portrait);
        name=itemView.findViewById(R.id.name);
        message=itemView.findViewById(R.id.message);
        agreeButton=itemView.findViewById(R.id.agree_button);
    }
}
