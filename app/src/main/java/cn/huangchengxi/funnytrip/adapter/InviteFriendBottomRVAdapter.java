package cn.huangchengxi.funnytrip.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.BottomRVFriendItem;
import cn.huangchengxi.funnytrip.viewholder.InviteFriendBottomRVHodler;

public class InviteFriendBottomRVAdapter extends RecyclerView.Adapter<InviteFriendBottomRVHodler> {
    private List<BottomRVFriendItem> list;

    public InviteFriendBottomRVAdapter(List<BottomRVFriendItem> list) {
        this.list=list;
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
