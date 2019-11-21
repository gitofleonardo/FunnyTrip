package cn.huangchengxi.funnytrip.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.PositionItem;
import cn.huangchengxi.funnytrip.viewholder.RoutePositionHolder;

public class RoutePositionAdapter extends RecyclerView.Adapter<RoutePositionHolder> {
    private List<PositionItem> items;

    public RoutePositionAdapter(List<PositionItem> list) {
        items=list;
    }

    @NonNull
    @Override
    public RoutePositionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoutePositionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_route_position,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RoutePositionHolder holder, int position) {
        PositionItem item=items.get(position);
        if (position==items.size()-1){
            holder.rect.setVisibility(View.GONE);
            holder.bottomContainer.setVisibility(View.GONE);
            holder.topContainer.setVisibility(View.VISIBLE);
        }else if (position==0){
            holder.rect.setVisibility(View.GONE);
            holder.bottomContainer.setVisibility(View.VISIBLE);
            holder.topContainer.setVisibility(View.GONE);
        }else{
            holder.rect.setVisibility(View.VISIBLE);
            holder.bottomContainer.setVisibility(View.GONE);
            holder.topContainer.setVisibility(View.GONE);
        }
        holder.position.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
