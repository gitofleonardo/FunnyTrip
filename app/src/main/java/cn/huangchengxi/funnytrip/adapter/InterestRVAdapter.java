package cn.huangchengxi.funnytrip.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.InterestItem;
import cn.huangchengxi.funnytrip.viewholder.InterestRVHolder;

public class InterestRVAdapter extends RecyclerView.Adapter<InterestRVHolder> {
    private List<InterestItem> list;
    private OnItemClick onItemClick;

    public InterestRVAdapter(List<InterestItem> list) {
        this.list=list;
    }

    @NonNull
    @Override
    public InterestRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InterestRVHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_interest_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull InterestRVHolder holder, final int position) {
        InterestItem item=list.get(position);
        holder.name.setText(item.getName());
        if (item.isSelected()){
            holder.check.setVisibility(View.VISIBLE);
        }else{
            holder.check.setVisibility(View.GONE);
        }
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClick!=null){
                    onItemClick.onClick(position);
                }
                list.get(position).setSelected(!list.get(position).isSelected());
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public interface OnItemClick{
        void onClick(int position);
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }
}
