package cn.huangchengxi.funnytrip.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.ClockItem;
import cn.huangchengxi.funnytrip.viewholder.ClockListHolder;

public class ClockListAdapter extends RecyclerView.Adapter<ClockListHolder> {
    private List<ClockItem> list;
    public ClockListAdapter() {
        list=new ArrayList<>();
    }
    @NonNull
    @Override
    public ClockListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_clock_item,parent,false);
        ClockListHolder holder=new ClockListHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClockListHolder holder, int position) {
        ClockItem item=list.get(position);
        holder.location.setText(item.getLocation());
        holder.time.setText(item.getFormattedTime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void add(ClockItem item){
        list.add(item);
        notifyDataSetChanged();
    }
}
