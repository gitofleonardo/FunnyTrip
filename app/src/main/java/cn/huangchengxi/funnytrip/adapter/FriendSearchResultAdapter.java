package cn.huangchengxi.funnytrip.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.FriendSearchResultItem;
import cn.huangchengxi.funnytrip.viewholder.FriendSearchResultHolder;

public class FriendSearchResultAdapter extends RecyclerView.Adapter<FriendSearchResultHolder> {
    private List<FriendSearchResultItem> list;
    private OnResultClickListener onResultClickListener;

    @NonNull
    @Override
    public FriendSearchResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_search_result_item,parent,false);
        return new FriendSearchResultHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendSearchResultHolder holder, final int position) {
        FriendSearchResultItem item= list.get(position);
        holder.name.setText(item.getName());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onResultClickListener!=null){
                    onResultClickListener.onClick(v,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public FriendSearchResultAdapter(List<FriendSearchResultItem> list) {
        this.list=list;
    }
    public interface OnResultClickListener{
        void onClick(View view,int position);
    }

    public void setOnResultClickListener(OnResultClickListener onResultClickListener) {
        this.onResultClickListener = onResultClickListener;
    }
}
