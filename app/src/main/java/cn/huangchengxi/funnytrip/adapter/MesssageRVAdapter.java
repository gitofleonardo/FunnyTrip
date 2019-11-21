package cn.huangchengxi.funnytrip.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.MessageItem;
import cn.huangchengxi.funnytrip.viewholder.MessageRVHolder;

public class MesssageRVAdapter extends RecyclerView.Adapter<MessageRVHolder> {
    private List<MessageItem> list;
    private OnMessageClick onMessageClick;

    public MesssageRVAdapter() {
        list=new ArrayList<>();
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void add(MessageItem item){
        list.add(item);
        notifyDataSetChanged();
    }

    public void setOnMessageClick(OnMessageClick onMessageClick) {
        this.onMessageClick = onMessageClick;
    }

    public interface OnMessageClick{
        public void onClick(int pos,View view);
    }
}
