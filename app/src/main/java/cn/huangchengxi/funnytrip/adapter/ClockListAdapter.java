package cn.huangchengxi.funnytrip.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.clock.BottomClocksCallback;
import cn.huangchengxi.funnytrip.item.ClockItem;
import cn.huangchengxi.funnytrip.viewholder.ClockListHolder;

public class ClockListAdapter extends RecyclerView.Adapter<ClockListHolder> implements BottomClocksCallback.OnItemRemoved {
    private int[] colors={Color.rgb(255,87,34),Color.rgb(0,188,212),Color.rgb(255,235,59),Color.rgb(76,175,80),Color.rgb(103,58,183),Color.rgb(3,169,244)};
    private List<ClockItem> list;
    private OnItemDelete onItemDelete;
    private boolean isSwipeEnable;
    private OnSwipeListener onSwipeListener;

    public ClockListAdapter(boolean isSwipeEnable) {
        list=new ArrayList<>();
        this.isSwipeEnable=isSwipeEnable;
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
        int colorIndex=(int)(Math.random()*(colors.length-1));
        holder.leftDecorator.setBackgroundColor(colors[colorIndex]);

        if (isSwipeEnable && onSwipeListener!=null){
            onSwipeListener.onSwipe(holder);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void add(ClockItem item){
        list.add(item);
        notifyDataSetChanged();
    }
    public void clear(){
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public boolean onRemoved(int position) {
        if (onItemDelete!=null){
            onItemDelete.onDelete(list.get(position).getId());
        }
        list.remove(position);
        notifyItemRemoved(position);
        return true;
    }
    public interface OnItemDelete{
        void onDelete(String clockId);
    }
    public interface OnSwipeListener{
        void onSwipe(ClockListHolder holder);
    }

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.onSwipeListener = onSwipeListener;
    }

    public void setOnItemDelete(OnItemDelete onItemDelete) {
        this.onItemDelete = onItemDelete;
    }
}
