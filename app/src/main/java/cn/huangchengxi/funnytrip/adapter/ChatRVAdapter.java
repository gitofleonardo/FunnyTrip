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
import cn.huangchengxi.funnytrip.item.ChatMessageItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.viewholder.ChatRVHolder;

public class ChatRVAdapter extends RecyclerView.Adapter<ChatRVHolder> {
    private List<ChatMessageItem> list;
    private Context context;
    private OnPortraitClick onPortraitClick;

    @NonNull
    @Override
    public ChatRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatRVHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chat_msg,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRVHolder holder, final int position) {
        ChatMessageItem item=list.get(position);
        if (item.isReceived()){
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
            if (item.getPortraitUrl()!=null && !item.getPortraitUrl().equals("") && !item.getPortraitUrl().equals("null")){
                Glide.with(context).load(HttpHelper.PIC_SERVER_HOST+item.getPortraitUrl()).into(holder.leftPortrait);
            }else{
                holder.leftPortrait.setImageResource(R.drawable.portrait);
            }
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
            if (item.getPortraitUrl()!=null && !item.getPortraitUrl().equals("") && !item.getPortraitUrl().equals("null")){
                Glide.with(context).load(HttpHelper.PIC_SERVER_HOST+item.getPortraitUrl()).into(holder.rightPortrait);
            }else{
                holder.rightPortrait.setImageResource(R.drawable.portrait);
            }
            if (item.isSent()){
                holder.sending.setVisibility(View.GONE);
            }else{
                Glide.with(context).load(R.drawable.loading).into(holder.sending);
                holder.sending.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public ChatRVAdapter(Context context,List<ChatMessageItem> items) {
        this.list=items;
        this.context=context;
    }
    public interface OnPortraitClick{
        void onClick(View view,int position);
    }

    public void setOnPortraitClick(OnPortraitClick onPortraitClick) {
        this.onPortraitClick = onPortraitClick;
    }
}
