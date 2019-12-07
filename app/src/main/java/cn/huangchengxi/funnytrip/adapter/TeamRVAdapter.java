package cn.huangchengxi.funnytrip.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.TeamItem;
import cn.huangchengxi.funnytrip.viewholder.TeamRVHolder;

public class TeamRVAdapter extends RecyclerView.Adapter<TeamRVHolder> {
    private int[] colors={Color.rgb(255,87,34),Color.rgb(0,188,212),Color.rgb(255,235,59),Color.rgb(76,175,80),Color.rgb(103,58,183),Color.rgb(3,169,244)};
    private List<TeamItem> list;
    private OnTeamClick onTeamClick;

    public TeamRVAdapter(List<TeamItem> list) {
        this.list=list;
    }

    @NonNull
    @Override
    public TeamRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TeamRVHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_team_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TeamRVHolder holder, final int position) {
        TeamItem item=list.get(position);
        holder.teamName.setText(item.getTeamName());
        holder.hasMsg.setText(item.getFormattedDate());
        int colorIndex=(int)(Math.random()*(colors.length-1));
        holder.leftBar.setBackgroundColor(colors[colorIndex]);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTeamClick!=null){
                    onTeamClick.onClick(view,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public interface OnTeamClick{
        void onClick(View view,int position);
    }

    public void setOnTeamClick(OnTeamClick onTeamClick) {
        this.onTeamClick = onTeamClick;
    }
}
