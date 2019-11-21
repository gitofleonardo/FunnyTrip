package cn.huangchengxi.funnytrip.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.HomeMomentItem;
import cn.huangchengxi.funnytrip.viewholder.HomeMomentsRVHolder;

public class HomeMomentsRVAdapter extends RecyclerView.Adapter<HomeMomentsRVHolder> {
    private List<HomeMomentItem> list;

    public HomeMomentsRVAdapter() {
        list=new ArrayList<>();
    }

    @NonNull
    @Override
    public HomeMomentsRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_home_rv_item,parent,false);
        HomeMomentsRVHolder homeMomentsRVHolder=new HomeMomentsRVHolder(view);
        return homeMomentsRVHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeMomentsRVHolder holder, int position) {
        HomeMomentItem item=list.get(position);
        holder.content.setText(item.getContent());
        holder.name.setText(item.getName());
        holder.time.setText(item.getFormattedDate());

        ViewGroup.LayoutParams params=holder.imageView.getLayoutParams();
        params.height=params.width;
        holder.imageView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void add(HomeMomentItem item){
        list.add(item);
        notifyDataSetChanged();
    }
}
