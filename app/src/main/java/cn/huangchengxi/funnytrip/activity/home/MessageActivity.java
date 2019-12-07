package cn.huangchengxi.funnytrip.activity.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.friend.ChattingActivity;
import cn.huangchengxi.funnytrip.activity.friend.FriendInvitationsActivity;
import cn.huangchengxi.funnytrip.activity.service.WebSocketMessageService;
import cn.huangchengxi.funnytrip.activity.team.TeamInvitationActivity;
import cn.huangchengxi.funnytrip.adapter.MesssageRVAdapter;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.broadcast.MessageReceiver;
import cn.huangchengxi.funnytrip.databean.UserInformationBean;
import cn.huangchengxi.funnytrip.item.MessageItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.utils.sqlite.LocalUsersUpdate;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;
import cn.huangchengxi.funnytrip.view.HomeAppView;

public class MessageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MesssageRVAdapter adapter;
    private HomeAppView newFriend;
    private HomeAppView systemMsg;
    private HomeAppView replyMe;
    private ImageView back;
    private Toolbar toolbar;
    private SwipeRefreshLayout srl;
    private WebSocketMessageService.WebSocketClientBinder binder;
    private ServiceConnection connection=null;
    private List<MessageItem> list;
    private MyHandler myHandler=new MyHandler();
    private MessageReceiver receiver;

    private final int CONNECTION_FAILED=0;
    private final int FETCH_SUCCESS=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        init();
        registerMessageReceiver();
    }
    private void init(){
        back=findViewById(R.id.back);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView=findViewById(R.id.message_rv);
        list=new ArrayList<>();
        adapter=new MesssageRVAdapter(this,list);
        adapter.setOnMessageClick(new MesssageRVAdapter.OnMessageClick() {
            @Override
            public void onClick(int pos, View view) {
                ChattingActivity.startChattingActivity(MessageActivity.this,list.get(pos).getHisID());
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newFriend=findViewById(R.id.new_friends);
        newFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessageActivity.this, FriendInvitationsActivity.class));
            }
        });
        systemMsg=findViewById(R.id.system_msg);
        systemMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessageActivity.this, TeamInvitationActivity.class));
            }
        });
        replyMe=findViewById(R.id.reply);

        srl=findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (srl.isRefreshing()){
                    srl.setRefreshing(false);
                    binder.getService().fetchUnread();
                }
            }
        });
        initFromLocalDatabase();
        bindService();
    }
    private void registerMessageReceiver(){
        receiver=new MessageReceiver();
        receiver.setOnMessageReceived(new MessageReceiver.OnMessageReceived() {
            @Override
            public void OnReveived(String messageID,String fromUID, String toUID, String content, long time,String context) {
                for (int i=0;i<list.size();i++){
                    if (list.get(i).getHisID().equals(fromUID) && ((MainApplication)getApplicationContext()).getUID().equals(toUID)){
                        list.get(i).setLatestContent(content);
                        MessageItem item=list.get(i);
                        list.remove(i);
                        list.add(0,item);
                        adapter.notifyDataSetChanged();
                        return;
                    }else if (list.get(i).getHisID().equals(toUID) && ((MainApplication)getApplicationContext()).getUID().equals(fromUID)){
                        list.get(i).setLatestContent(content);
                        MessageItem item=list.get(i);
                        list.remove(i);
                        list.add(0,item);
                        adapter.notifyDataSetChanged();
                        return;
                    }
                }
                fetchUsersInfo(fromUID);
                MessageItem item=new MessageItem(fromUID,content,fromUID,time,"");
                list.add(0,item);
                adapter.notifyItemInserted(0);
            }
            @Override
            public void onSuccessSent(String messageID) {

            }
        });
        IntentFilter intentFilter=new IntentFilter("cn.huangchengxi.funnytrip.MESSAGE_RECEIVER");
        registerReceiver(receiver,intentFilter);
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
        Intent intent=new Intent(this, WebSocketMessageService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }
    private void initFromLocalDatabase(){
        String myUID=((MainApplication)getApplicationContext()).getUID();

        SqliteHelper helper=new SqliteHelper(this,"messages",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("messages",null,"to_uid=? or from_uid=?",new String[]{myUID,myUID},null,null,"create_time desc");
        List<MessageItem> items=new ArrayList<>();
        Map<String,Boolean> map=new HashMap<>();

        if (cursor.moveToFirst()){
            do{
                String toUID=cursor.getString(cursor.getColumnIndex("to_uid"));
                String fromUID=cursor.getString(cursor.getColumnIndex("from_uid"));
                if (toUID.equals(myUID)){
                    if (!map.containsKey(fromUID)){
                        String latestContent=cursor.getString(cursor.getColumnIndex("content"));
                        long time=cursor.getLong(cursor.getColumnIndex("create_time"));
                        MessageItem item=new MessageItem(fromUID,latestContent,fromUID,time,"null");
                        items.add(item);
                        map.put(fromUID,true);
                    }else{
                        continue;
                    }
                }else{
                    if (!map.containsKey(toUID)){
                        String latestContent=cursor.getString(cursor.getColumnIndex("content"));
                        long time=cursor.getLong(cursor.getColumnIndex("create_time"));
                        MessageItem item=new MessageItem(toUID,latestContent,toUID,time,"null");
                        items.add(item);
                        map.put(toUID,true);
                    }else{
                        continue;
                    }
                }
            }while (cursor.moveToNext());
        }
        list.clear();
        list.addAll(items);
        adapter.notifyDataSetChanged();

        helper=new SqliteHelper(this,"local_users",null,1);
        db=helper.getWritableDatabase();
        for (int i=0;i<list.size();i++){
            MessageItem item=list.get(i);
            String uid=item.getHisID();
            Cursor info=db.query("local_users",null,"uid=?",new String[]{uid},null,null,null);
            if (info.moveToFirst()){
                String name=info.getString(info.getColumnIndex("nickname"));
                String portrait=info.getString(info.getColumnIndex("portrait_url"));
                item.setHisName(name);
                item.setPortraitUrl(portrait);
                adapter.notifyItemChanged(i);
            }else{
                fetchUsersInfo(item.getHisID());
            }
        }
    }
    private void fetchUsersInfo(final String hisUID){
        String myUID=((MainApplication)getApplicationContext()).getUID();
        HttpHelper.fetchUserInformation(myUID, hisUID, this, new HttpHelper.OnFetchUserInformation() {
            @Override
            public void onReturnSuccess(UserInformationBean bean) {
                LocalUsersUpdate.InsertOrUpdate(MessageActivity.this,hisUID,bean.getNickname(),bean.getPortraitUrl());
                sendMessage(FETCH_SUCCESS,hisUID);
            }
            @Override
            public void onReturnFail() {
                sendMessage(CONNECTION_FAILED,hisUID);
            }
        });
    }
    private void updateNameAndPortrait(String uid){
        SqliteHelper helper=new SqliteHelper(this,"local_users",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("local_users",null,"uid=?",new String[]{uid},null,null,null);
        if (cursor.moveToFirst()){
            String name=cursor.getString(cursor.getColumnIndex("nickname"));
            String url=cursor.getString(cursor.getColumnIndex("portrait_url"));
            for (int i=0;i<list.size();i++){
                if (list.get(i).getHisID().equals(uid)){
                    list.get(i).setHisName(name);
                    list.get(i).setPortraitUrl(url);
                    adapter.notifyItemChanged(i);
                }
            }
        }
    }
    private void sendMessage(int what,String uid){
        Message msg=myHandler.obtainMessage();
        msg.what=what;
        msg.obj=uid;
        myHandler.sendMessage(msg);
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CONNECTION_FAILED:
                    break;
                case FETCH_SUCCESS:
                    String uid=(String)msg.obj;
                    updateNameAndPortrait(uid);
                    break;
            }
        }
    }
    @Override
    protected void onDestroy() {
        if (connection!=null){
            unbindService(connection);
        }
        if (receiver!=null){
            unregisterReceiver(receiver);
        }
        super.onDestroy();
    }
}
