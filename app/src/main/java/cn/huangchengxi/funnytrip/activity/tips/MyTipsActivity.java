package cn.huangchengxi.funnytrip.activity.tips;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.Date;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.home.TipsActivity;
import cn.huangchengxi.funnytrip.adapter.MyTipsRVAdapter;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.databean.TipsResultBean;
import cn.huangchengxi.funnytrip.item.TipsItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyTipsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ImageView back;
    private SwipeRefreshLayout srl;
    private MyTipsRVAdapter adapter;
    private List<TipsItem> list;
    private List<TipsItem> newList;
    private final int CONNECTION_FAILED=0;
    private final int FETCH_FAILED=1;
    private final int FETCH_SUCCESS=2;
    private final int NOT_LOGIN=3;

    private final int DELETE_FAILED=4;
    private final int DELETE_SUCCESS=5;

    private MyHandler myHandler=new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tips);
        init();
    }
    private void init(){
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView=findViewById(R.id.tips_rv);
        list=new ArrayList<>();
        adapter=new MyTipsRVAdapter(list);
        adapter.setOnTipsRemoved(new MyTipsRVAdapter.OnTipsRemoved() {
            @Override
            public void onRemoved(int position) {
                deleteTipsFromServer(list.get(position).getId());
            }
        });
        adapter.setOnItemClickListener(new MyTipsRVAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                TipsActivity.startTipsActivity(MyTipsActivity.this,list.get(position).getUrl());
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper.Callback callback=new TipsCallback(adapter);
        ItemTouchHelper helper=new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);

        srl=findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //updateTips();
                fetchTipsFromServer(new Date().getTime(),true);
            }
        });
        fetchTipsFromServer(new Date().getTime(),true);
        //updateTips();
    }
    private void deleteTipsFromServer(String id){
        String uid=((MainApplication)getApplicationContext()).getUID();
        HttpHelper.deleteTip(id, uid, this, new HttpHelper.OnCommonResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }

            @Override
            public void onReturnSuccess() {
                sendMessage(DELETE_SUCCESS);
            }
        });
    }
    private void fetchTipsFromServer(long timeLimit,final boolean removed){
        String uid=((MainApplication)getApplicationContext()).getUID();
        HttpHelper.fetchMyTips(uid, timeLimit, this, new HttpHelper.OnTipsResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }

            @Override
            public void onReturnSuccess(TipsResultBean bean) {
                newList=bean.getList();
                sendMessage(FETCH_SUCCESS,removed);
            }
        });
    }
    private void sendMessage(int what,boolean removed){
        Message msg=myHandler.obtainMessage();
        msg.what=what;
        msg.obj=removed;
        myHandler.sendMessage(msg);
    }
    private void sendMessage(int what){
        Message msg=myHandler.obtainMessage();
        msg.what=what;
        myHandler.sendMessage(msg);
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CONNECTION_FAILED:
                    srl.setRefreshing(false);
                    Toast.makeText(MyTipsActivity.this, "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    break;
                case FETCH_FAILED:
                    srl.setRefreshing(false);
                    Toast.makeText(MyTipsActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                    break;
                case FETCH_SUCCESS:
                    srl.setRefreshing(false);
                    if ((Boolean)msg.obj){
                        list.clear();
                        list.addAll(newList);
                    }else{
                        list.addAll(newList);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case NOT_LOGIN:
                    break;
                case DELETE_FAILED:
                    Toast.makeText(MyTipsActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    fetchTipsFromServer(new Date().getTime(),true);
                    break;
            }
        }
    }

}
