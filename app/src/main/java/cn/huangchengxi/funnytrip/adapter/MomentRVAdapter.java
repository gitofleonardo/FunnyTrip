package cn.huangchengxi.funnytrip.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.MomentItem;
import cn.huangchengxi.funnytrip.viewholder.MomentRVHolder;

public class MomentRVAdapter extends RecyclerView.Adapter<MomentRVHolder> {
    private List<MomentItem> list;
    private OnImageClick onImageClick;
    private OnContainerClick onContainerClick;
    private OnPortraitClick onPortraitClick;

    public MomentRVAdapter(List<MomentItem> list) {
        this.list=list;
    }

    @NonNull
    @Override
    public MomentRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MomentRVHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_moment_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MomentRVHolder holder, final int position) {
        MomentItem item=list.get(position);
        holder.content.setText(item.getContent());
        holder.time.setText(item.getFormattedTime());
        holder.name.setText(item.getName());
        holder.portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPortraitClick!=null){
                    onPortraitClick.onClick(v,position);
                }
            }
        });
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onContainerClick!=null){
                    onContainerClick.onClick(v,position);
                }
            }
        });
        holder.contentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImageClick!=null){
                    onImageClick.onClick(v,position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public interface OnImageClick{
        void onClick(View view,int position);
    }
    public interface OnContainerClick{
        void onClick(View view,int position);
    }
    public interface OnPortraitClick{
        void onClick(View view,int position);
    }
    public void setOnContainerClick(OnContainerClick onContainerClick) {
        this.onContainerClick = onContainerClick;
    }

    public void setOnImageClick(OnImageClick onImageClick) {
        this.onImageClick = onImageClick;
    }

    public void setOnPortraitClick(OnPortraitClick onPortraitClick) {
        this.onPortraitClick = onPortraitClick;
    }
}
