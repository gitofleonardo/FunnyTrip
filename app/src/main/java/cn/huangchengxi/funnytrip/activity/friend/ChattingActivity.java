package cn.huangchengxi.funnytrip.activity.friend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.service.WebSocketMessageService;
import cn.huangchengxi.funnytrip.adapter.ChatRVAdapter;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.broadcast.MessageReceiver;
import cn.huangchengxi.funnytrip.item.ChatMessageItem;
import cn.huangchengxi.funnytrip.item.MessageItem;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;

public class ChattingActivity extends AppCompatActivity {
    private String userId;
    private Toolbar toolbar;
    private ImageView back;
    private SwipeRefreshLayout srl;
    private RecyclerView recyclerView;
    private ChatRVAdapter adapter;
    private List<ChatMessageItem> list;
    private LinearLayoutManager manager;
    private Button sendButton;
    private EditText content;
    private TextView name;
    private WebSocketMessageService.WebSocketClientBinder binder;
    private ServiceConnection connection=null;
    private String myPortraitUrl;
    private String hisPortraitUrl;
    private MessageReceiver receiver;
    SqliteHelper helper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        init();
        initInformationFromLocal();
        initFromLocalDatabase();
        bindService();
        registerReceiver();
    }
    private void init(){
        userId=getIntent().getStringExtra("id");

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        sendButton=findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!content.getText().toString().equals("")){
                    String uid=((MainApplication)getApplicationContext()).getUID();
                    String tmpId=generateLocalTmpId();
                    String data="{" +
                            "\"type\":\"message\"," +
                            "\"from_uid\":\""+uid+"\"," +
                            "\"to_uid\":\""+userId+"\"," +
                            "\"content\":\""+content.getText().toString()+"\"," +
                            "\"message_id\":\""+tmpId+"\"" +
                            "}";
                    Intent intent=new Intent("cn.huangchengxi.funnytrip.MESSAGE_RECEIVER");
                    intent.putExtra("content",content.getText().toString());
                    intent.putExtra("from_uid",uid);
                    intent.putExtra("to_uid",userId);
                    intent.putExtra("time",new Date().getTime());
                    intent.putExtra("fromActivity","ChattingActivity");
                    sendBroadcast(intent);

                    insertTmpMessage(uid,userId,content.getText().toString(),tmpId);
                    binder.getService().sendMessage(data, new WebSocketMessageService.OnMessageCallback() {
                        @Override
                        public void onError() {
                            Toast.makeText(ChattingActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess() {
                            //Toast.makeText(ChattingActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    ChatMessageItem item=new ChatMessageItem(tmpId,content.getText().toString(),uid,myPortraitUrl,new Date().getTime(),false,false);
                    content.setText("");
                    if (list.size()>0){
                        list.add(list.size(),item);
                    }else{
                        list.add(item);
                    }
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(list.size()-1);
                }
            }
        });
        sendButton.setEnabled(false);
        content=findViewById(R.id.text);
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")){
                    sendButton.setEnabled(false);
                }else{
                    sendButton.setEnabled(true);
                }
            }
        });
        srl=findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (list.size()>0){
                    fetchMore(list.get(0).getTime());
                }else{
                    fetchMore(new Date().getTime());
                }
            }
        });
        recyclerView=findViewById(R.id.chat_rv);
        list=new ArrayList<>();
        adapter=new ChatRVAdapter(this,list);
        adapter.setOnPortraitClick(new ChatRVAdapter.OnPortraitClick() {
            @Override
            public void onClick(View view, int position) {
                ChatMessageItem item=list.get(position);
                if (item.isReceived()){
                    FriendDetailActivity.startDetailActivity(ChattingActivity.this,item.getUserId());
                }else{
                    FriendDetailActivity.startDetailActivity(ChattingActivity.this,((MainApplication)getApplicationContext()).getUID());
                }
            }
        });
        recyclerView.setAdapter(adapter);
        manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        name=findViewById(R.id.name);
        name.setText(userId);
    }
    private void initInformationFromLocal(){
        SqliteHelper helper=new SqliteHelper(this,"local_users",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("local_users",null,"uid=?",new String[]{userId},null,null,null);
        if (cursor.moveToFirst()){
            String nickname=cursor.getString(cursor.getColumnIndex("nickname"));
            String portrait=cursor.getString(cursor.getColumnIndex("portrait_url"));
            name.setText(nickname);
            this.hisPortraitUrl=portrait;
        }
    }
    private void registerReceiver(){
        receiver=new MessageReceiver();
        receiver.setOnMessageReceived(new MessageReceiver.OnMessageReceived() {
            @Override
            public void OnReveived(String messageID,String fromUID, String toUID, String content, long time,String context) {
                if (!context.equals("ChattingActivity") && fromUID.equals(userId)){
                    ChatMessageItem item=new ChatMessageItem(messageID,content,fromUID,hisPortraitUrl,time,true,true);
                    list.add(list.size(),item);
                    //adapter.notifyItemChanged(list.size()-1);
                    //adapter.notifyItemInserted(list.size()-1);
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(list.size()-1);
                }
            }
            @Override
            public void onSuccessSent(String messageID) {
                for (int i=0;i<list.size();i++){
                    if (list.get(i).getMessageId().equals(messageID)){
                        list.get(i).setSent(true);
                        adapter.notifyItemChanged(i);
                        break;
                    }
                }
            }
        });
        IntentFilter intentFilter=new IntentFilter("cn.huangchengxi.funnytrip.MESSAGE_RECEIVER");
        registerReceiver(receiver,intentFilter);
    }
    private void insertTmpMessage(String from,String to,String content,String tmpId){
        SqliteHelper helper=new SqliteHelper(this,"messages",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        long time=new Date().getTime();

        db.execSQL("insert into messages values(\""+from+"\",\""+to+"\","+time+",\""+content+"\",0,\""+tmpId+"\",0)");
        helper.close();
        db.close();
    }
    private String generateLocalTmpId(){
        return ((MainApplication)getApplicationContext()).getUID()+new Date().getTime();
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
        Intent intent=new Intent(this,WebSocketMessageService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }
    private void fetchMore(long timeLimit){
        String myUID=((MainApplication)getApplicationContext()).getUID();

        SqliteHelper helper=new SqliteHelper(this,"messages",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("messages",null,"((from_uid=? and to_uid=?) or (from_uid=? and to_uid=?)) and create_time<?",new String[]{userId,myUID,myUID,userId,String.valueOf(timeLimit)},null,null,"create_time desc","20");

        SqliteHelper helper1=new SqliteHelper(this,"local_users",null,1);
        SQLiteDatabase db1=helper1.getWritableDatabase();

        List<ChatMessageItem> items=new ArrayList<>();
        if (cursor.moveToFirst()){
            do{
                String content=cursor.getString(cursor.getColumnIndex("content"));
                long time=cursor.getLong(cursor.getColumnIndex("create_time"));
                String fromUID=cursor.getString(cursor.getColumnIndex("from_uid"));
                String messageID=cursor.getString(cursor.getColumnIndex("message_id"));

                if (fromUID.equals(myUID)){
                    Cursor cursor1=db1.query("local_users",null,"uid=?",new String[]{myUID},null,null,null);
                    String portraitUrl="";
                    if (cursor1.moveToFirst()){
                        portraitUrl=cursor1.getString(cursor1.getColumnIndex("portrait_url"));
                        myPortraitUrl=portraitUrl;
                    }
                    ChatMessageItem item=new ChatMessageItem(messageID,content,myUID,portraitUrl,time,false,true);
                    items.add(item);
                }else{
                    Cursor cursor1=db1.query("local_users",null,"uid=?",new String[]{fromUID},null,null,null);
                    String portraitUrl="";
                    String name=null;
                    if (cursor1.moveToFirst()){
                        portraitUrl=cursor1.getString(cursor1.getColumnIndex("portrait_url"));
                        name=cursor1.getString(cursor1.getColumnIndex("nickname"));
                        hisPortraitUrl=portraitUrl;
                    }
                    ChatMessageItem item=new ChatMessageItem(messageID,content,fromUID,portraitUrl,time,true,true);
                    items.add(item);
                    if (name!=null){
                        ChattingActivity.this.name.setText(name);
                    }else{
                        ChattingActivity.this.name.setText(fromUID);
                    }
                }
            }while (cursor.moveToNext());
        }
        Collections.reverse(items);
        list.addAll(0,items);
        adapter.notifyItemRangeInserted(0,items.size());
        srl.setRefreshing(false);
    }
    private void initFromLocalDatabase(){
        String myUID=((MainApplication)getApplicationContext()).getUID();

        SqliteHelper helper=new SqliteHelper(this,"messages",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("messages",null,"(from_uid=? and to_uid=?) or (from_uid=? and to_uid=?)",new String[]{userId,myUID,myUID,userId},null,null,"create_time desc","20");

        SqliteHelper helper1=new SqliteHelper(this,"local_users",null,1);
        SQLiteDatabase db1=helper1.getWritableDatabase();

        List<ChatMessageItem> items=new ArrayList<>();
        if (cursor.moveToFirst()){
            do{
                String content=cursor.getString(cursor.getColumnIndex("content"));
                long time=cursor.getLong(cursor.getColumnIndex("create_time"));
                String fromUID=cursor.getString(cursor.getColumnIndex("from_uid"));
                String messageID=cursor.getString(cursor.getColumnIndex("message_id"));
                if (fromUID.equals(myUID)){
                    Cursor cursor1=db1.query("local_users",null,"uid=?",new String[]{myUID},null,null,null);
                    String portraitUrl="";
                    if (cursor1.moveToFirst()){
                        portraitUrl=cursor1.getString(cursor1.getColumnIndex("portrait_url"));
                        myPortraitUrl=portraitUrl;
                    }
                    ChatMessageItem item=new ChatMessageItem(messageID,content,myUID,portraitUrl,time,false,true);
                    items.add(item);
                }else{
                    Cursor cursor1=db1.query("local_users",null,"uid=?",new String[]{fromUID},null,null,null);
                    String portraitUrl="";
                    if (cursor1.moveToFirst()){
                        portraitUrl=cursor1.getString(cursor1.getColumnIndex("portrait_url"));
                        hisPortraitUrl=portraitUrl;
                    }
                    ChatMessageItem item=new ChatMessageItem(messageID,content,fromUID,portraitUrl,time,true,true);
                    items.add(item);
                }
                Log.e("localMessage",fromUID+":"+content+":");
            }while (cursor.moveToNext());
        }
        list.clear();
        list.addAll(items);
        Collections.reverse(list);
        adapter.notifyDataSetChanged();
        manager.scrollToPosition(list.size()-1);
    }
    public static void startChattingActivity(Context context,String userId){
        Intent intent=new Intent(context,ChattingActivity.class);
        intent.putExtra("id",userId);
        context.startActivity(intent);
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
