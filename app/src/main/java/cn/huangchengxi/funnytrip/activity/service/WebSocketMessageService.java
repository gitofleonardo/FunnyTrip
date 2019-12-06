package cn.huangchengxi.funnytrip.activity.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketMessageService extends Service {
    private WebSocketClientBinder binder=new WebSocketClientBinder();
    private WebSocket webSocket;
    private boolean isAbleToSendMessage=false;
    private String uid;
    private String password;
    private SqliteHelper helper;
    private SQLiteDatabase db;
    private Thread heartBeatThread;
    private final String heartBeat="{" +
            "\"type\":\"heart_beat\"" +
            "}";

    private final int SEND_HEART_BEAT=0;
    private boolean onCloseAutoConnect=true;

    private MyHandler myHandler=new MyHandler();

    public WebSocketMessageService() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    public class WebSocketClientBinder extends Binder implements MessageInterface{
        public WebSocketMessageService getService(){
            return WebSocketMessageService.this;
        }
        @Override
        public void sendMessage(String message,OnMessageCallback callback) {
            WebSocketMessageService.this.sendMessage(message,callback);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        initDatabase();
        return this.binder;
    }
    private void initDatabase(){
        helper=new SqliteHelper(this,"messages",null,1);
        db=helper.getWritableDatabase();
    }
    public void requestConnection(){
        if (webSocket!=null){
            webSocket.close(1000,null);
        }
        if (heartBeatThread!=null){
            heartBeatThread.interrupt();
            heartBeatThread=null;
        }
        HttpHelper.connectToMessagePusher(new WebSocketListener() {
            @Override
            public void onOpen(@NotNull final WebSocket webSocket, @NotNull Response response) {
                super.onOpen(webSocket, response);
                WebSocketMessageService.this.webSocket=webSocket;
                sendValidate();
                heartBeatThread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            while (!Thread.currentThread().isInterrupted()){
                                Log.e("interrupt",webSocket.toString());
                                Thread.sleep(5000);
                                Message msg=myHandler.obtainMessage();
                                msg.what=SEND_HEART_BEAT;
                                myHandler.sendMessage(msg);
                            }
                        }catch (Exception e){
                            Log.e("interrupt",e.toString());
                        }
                    }
                });
                heartBeatThread.start();
            }
            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                super.onMessage(webSocket, text);
                Log.e("onMessageReceived",text);
                try{
                    JSONObject json=new JSONObject(text);
                    String type=json.getString("type");
                    if (type.equals("validate_success")){
                        isAbleToSendMessage=true;
                        fetchUnread();
                    }else if (type.equals("validate_failed")){
                        isAbleToSendMessage=false;
                    }else if (type.equals("request_friend")){
                        Toast.makeText(WebSocketMessageService.this, "收到好友请求", Toast.LENGTH_SHORT).show();
                    }else if (type.equals("message")){
                        insertIfNotExisted(json);
                    }else if (type.equals("return_message")){
                        updateLocalMessage(json);
                    }
                    else{
                        Toast.makeText(WebSocketMessageService.this, "收到一条消息:"+text, Toast.LENGTH_SHORT).show();
                        Log.e("message",text);
                    }
                }catch (Exception e){
                    Log.e("text received",text);
                }
            }
            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosed(webSocket, code, reason);
                isAbleToSendMessage=false;
                WebSocketMessageService.this.webSocket=null;
                if (onCloseAutoConnect){
                    requestConnection();
                }
            }
            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                isAbleToSendMessage=false;
            }
        });
    }
    public void sendMessage(String jsonString,OnMessageCallback callback){
        if (webSocket!=null && isAbleToSendMessage){
            webSocket.send(jsonString);
            if (callback!=null){
                callback.onSuccess();
            }
        }else{
            
        }
    }
    public void sendAddFriendMessage(String jsonString,OnMessageCallback callback){
        Log.e("invoke send message","invoke");
        if (webSocket!=null && isAbleToSendMessage){
            webSocket.send(jsonString);
            Log.e("invoke send message","success");
            if (callback!=null){
                callback.onSuccess();
            }
        }else{

        }
    }
    public void sendValidate(){
        uid=((MainApplication)getApplicationContext()).getUID();
        password=((MainApplication)getApplicationContext()).getCurrentPassword();
        String validate="{" +
                "\"type\":\"validate\"," +
                "\"uid\":\""+uid+"\"," +
                "\"password\":\""+password+"\"" +
                "}";
        webSocket.send(validate);
    }
    public interface MessageInterface{
        void sendMessage(String message,OnMessageCallback callback);
    }
    public interface OnMessageCallback{
        void onError();
        void onSuccess();
    }
    public void fetchUnread(){
        //send this after right after connection
        String fetchUnread="{" +
                "\"type\":\"fetch_unread\"," +
                "\"uid\":\":"+uid+"\"" +
                "}";
        webSocket.send(fetchUnread);
    }
    private void updateLocalMessage(JSONObject json){
        try {
            String tmpId=json.getString("message_id");
            long time=json.getLong("time");
            SqliteHelper helper=new SqliteHelper(this,"messages",null,1);
            SQLiteDatabase db=helper.getWritableDatabase();
            db.execSQL("update messages set create_time="+time+",sent=true where message_id=\""+tmpId+"\"");
        }catch (Exception e){
            Log.e("return message",e.toString());
        }
    }
    public boolean insertIfNotExisted(JSONObject json){
        try{
            String fromUID=json.getString("from_uid");
            String to_uid=json.getString("to_uid");
            String content=json.getString("content");
            long time=json.getLong("time");
            String messageID=json.getString("message_id");
            SqliteHelper helper=new SqliteHelper(WebSocketMessageService.this,"messages",null,1);
            SQLiteDatabase db=helper.getWritableDatabase();
            Cursor cursor=db.query("messages",null,"from_uid=? and to_uid=? and message_id=?",new String[]{fromUID,to_uid,messageID},null,null,null);
            if (cursor.moveToFirst()){
                return false;
            }
            db.execSQL("insert into messages values(\""+fromUID+"\",\""+to_uid+"\","+time+",\""+content+"\",false,\""+messageID+"\",false)");

            sendNotification(content);

            Intent intent=new Intent("cn.huangchengxi.funnytrip.MESSAGE_RECEIVER");
            intent.putExtra("content",content);
            intent.putExtra("from_uid",fromUID);
            intent.putExtra("to_uid",to_uid);
            intent.putExtra("time",time);
            intent.putExtra("fromActivity","WebSocketMessageService");
            sendBroadcast(intent);

            return true;
        }catch (Exception e){
            Log.e("insert into message",e.toString());
            return false;
        }
    }
    private void sendNotification(String message){
        Context context=getApplicationContext();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String channelID="FunnyTripChannel";
            Notification notification=new Notification.Builder(context).setChannelId(channelID).setSmallIcon(R.drawable.icon).setContentText("收到一条消息").setContentText(message).build();
            NotificationChannel channel=new NotificationChannel(channelID,"有趣的旅行", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(1,notification);
        }else{
            Notification notification=new Notification.Builder(context).setSmallIcon(R.drawable.icon).setContentText("收到一条消息").setContentText(message).build();
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(1,notification);
        }
    }

    public void setOnCloseAutoConnect(boolean onCloseAutoConnect) {
        this.onCloseAutoConnect = onCloseAutoConnect;
    }

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            try{
                switch (msg.what){
                    case SEND_HEART_BEAT:
                        webSocket.send(heartBeat);
                        break;
                }
            }catch (Exception e){}
        }
    }
}