package cn.huangchengxi.funnytrip.activity.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.friend.SearchFriendActivity;
import cn.huangchengxi.funnytrip.activity.friend.FriendDetailActivity;
import cn.huangchengxi.funnytrip.adapter.FriendRVAdapter;
import cn.huangchengxi.funnytrip.item.FriendItem;

public class FriendsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FriendRVAdapter adapter;
    private SwipeRefreshLayout srl;
    private ImageView back;
    private ImageView addFrind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        init();
    }
    private void init(){
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView=findViewById(R.id.friends_rv);
        adapter=new FriendRVAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<FriendItem> tmp=new ArrayList<>();
        tmp.add(new FriendItem("0","黄承喜"));
        tmp.add(new FriendItem("0","徐金华"));
        tmp.add(new FriendItem("0","王祖蓝"));
        tmp.add(new FriendItem("0","刘宏伟"));
        tmp.add(new FriendItem("0","潘靖东"));
        tmp.add(new FriendItem("0","成龙"));
        tmp.add(new FriendItem("0","小玉"));
        tmp.add(new FriendItem("0","特鲁"));
        tmp.add(new FriendItem("0","王祖蓝"));
        tmp.add(new FriendItem("0","刘宏伟"));
        tmp.add(new FriendItem("0","潘靖东"));
        tmp.add(new FriendItem("0","成龙"));
        tmp.add(new FriendItem("0","郑智浩"));
        tmp.add(new FriendItem("0","张磊"));
        tmp.add(new FriendItem("0","a张磊"));
        tmp.add(new FriendItem("0","#张磊"));
        tmp.add(new FriendItem("0","0张磊"));

        adapter.addAll(tmp);
        adapter.setOnUserClick(new FriendRVAdapter.OnUserClick() {
            @Override
            public void onClick(View view, FriendItem item) {
                FriendDetailActivity.startDetailActivity(FriendsActivity.this,item.getUserID());
            }
        });

        srl=findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (srl.isRefreshing()){
                    srl.setRefreshing(false);
                }
            }
        });
        addFrind=findViewById(R.id.add_friend);
        addFrind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FriendsActivity.this, SearchFriendActivity.class));
            }
        });
    }
}
