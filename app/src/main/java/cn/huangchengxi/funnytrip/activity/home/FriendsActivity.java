package cn.huangchengxi.funnytrip.activity.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.friend.SearchFriendActivity;
import cn.huangchengxi.funnytrip.activity.friend.FriendDetailActivity;
import cn.huangchengxi.funnytrip.adapter.FriendRVAdapter;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.databean.FriendBean;
import cn.huangchengxi.funnytrip.item.FriendItem;
import cn.huangchengxi.funnytrip.item.FriendSearchResultItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FriendsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FriendRVAdapter adapter;
    private SwipeRefreshLayout srl;
    private ImageView back;
    private ImageView addFrind;
    private List<FriendItem> list;

    private MyHandler myHandler=new MyHandler();

    private final int FETCH_FAILED=0;
    private final int FETCH_SUCCESS=1;
    private final int CONNECTION_FAILED=2;
    private final int NOT_LOGIN=3;

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
        adapter=new FriendRVAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list=new ArrayList<>();

        adapter.addAll(list);
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
        fetchMyFriends();
    }
    private void fetchMyFriends(){
        srl.setRefreshing(true);
        String uid=((MainApplication)getApplicationContext()).getUID();
        HttpHelper.fetchMyFriends(uid, this, new HttpHelper.OnFetchFriendResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }
            @Override
            public void onReturnSuccess(FriendBean bean) {
                list.clear();
                list.addAll(bean.getList());
                sendMessage(FETCH_SUCCESS);
            }
        });
    }
    private void sendMessage(int what){
        Message m=myHandler.obtainMessage();
        m.what=what;
        myHandler.sendMessage(m);
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            srl.setRefreshing(false);
            switch (msg.what){
                case CONNECTION_FAILED:
                    Toast.makeText(FriendsActivity.this, "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    break;
                case FETCH_FAILED:
                    Toast.makeText(FriendsActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                    break;
                case FETCH_SUCCESS:
                    adapter.clearAll();
                    adapter.addAll(list);
                    break;
                case NOT_LOGIN:
                    break;
            }
        }
    }
}
