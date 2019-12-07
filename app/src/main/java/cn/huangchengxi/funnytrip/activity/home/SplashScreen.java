package cn.huangchengxi.funnytrip.activity.home;

import androidx.annotation.NonNull;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.base.BaseAppCompatActivity;
import cn.huangchengxi.funnytrip.activity.service.WebSocketMessageService;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.item.ClockItem;
import cn.huangchengxi.funnytrip.item.NoteItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.utils.PermissionHelper;
import cn.huangchengxi.funnytrip.utils.StorageHelper;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SplashScreen extends BaseAppCompatActivity {
    private ImageView loading;
    private TextView loadingText;
    private ImageView wSplashImg;
    private final int thisRQ=0;
    private final int tmCode=0;
    private MyTimerTask myTimerTask;
    private MyHandler myHandler=new MyHandler();

    private final int LOGIN_SUCCESS=2;
    private final int LOGIN_FAILED=1;
    private String accountStr;
    private String passwordStr;

    private MainApplication application;
    private ServiceConnection connection=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);
        setStatusBarColor(this,R.color.ThemeBlue);
        initWidget();
        application=(MainApplication)getApplicationContext();
        readSetting();
        //readNotes();
        //readClocks();
        requestPermission();
    }
    private void startLogin(){
        SqliteHelper helper=new SqliteHelper(this,"users",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("users",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            String email=cursor.getString(cursor.getColumnIndex("email"));
            String password=cursor.getString(cursor.getColumnIndex("passwd"));
            accountStr=email;
            passwordStr=password;
            //try to login
            HttpHelper.sendLoginRequest(email, password, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    sendMessage(LOGIN_FAILED);
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        JSONObject json=new JSONObject(response.body().string());
                        String result=json.getString("result");
                        if (result.equals("ok")){
                            String cookie=response.header("Set-Cookie");
                            Pattern pattern=Pattern.compile("^.*JSESSIONID=(.+);.*$");
                            Matcher matcher=pattern.matcher(cookie);
                            if (matcher.find()){
                                ((MainApplication)getApplicationContext()).setJSESSIONID(matcher.group(1));
                            }
                            String uid=json.getString("uid");
                            ((MainApplication)getApplicationContext()).setUID(uid);
                            sendMessage(LOGIN_SUCCESS);
                        }else{
                            sendMessage(LOGIN_FAILED);
                        }
                    }catch (Exception e){
                        Log.e("exception",e.toString());
                        sendMessage(LOGIN_FAILED);
                    }
                }
            });
        }else{
            startActivity(new Intent(SplashScreen.this,LoginActivity.class));
            finish();
        }

    }
    private void sendMessage(int what){
        Message msg=myHandler.obtainMessage();
        msg.what=what;
        myHandler.sendMessage(msg);
    }
    private void initWidget(){
        loading=findViewById(R.id.loading);
        loadingText=findViewById(R.id.loading_text);
        wSplashImg=findViewById(R.id.splash_img);
        //载入封面
        //Glide.with(this).load(R.raw.splash).into(wSplashImg);
        Glide.with(this).load(R.drawable.loading).into(loading);
    }
    private void requestPermission(){
        if (PermissionHelper.checkNeededPermissions(this)){
            StorageHelper.getExternalDirectory();
            startLogin();
        }else{
            PermissionHelper.requestNeededPermissions(this,thisRQ);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==thisRQ){
            if (!PermissionHelper.checkNeededPermissions(this)){
                Toast.makeText(this, getString(R.string.permissions_not_completed), Toast.LENGTH_SHORT).show();
            }
            StorageHelper.getExternalDirectory();
            startLogin();
        }
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            //super.handleMessage(msg);
            switch (msg.what){
                case tmCode:
                    startLogin();
                    break;
                case LOGIN_FAILED:
                    Toast.makeText(SplashScreen.this, "登录失败", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SplashScreen.this,LoginActivity.class));
                    finish();
                    break;
                case LOGIN_SUCCESS:
                    updateLocalUserDatabaseAndSetAppData();
                    startActivity(new Intent(SplashScreen.this,MainActivity.class));
                    finish();
                    break;
            }
        }
    }
    private void updateLocalUserDatabaseAndSetAppData(){
        SqliteHelper helper=new SqliteHelper(this,"users",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("users",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            db.execSQL("delete from users");
            db.execSQL("insert into users values(\""+accountStr+"\",\""+passwordStr+"\")");
        }else{
            db.execSQL("insert into users values(\""+accountStr+"\",\""+passwordStr+"\")");
        }
        MainApplication app=(MainApplication)getApplicationContext();
        app.setLogin(true);
        Log.e("check this set","check");
        app.setCurrentAccount(accountStr);
        app.setCurrentPassword(passwordStr);

        connection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.e("serviceConnected","message");
                ((WebSocketMessageService.WebSocketClientBinder)iBinder).getService().requestConnection();
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.e("serviceDisconnected","message");
            }
        };
        Intent intent=new Intent(SplashScreen.this,WebSocketMessageService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }
    private class MyTimerTask extends TimerTask{
        @Override
        public void run() {
            Message msg=myHandler.obtainMessage();
            msg.what=tmCode;
            myHandler.sendMessage(msg);
        }
    }
    private boolean readSetting(){
        //read weather setting
        SqliteHelper helper=new SqliteHelper(this,"weather",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("weather",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            application.setWEATHER_ID(cursor.getString(cursor.getColumnIndex("id")));
        }
        //read app settings
        helper=new SqliteHelper(this,"settings",null,1);
        db=helper.getWritableDatabase();
        Cursor cursor1=db.query("settings",null,null,null,null,null,null);
        if (!cursor1.moveToFirst()){
            db.execSQL("insert into settings values(3,3)");
        }else{
            application.setMAX_CLOCK_COUNT(cursor1.getInt(cursor1.getColumnIndex("max_clock")));
            application.setMAX_NOTE_COUNT(cursor1.getInt(cursor1.getColumnIndex("max_note")));
        }
        return true;
    }
    /*
    private void readNotes(){
        SqliteHelper helper=new SqliteHelper(this,"notes",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("notes",null,null,null,null,null,null);
        MainApplication.notes.clear();
        if (cursor.moveToFirst()){
            int i=0;
            do{
                long time=cursor.getLong(cursor.getColumnIndex("note_time"));
                String content=cursor.getString(cursor.getColumnIndex("content"));
                NoteItem item=new NoteItem(time,content);
                MainApplication.notes.add(item);
            }while(cursor.moveToNext() && ++i<application.getMAX_NOTE_COUNT());
        }
    }

     */
    /*
    private void readClocks(){
        SqliteHelper helper=new SqliteHelper(this,"clocks",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("clocks",null,null,null,null,null,null);
        MainApplication.clocks.clear();
        if (cursor.moveToFirst()){
            int i=0;
            do{
                long time=cursor.getLong(cursor.getColumnIndex("clock_time"));
                String location=cursor.getString(cursor.getColumnIndex("location"));
                MainApplication.clocks.add(new ClockItem(String.valueOf(time),location,time));
            }while(cursor.moveToNext() && ++i<application.getMAX_CLOCK_COUNT());
        }
    }

     */

    @Override
    protected void onDestroy() {
        if (connection!=null){
            unbindService(connection);
        }
        super.onDestroy();
    }
}
