package cn.huangchengxi.funnytrip.activity.home;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Timer;
import java.util.TimerTask;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.base.BaseAppCompatActivity;
import cn.huangchengxi.funnytrip.item.ClockItem;
import cn.huangchengxi.funnytrip.item.NoteItem;
import cn.huangchengxi.funnytrip.utils.PermissionHelper;
import cn.huangchengxi.funnytrip.utils.setting.ApplicationSetting;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;

public class SplashScreen extends BaseAppCompatActivity {
    private FrameLayout wSkipButton;
    private ImageView wSplashImg;
    private Timer waitForSkip;
    private final int thisRQ=0;
    private final int tmCode=0;
    private MyTimerTask myTimerTask;
    private MyHandler myHandler=new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);
        setStatusBarColor(this,R.color.ThemeBlue);
        initWidget();
        readSetting();
        readNotes();
        readClocks();
        requestPermission();
    }
    private void initWidget(){
        wSkipButton=findViewById(R.id.splash_skip_bt);
        wSplashImg=findViewById(R.id.splash_img);
        //载入封面
        Glide.with(this).load(R.raw.splash).into(wSplashImg);

        wSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (waitForSkip!=null){
                    waitForSkip.cancel();
                }
                Intent intent=new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void requestPermission(){
        if (PermissionHelper.checkNeededPermissions(this)){
            waitForSkip=new Timer();
            myTimerTask=new MyTimerTask();
            waitForSkip.schedule(myTimerTask,100);
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
            if (waitForSkip!=null){
                waitForSkip.cancel();
            }
            myTimerTask=new MyTimerTask();
            waitForSkip.schedule(myTimerTask,100);
        }
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            //super.handleMessage(msg);
            switch (msg.what){
                case tmCode:
                    Intent intent=new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
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
            ApplicationSetting.WEATHER_ID=cursor.getString(cursor.getColumnIndex("id"));
        }
        //read app settings
        helper=new SqliteHelper(this,"settings",null,1);
        db=helper.getWritableDatabase();
        Cursor cursor1=db.query("settings",null,null,null,null,null,null);
        if (!cursor1.moveToFirst()){
            db.execSQL("insert into settings values(3,3)");
        }else{
            ApplicationSetting.MAX_CLOCK_COUNT=cursor1.getInt(cursor1.getColumnIndex("max_clock"));
            ApplicationSetting.MAX_NOTE_COUNT=cursor1.getInt(cursor1.getColumnIndex("max_note"));
        }
        return true;
    }
    private void readNotes(){
        SqliteHelper helper=new SqliteHelper(this,"notes",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("notes",null,null,null,null,null,null);
        ApplicationSetting.notes.clear();
        if (cursor.moveToFirst()){
            int i=0;
            do{
                long time=cursor.getLong(cursor.getColumnIndex("note_time"));
                String content=cursor.getString(cursor.getColumnIndex("content"));
                NoteItem item=new NoteItem(time,content);
                ApplicationSetting.notes.add(item);
            }while(cursor.moveToNext() && ++i<ApplicationSetting.MAX_NOTE_COUNT);
        }
    }
    private void readClocks(){
        SqliteHelper helper=new SqliteHelper(this,"clocks",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("clocks",null,null,null,null,null,null);
        ApplicationSetting.clocks.clear();
        if (cursor.moveToFirst()){
            int i=0;
            do{
                long time=cursor.getLong(cursor.getColumnIndex("clock_time"));
                String location=cursor.getString(cursor.getColumnIndex("location"));
                ApplicationSetting.clocks.add(new ClockItem(String.valueOf(time),location,time));
            }while(cursor.moveToNext() && ++i<ApplicationSetting.MAX_CLOCK_COUNT);
        }
    }
}
