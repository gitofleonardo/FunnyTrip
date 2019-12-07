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
import cn.huangchengxi.funnytrip.item.BottomRVFriendItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.viewholder.InviteFriendBottomRVHodler;

public class InviteFriendBottomRVAdapter extends RecyclerView.Adapter<InviteFriendBottomRVHodler> {
    private List<BottomRVFriendItem> list;
    private Context context;

    public InviteFriendBottomRVAdapter(Context context,List<BottomRVFriendItem> list) {
        this.list=list;
        this.context=context;
    }

    @NonNull
    @Override
    public InviteFriendBottomRVHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InviteFriendBottomRVHodler(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_choose_friend_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull InviteFriendBottomRVHodler holder, final int position) {
        final BottomRVFriendItem item=list.get(position);
        holder.check.setVisibility(item.isCheck()? View.VISIBLE:View.GONE);
        holder.name.setText(item.getUserName());
        holder.friendContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.get(position).setCheck(!list.get(position).isCheck());
                notifyItemChanged(position);
            }
        });
        if (item.getPortraitUrl()!=null && !item.getPortraitUrl().equals("") && !item.getPortraitUrl().equals("null")){
            Glide.with(context).load(HttpHelper.PIC_SERVER_HOST+item.getPortraitUrl()).into(holder.portrait);
        }else{
            holder.portrait.setImageResource(R.drawable.portrait);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
