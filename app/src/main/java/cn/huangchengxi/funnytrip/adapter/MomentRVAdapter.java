package cn.huangchengxi.funnytrip.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.MomentItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.viewholder.MomentRVHolder;

public class MomentRVAdapter extends RecyclerView.Adapter<MomentRVHolder> {
    private List<MomentItem> list;
    private Context context;
    private OnImageClick onImageClick;
    private OnContainerClick onContainerClick;
    private OnPortraitClick onPortraitClick;
    private OnLikeClick onLikeClick;

    public MomentRVAdapter(List<MomentItem> list,Context context) {
        this.list=list;
        this.context=context;
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
        holder.likeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onLikeClick!=null){
                    onLikeClick.onClick(view,position);
                }
            }
        });
        holder.likeText.setText(String.valueOf(item.getLikeCount()));

        if (item.getPortraitUrl()!=null && !item.getPortraitUrl().equals("") && !item.getPortraitUrl().equals("null")){
            Glide.with(context).load(HttpHelper.SERVER_HOST+item.getPortraitUrl()).into(holder.portrait);
        }else{
            Glide.with(context).load(R.drawable.portrait).into(holder.portrait);
        }
        if (item.getImgUrl()!=null && !item.getImgUrl().equals("") && !item.getImgUrl().equals("null")){
            ViewGroup.LayoutParams params=holder.contentImg.getLayoutParams();
            params.width=context.getResources().getDisplayMetrics().widthPixels;
            params.height=(int)(context.getResources().getDisplayMetrics().widthPixels*(9f/16f));
            holder.contentImg.setLayoutParams(params);

            Glide.with(context).load(HttpHelper.SERVER_HOST+item.getImgUrl()).into(holder.contentImg);
        }else{
            ViewGroup.LayoutParams params=holder.contentImg.getLayoutParams();
            params.width= ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height= ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.contentImg.setLayoutParams(params);
            holder.contentImg.setImageBitmap(null);
        }
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
    public interface OnLikeClick{
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

    public void setOnLikeClick(OnLikeClick onLikeClick) {
        this.onLikeClick = onLikeClick;
    }
}
