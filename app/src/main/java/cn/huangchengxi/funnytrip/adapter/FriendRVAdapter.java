package cn.huangchengxi.funnytrip.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.FriendItem;
import cn.huangchengxi.funnytrip.utils.NameHelper;
import cn.huangchengxi.funnytrip.viewholder.FriendRVHolder;

public class FriendRVAdapter extends RecyclerView.Adapter<FriendRVHolder> {
    private List<FriendItem> list;
    private List<FriendItem> rvList;
    private HashMap<Character,ArrayList<FriendItem>> map;
    private OnUserClick onUserClick;

    public FriendRVAdapter() {
        list=new ArrayList<>();
        rvList=new ArrayList<>();
        map=new HashMap<>();
    }

    @NonNull
    @Override
    public FriendRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_friend_item,parent,false);
        return new FriendRVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRVHolder holder, final int position) {
        FriendItem item=rvList.get(position);
        if (item.isFriendItem()){
            holder.friendName.setText(item.getUserName());
            holder.letter.setVisibility(View.GONE);
            holder.friendItem.setVisibility(View.VISIBLE);
            holder.friendItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onUserClick!=null){
                        onUserClick.onClick(v,rvList.get(position));
                    }
                }
            });
        }else{
            holder.friendItem.setVisibility(View.GONE);
            holder.letter.setVisibility(View.VISIBLE);
            holder.letter.setText(item.getLetter()+"");
        }
    }
    public interface OnUserClick{
        void onClick(View view,FriendItem item);
    }

    public void setOnUserClick(OnUserClick onUserClick) {
        this.onUserClick = onUserClick;
    }

    @Override
    public int getItemCount() {
        return rvList.size();
    }
    public void addAll(Collection<FriendItem> collection){
        list.addAll(collection);
        rvList.clear();
        makeList();
        notifyDataSetChanged();
    }
    private void makeList(){
        for (FriendItem item:list){
            Log.e("eng",NameHelper.ChineseToFirstLetter(item.getUserName()));
            char f=NameHelper.ChineseToFirstLetter(item.getUserName()).charAt(0);
            if (f>='A' && f<='Z'){
                if (map.containsKey(f)){
                    map.get(f).add(item);
                }else{
                    ArrayList<FriendItem> l=new ArrayList<>();
                    l.add(item);
                    map.put(f,l);
                }
            }else{
                if (map.containsKey('#')){
                    map.get('#').add(item);
                }else{
                    ArrayList<FriendItem> l=new ArrayList<>();
                    l.add(item);
                    map.put('#',l);
                }
            }
        }
        for (int i='A';i<='Z';i++){
            Character key=new Character((char)i);
            if (map.containsKey((char)i)){
                FriendItem item=new FriendItem(key);
                rvList.add(item);
                for (FriendItem item1:map.get(key)){
                    rvList.add(item1);
                }
            }
        }
        if (map.containsKey('#')){
            rvList.add(new FriendItem('#'));
            for (FriendItem item:map.get('#')){
                rvList.add(item);
            }
        }

        for (FriendItem item:rvList){
            if (item.isFriendItem())
                Log.e("item",item.getUserName());
            else
                Log.e("item",item.getLetter()+"");
        }
    }
}
