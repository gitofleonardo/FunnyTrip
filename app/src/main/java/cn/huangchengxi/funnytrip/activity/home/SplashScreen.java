package cn.huangchengxi.funnytrip.activity.home;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Timer;
import java.util.TimerTask;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.base.BaseAppCompatActivity;
import cn.huangchengxi.funnytrip.utils.PermissionHelper;
import cn.huangchengxi.funnytrip.utils.setting.ApplicationSetting;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;

public class SplashScreen extends BaseAppCompatActivity {
    private Button wSkipButton;
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
            waitForSkip.schedule(myTimerTask,1000);
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
            waitForSkip.schedule(myTimerTask,1000);
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
        return true;
    }
}
