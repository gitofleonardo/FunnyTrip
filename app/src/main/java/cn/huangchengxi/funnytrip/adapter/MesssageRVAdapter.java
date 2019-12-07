package cn.huangchengxi.funnytrip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.MessageItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.viewholder.MessageRVHolder;

public class MesssageRVAdapter extends RecyclerView.Adapter<MessageRVHolder> {
    private List<MessageItem> list;
    private OnMessageClick onMessageClick;
    private Context context;

    public MesssageRVAdapter(Context context,List<MessageItem> list) {
        this.list=list;
        this.context=context;
    }

    @NonNull
    @Override
    public MessageRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_message_list_item,parent,false);
        return new MessageRVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageRVHolder holder, final int position) {
        MessageItem item=list.get(position);
        holder.time.setText(item.getFormattedDate());
        holder.name.setText(item.getHisName());
        holder.content.setText(item.getLatestContent());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMessageClick!=null){
                    onMessageClick.onClick(position,v);
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
    public void setOnMessageClick(OnMessageClick onMessageClick) {
        this.onMessageClick = onMessageClick;
    }

    public interface OnMessageClick{
        void onClick(int pos,View view);
    }
}
