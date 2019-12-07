package cn.huangchengxi.funnytrip.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.TeamInviteFriendItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.viewholder.TeamInviteFriendRVHodler;

public class TeamInviteFriendRVAdapter extends RecyclerView.Adapter<TeamInviteFriendRVHodler> {
    private List<TeamInviteFriendItem> list;
    private OnDeleteClick onDeleteClick;
    private Context context;

    public TeamInviteFriendRVAdapter(final List<TeamInviteFriendItem> list,final Context context) {
        this.list=list;
        this.onDeleteClick=new OnDeleteClick() {
            @Override
            public void onClick(View view, int position) {
                list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,list.size()-position);
            }
        };
        this.context=context;
    }
    @NonNull
    @Override
    public TeamInviteFriendRVHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TeamInviteFriendRVHodler(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_team_invite_friend_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TeamInviteFriendRVHodler holder, final int position) {
        TeamInviteFriendItem item=list.get(position);
        holder.name.setText(item.getUserName());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("name",String.valueOf(position));
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onDeleteClick!=null){
                    onDeleteClick.onClick(view,position);
                }
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
    public interface OnDeleteClick{
        void onClick(View view,int position);
    }

    public void setOnDeleteClick(OnDeleteClick onDeleteClick) {
        this.onDeleteClick = onDeleteClick;
    }
}
