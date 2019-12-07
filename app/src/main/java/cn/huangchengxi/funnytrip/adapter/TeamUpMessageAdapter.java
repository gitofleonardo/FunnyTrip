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
import cn.huangchengxi.funnytrip.item.TeamInvitationItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.viewholder.FriendRequestHolder;

public class TeamUpMessageAdapter extends RecyclerView.Adapter<FriendRequestHolder> {
    private List<TeamInvitationItem> list;
    private Context context;
    private OnAgreeListener onAgreeListener;

    public TeamUpMessageAdapter(List<TeamInvitationItem> list,final Context context) {
        this.list=list;
        this.context=context;
    }

    @NonNull
    @Override
    public FriendRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendRequestHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_friend_request_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestHolder holder, final int position) {
        TeamInvitationItem item=list.get(position);
        if (item.isAgreed()){
            holder.agreeButton.setEnabled(false);
            holder.agreeButton.setText("已同意");
        }else{
            holder.agreeButton.setEnabled(true);
            holder.agreeButton.setText("同意");
            if (onAgreeListener!=null){
                holder.agreeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onAgreeListener.onAgree(position);
                    }
                });
            }
        }
        holder.name.setText(item.getInvitorName());
        holder.message.setText("邀请您加入队伍"+item.getTeamName());
        if (item.getPortraitUrl()!=null && !item.getPortraitUrl().equals("") && !item.getPortraitUrl().equals("null")){
            Glide.with(context).load(HttpHelper.PIC_SERVER_HOST+item.getPortraitUrl()).into(holder.portrait);
        }else{
            holder.portrait.setImageResource(R.drawable.portrait);
        }
    }

    public interface OnAgreeListener{
        void onAgree(int position);
    }

    public void setOnAgreeListener(OnAgreeListener onAgreeListener) {
        this.onAgreeListener = onAgreeListener;
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
}
