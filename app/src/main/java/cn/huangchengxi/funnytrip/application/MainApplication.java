package cn.huangchengxi.funnytrip.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

import java.util.ArrayList;

import cn.huangchengxi.funnytrip.activity.home.MainActivity;
import cn.huangchengxi.funnytrip.activity.service.WebSocketMessageService;
import cn.huangchengxi.funnytrip.item.ClockItem;
import cn.huangchengxi.funnytrip.item.NoteItem;
import cn.huangchengxi.funnytrip.item.WeatherNow;

public class MainApplication extends Application {
    private boolean isLogin=false;
    private String currentAccount;
    private String currentPassword;
    private String JSESSIONID;
    private String UID;
    private String portraitUrl;

    private WeatherNow weatherNow=null;
    private String WEATHER_ID=null;
    //default value
    private int MAX_NOTE_COUNT=3;
    private int MAX_CLOCK_COUNT=5;

    public static final ArrayList<NoteItem> notes=new ArrayList<>();
    public static final ArrayList<ClockItem> clocks=new ArrayList<>();

    private ServiceConnection serviceConnection;
    private WebSocketMessageService.MessageInterface messageInterface=null;

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL);
        bindService();
    }

    //bind service and start message listener
    private void bindService(){
        serviceConnection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                //messageInterface=(WebSocketMessageService.MessageInterface)iBinder;
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        Intent intent=new Intent(MainApplication.this,WebSocketMessageService.class);
        bindService(intent,serviceConnection,BIND_AUTO_CREATE);
    }
    public WeatherNow getWeatherNow() {
        return weatherNow;
    }

    public int getMAX_CLOCK_COUNT() {
        return MAX_CLOCK_COUNT;
    }

    public int getMAX_NOTE_COUNT() {
        return MAX_NOTE_COUNT;
    }

    public String getWEATHER_ID() {
        return WEATHER_ID;
    }

    public void setMAX_CLOCK_COUNT(int MAX_CLOCK_COUNT) {
        this.MAX_CLOCK_COUNT = MAX_CLOCK_COUNT;
    }

    public void setMAX_NOTE_COUNT(int MAX_NOTE_COUNT) {
        this.MAX_NOTE_COUNT = MAX_NOTE_COUNT;
    }

    public void setWEATHER_ID(String WEATHER_ID) {
        this.WEATHER_ID = WEATHER_ID;
    }

    public void setWeatherNow(WeatherNow weatherNow) {
        this.weatherNow = weatherNow;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public String getCurrentAccount() {
        return currentAccount;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getJSESSIONID() {
        return this.JSESSIONID;
    }

    public void setCurrentAccount(String currentAccount) {
        this.currentAccount = currentAccount;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public void setPortraitUrl(String portraitUrl) {
        this.portraitUrl = portraitUrl;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }

    public void setJSESSIONID(String JSESSIONID) {
        this.JSESSIONID = JSESSIONID;
    }
}
