package cn.huangchengxi.funnytrip.viewholder;

import android.media.Image;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.huangchengxi.funnytrip.R;

public class MomentRVHolder extends RecyclerView.ViewHolder {
    public ImageView portrait;
    public FrameLayout container;
    public TextView name;
    public TextView time;
    public TextView content;
    public ImageView contentImg;
    public ImageView likeImage;
    public TextView likeText;
    public LinearLayout likeItem;

    public MomentRVHolder(@NonNull View itemView) {
        super(itemView);
        portrait=itemView.findViewById(R.id.portrait);
        container=itemView.findViewById(R.id.moment_container);
        name=itemView.findViewById(R.id.name);
        time=itemView.findViewById(R.id.time);
        content=itemView.findViewById(R.id.content);
        contentImg=itemView.findViewById(R.id.content_img);
        likeImage=itemView.findViewById(R.id.like_image);
        likeText=itemView.findViewById(R.id.like_count);
        likeItem=itemView.findViewById(R.id.like);
    }
}
