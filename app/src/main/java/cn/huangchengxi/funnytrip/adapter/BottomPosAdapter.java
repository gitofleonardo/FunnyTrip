package cn.huangchengxi.funnytrip.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.PositionItem;
import cn.huangchengxi.funnytrip.viewholder.BottomPosHolder;

public class BottomPosAdapter extends RecyclerView.Adapter<BottomPosHolder> {
    private List<PositionItem> list;

    public BottomPosAdapter(List<PositionItem> list) {
        this.list=list;
    }

    @NonNull
    @Override
    public BottomPosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BottomPosHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_bottom_pos_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull BottomPosHolder holder, int position) {
        PositionItem item=list.get(position);
        holder.name.setText(item.getName());
        holder.position.setText("经度"+item.getLatitude()+"\t纬度"+item.getLongitude());
        holder.index.setText(String.valueOf(position+1));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
