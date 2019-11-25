package cn.huangchengxi.funnytrip.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.TeamPartnerItem;
import cn.huangchengxi.funnytrip.viewholder.TeamPartnerRVHolder;

public class TeamPartnerRVAdapter extends RecyclerView.Adapter<TeamPartnerRVHolder> {
    private List<TeamPartnerItem> list;
    private OnPortraitClick onPortraitClick;

    public TeamPartnerRVAdapter(List<TeamPartnerItem> list) {
        this.list=list;
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
