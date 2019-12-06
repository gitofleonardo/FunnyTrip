package cn.huangchengxi.funnytrip.activity.friend;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import cn.huangchengxi.funnytrip.activity.service.WebSocketMessageService;
import cn.huangchengxi.funnytrip.adapter.FriendRequestAdapter;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.databean.FriendRequestResultBean;
import cn.huangchengxi.funnytrip.item.FriendRequestItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FriendInvitationsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FriendRequestAdapter adapter;
    private List<FriendRequestItem> list;
    private Toolbar toolbar;
    private ImageView back;

    private MyHandler myHandler=new MyHandler();
    private ServiceConnection connection;
    private WebSocketMessageService.WebSocketClientBinder binder;
    private AlertDialog dialog;

    private final int NOT_LOGIN=0;
    private final int CONNECTION_FAILED=1;
    private final int FETCH_SUCCESS=2;
    private final int FETCH_FAILED=3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_invitations);
        init();
    }
    private void init(){
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        recyclerView=findViewById(R.id.friend_request_rv);
        list=new ArrayList<>();
        adapter=new FriendRequestAdapter(list,this);
        adapter.setOnPortraitClick(new FriendRequestAdapter.OnPortraitClick() {
            @Override
            public void onClick(int position) {
                FriendDetailActivity.startDetailActivity(FriendInvitationsActivity.this,list.get(position).getUid());
            }
        });
        adapter.setOnAgreeClick(new FriendRequestAdapter.OnAgreeClick() {
            @Override
            public void onClick(int position) {
                String toUID=((MainApplication)getApplicationContext()).getUID();
                String fromUID=list.get(position).getUid();

                String json="{" +
                        "\"type\":\"agree_friend_request\"," +
                        "\"from_uid\":\""+fromUID+"\"," +
                        "\"to_uid\":\""+toUID+"\"" +
                        "}";
                if (binder!=null){
                    binder.getService().sendMessage(json, new WebSocketMessageService.OnMessageCallback() {
                        @Override
                        public void onError() {
                            if (dialog!=null){
                                dialog.dismiss();
                            }
                        }
                        @Override
                        public void onSuccess() {
                            if (dialog!=null){
                                dialog.dismiss();
                            }
                            Toast.makeText(FriendInvitationsActivity.this, "已同意请求", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bindService();
        fetchFriendRequest();
    }
    private void bindService(){
        connection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                binder=(WebSocketMessageService.WebSocketClientBinder)iBinder;
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            }
        };
        Intent intent=new Intent(FriendInvitationsActivity.this, WebSocketMessageService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }
    private void fetchFriendRequest(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("正在获取").setCancelable(false).setView(R.layout.view_processing_dialog);
        dialog=builder.show();

        String uid=((MainApplication)getApplicationContext()).getUID();
        HttpHelper.fetchFriendRequest(uid, this, new HttpHelper.OnFetchFriendRequestResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }
            @Override
            public void onReturnSuccess(FriendRequestResultBean bean) {
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
            if (dialog!=null){
                dialog.dismiss();
            }
            switch (msg.what){
                case FETCH_SUCCESS:
                    adapter.notifyDataSetChanged();
                    break;
                case FETCH_FAILED:
                    Toast.makeText(FriendInvitationsActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECTION_FAILED:
                    Toast.makeText(FriendInvitationsActivity.this, "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    break;
                case NOT_LOGIN:
                    break;
            }
        }
    }
    @Override
    protected void onDestroy() {
        if (connection!=null){
            unbindService(connection);
        }
        super.onDestroy();
    }
}
