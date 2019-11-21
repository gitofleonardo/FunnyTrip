package cn.huangchengxi.funnytrip.activity.friend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.adapter.FriendSearchResultAdapter;
import cn.huangchengxi.funnytrip.item.FriendSearchResultItem;

public class SearchFriendActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView back;
    private TextView search;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout srl;
    private List<FriendSearchResultItem> list;
    private FriendSearchResultAdapter adapter;

    private MyHandler myHandler=new MyHandler();
    private final int SEARCH_OK=1;
    private final int SEARCH_FAILED=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        init();
    }
    private void init(){
        toolbar=findViewById(R.id.toolbar);
        back=findViewById(R.id.back);
        search=findViewById(R.id.search);
        recyclerView=findViewById(R.id.search_result_rv);
        srl=findViewById(R.id.srl);
        setSupportActionBar(toolbar);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do search process
            }
        });
        list=new ArrayList<>();
        list.add(new FriendSearchResultItem("0","黄承喜","nil"));
        list.add(new FriendSearchResultItem("0","潘靖东","nil"));
        list.add(new FriendSearchResultItem("0","徐金华","nil"));
        list.add(new FriendSearchResultItem("0","刘宏伟","nil"));
        adapter=new FriendSearchResultAdapter(list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //do search process
                srl.setRefreshing(false);
            }
        });
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case SEARCH_OK:
                    break;
                case SEARCH_FAILED:
                    Toast.makeText(SearchFriendActivity.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
