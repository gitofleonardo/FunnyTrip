package cn.huangchengxi.funnytrip.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.tips.TipsCallback;
import cn.huangchengxi.funnytrip.item.TipsItem;
import cn.huangchengxi.funnytrip.viewholder.MyTipsRVHolder;

public class MyTipsRVAdapter extends RecyclerView.Adapter<MyTipsRVHolder> implements TipsCallback.OnRemoved {
    private List<TipsItem> list;
    private OnTipsRemoved onTipsRemoved;
    private OnItemClickListener onItemClickListener;

    public MyTipsRVAdapter(List<TipsItem> list) {
        this.list=list;
    }
    @NonNull
    @Override
    public MyTipsRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyTipsRVHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_my_tips_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyTipsRVHolder holder, final int position) {
        TipsItem item=list.get(position);
        holder.url.setText(item.getUrl());
        holder.name.setText(item.getName());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onClick(v,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onRemoved(int position) {
        if (onTipsRemoved!=null){
            onTipsRemoved.onRemoved(position);
        }
        list.remove(position);
        notifyItemRemoved(position);
    }
    public interface OnTipsRemoved{
        void onRemoved(int position);
    }

    public void setOnTipsRemoved(OnTipsRemoved onTipsRemoved) {
        this.onTipsRemoved = onTipsRemoved;
    }
    public interface OnItemClickListener{
        void onClick(View view,int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
