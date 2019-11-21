package cn.huangchengxi.funnytrip.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.RouteItem;
import cn.huangchengxi.funnytrip.viewholder.RouteHolder;

public class RouteAdapter extends RecyclerView.Adapter<RouteHolder> {
    private int[] colors={Color.rgb(255,87,34),Color.rgb(0,188,212),Color.rgb(255,235,59),Color.rgb(76,175,80),Color.rgb(103,58,183),Color.rgb(3,169,244)};
    private List<RouteItem> list;
    private Context context;

    public RouteAdapter(List<RouteItem> list,Context context) {
        this.list=list;
        this.context=context;
    }

    @NonNull
    @Override
    public RouteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RouteHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_route_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RouteHolder holder, int position) {
        RouteItem routeItem=list.get(position);
        RoutePositionAdapter adapter=new RoutePositionAdapter(routeItem.getRoute());
        holder.recyclerView.setAdapter(adapter);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.title.setText(routeItem.getRouteId());
        int colorIndex=(int)(Math.random()*(colors.length-1));
        holder.leftBar.setBackgroundColor(colors[colorIndex]);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
