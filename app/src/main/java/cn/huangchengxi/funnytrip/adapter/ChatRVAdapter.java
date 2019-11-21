package cn.huangchengxi.funnytrip.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.ChatMessageItem;
import cn.huangchengxi.funnytrip.viewholder.ChatRVHolder;

public class ChatRVAdapter extends RecyclerView.Adapter<ChatRVHolder> {
    private List<ChatMessageItem> list;
    private OnPortraitClick onPortraitClick;

    @NonNull
    @Override
    public ChatRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatRVHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chat_msg,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRVHolder holder, final int position) {
        ChatMessageItem item=list.get(position);
        if (item.isRecieved()){
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

    public ChatRVAdapter(List<ChatMessageItem> items) {
        this.list=items;
    }
    public interface OnPortraitClick{
        void onClick(View view,int position);
    }

    public void setOnPortraitClick(OnPortraitClick onPortraitClick) {
        this.onPortraitClick = onPortraitClick;
    }
}
