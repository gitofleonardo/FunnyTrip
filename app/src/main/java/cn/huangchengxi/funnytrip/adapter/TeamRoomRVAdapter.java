package cn.huangchengxi.funnytrip.adapter;

import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.ChatMessageItem;
import cn.huangchengxi.funnytrip.item.ChatRoomMsgItem;
import cn.huangchengxi.funnytrip.viewholder.ChatRoomRVHolder;

public class TeamRoomRVAdapter extends RecyclerView.Adapter<ChatRoomRVHolder> {
    private List<ChatRoomMsgItem> list;
    private OnPortraitClick onPortraitClick;

    public TeamRoomRVAdapter(List<ChatRoomMsgItem> list) {
        this.list=list;
    }

    @NonNull
    @Override
    public ChatRoomRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatRoomRVHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chat_room_msg,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomRVHolder holder,final int position) {
        ChatRoomMsgItem item=list.get(position);
        if (item.isRecieved()){
            holder.leftName.setText(item.getName());
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftText.setText(item.getContent());
            holder.leftPortrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPortraitClick!=null){
                        onPortraitClick.onClick(v,position);
                    }
                }
            });
        }else{
            holder.rightName.setText(item.getName());
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.rightText.setText(item.getContent());
            holder.rightPortrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPortraitClick!=null){
                        onPortraitClick.onClick(v,position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public interface OnPortraitClick{
        void onClick(View view,int position);
    }

    public void setOnPortraitClick(OnPortraitClick onPortraitClick) {
        this.onPortraitClick = onPortraitClick;
    }
}
