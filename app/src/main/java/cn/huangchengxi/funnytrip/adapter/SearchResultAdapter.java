package cn.huangchengxi.funnytrip.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.mapapi.search.sug.SuggestionResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.viewholder.SearchSugHolder;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchSugHolder> {
    private OnAddressClick onAddressClick;
    private List<SuggestionResult.SuggestionInfo> list;

    public SearchResultAdapter() {
        this.list=new ArrayList<>();
    }

    @NonNull
    @Override
    public SearchSugHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchSugHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_sug_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchSugHolder holder, final int position) {
        SuggestionResult.SuggestionInfo info=list.get(position);
        holder.address.setText(info.key);
        holder.detail.setText(info.city+info.district);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAddressClick!=null){
                    onAddressClick.onClick(v,list.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public interface OnAddressClick{
        void onClick(View view, SuggestionResult.SuggestionInfo info);
    }

    public void setOnAddressClick(OnAddressClick onAddressClick) {
        this.onAddressClick = onAddressClick;
    }
    public void addAll(Collection<SuggestionResult.SuggestionInfo> infos){
        list.addAll(infos);
        notifyDataSetChanged();
    }
    public void clear(){
        list.clear();
        notifyDataSetChanged();
    }
}
