package cn.huangchengxi.funnytrip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.TeamPartnerItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.viewholder.TeamPartnerRVHolder;

public class TeamPartnerRVAdapter extends RecyclerView.Adapter<TeamPartnerRVHolder> {
    private List<TeamPartnerItem> list;
    private OnPortraitClick onPortraitClick;
    private Context context;

    public TeamPartnerRVAdapter(final Context context,List<TeamPartnerItem> list) {
        this.list=list;
        this.context=context;
    }

    @NonNull
    @Override
    public TeamPartnerRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TeamPartnerRVHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_team_partner_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TeamPartnerRVHolder holder, final int position) {
        TeamPartnerItem item=list.get(position);
        holder.portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPortraitClick!=null){
                    onPortraitClick.onClick(view,position);
                }
            }
        });
        holder.name.setText(item.getUserName());
        if (item.getPortraitUrl()!=null && !item.getPortraitUrl().equals("") && !item.getPortraitUrl().equals("null")){
            Glide.with(context).load(HttpHelper.PIC_SERVER_HOST+item.getPortraitUrl()).into(holder.portrait);
        }else{
            holder.portrait.setImageResource(R.drawable.portrait);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public interface OnPortraitClick{
        void onClick(View view,int position);
    }

    public void setOnPortraitClick(OnPortraitClick onPortraitClick) {
        this.onPortraitClick = onPortraitClick;
    }
}
