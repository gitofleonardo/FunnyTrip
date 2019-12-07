package cn.huangchengxi.funnytrip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.FriendRequestItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.viewholder.FriendRequestHolder;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestHolder> {
    private List<FriendRequestItem> list;
    private OnPortraitClick onPortraitClick;
    private Context context;
    private OnAgreeClick onAgreeClick;

    public FriendRequestAdapter(List<FriendRequestItem> list, Context context) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public FriendRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendRequestHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_friend_request_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestHolder holder, final int position) {
        FriendRequestItem item=list.get(position);
        holder.name.setText(item.getName());
        holder.message.setText(item.getMessage());
        holder.agreeButton.setEnabled(!item.isAgreed());
        Glide.with(context).load(HttpHelper.PIC_SERVER_HOST+item.getPortraitUrl()).into(holder.portrait);
        holder.portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPortraitClick!=null){
                    onPortraitClick.onClick(position);
                }
            }
        });
        holder.agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onAgreeClick!=null){
                    onAgreeClick.onClick(position);
                }
            }
        });
        if (item.isAgreed()){
            holder.agreeButton.setEnabled(false);
            holder.agreeButton.setText("已同意");
        }else{
            holder.agreeButton.setEnabled(true);
            holder.agreeButton.setText("同意");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public interface OnPortraitClick{
        void onClick(int position);
    }

    public void setOnPortraitClick(OnPortraitClick onPortraitClick) {
        this.onPortraitClick = onPortraitClick;
    }
    public interface OnAgreeClick{
        void onClick(int position);
    }

    public void setOnAgreeClick(OnAgreeClick onAgreeClick) {
        this.onAgreeClick = onAgreeClick;
    }
}
