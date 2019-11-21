package cn.huangchengxi.funnytrip.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.WeatherItem;
import cn.huangchengxi.funnytrip.viewholder.PositionRVHolder;

public class PositionRVAdapter extends RecyclerView.Adapter<PositionRVHolder> {
    private List<WeatherItem> list;
    private OnPositionClick onPositionClick;

    @NonNull
    @Override
    public PositionRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_position_item,parent,false);
        return new PositionRVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PositionRVHolder holder, final int position) {
        WeatherItem s=list.get(position);
        holder.position.setText(s.getName());
        holder.position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPositionClick!=null){
                    onPositionClick.onClick(v,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public PositionRVAdapter(ArrayList<WeatherItem> list) {
        this.list=list;
    }
    public interface OnPositionClick{
        void onClick(View view,int pos);
    }

    public void setOnPositionClick(OnPositionClick onPositionClick) {
        this.onPositionClick = onPositionClick;
    }
}
