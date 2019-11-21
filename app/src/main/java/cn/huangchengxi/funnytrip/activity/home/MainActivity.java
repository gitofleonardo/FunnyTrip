package cn.huangchengxi.funnytrip.activity.home;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.base.BaseAppCompatActivity;
import cn.huangchengxi.funnytrip.activity.navigation.AboutActivity;
import cn.huangchengxi.funnytrip.activity.navigation.AccountInfoActivity;
import cn.huangchengxi.funnytrip.activity.navigation.AccountSecurityActivity;
import cn.huangchengxi.funnytrip.activity.navigation.ContactActivity;
import cn.huangchengxi.funnytrip.activity.navigation.SettingActivity;
import cn.huangchengxi.funnytrip.adapter.ClockListAdapter;
import cn.huangchengxi.funnytrip.adapter.NoteAdapter;
import cn.huangchengxi.funnytrip.item.ClockItem;
import cn.huangchengxi.funnytrip.item.NoteItem;
import cn.huangchengxi.funnytrip.item.WeatherNow;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.utils.setting.ApplicationSetting;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;
import cn.huangchengxi.funnytrip.view.HomeAppView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseAppCompatActivity {
    private RecyclerView clockListView;
    private ClockListAdapter clockListAdapter;
    private Toolbar toolbar;
    private TextView everydayPhi;
    private String phi="我们遇到什么困难，也不要怕，微笑面对他。消除恐惧最好的办法就是面对恐惧，坚持才是胜利。加油！奥力给！！！";
    private NavigationView homeNavi;
    private DrawerLayout drawerLayout;
    private ImageView menuImg;
    private CoordinatorLayout homeView;
    private FrameLayout homeMenuContainer;
    private RecyclerView noteRv;
    private NoteAdapter noteAdapter;
    private HomeAppView clockView;
    private HomeAppView messageView;
    private ImageView refreshImg;
    private HomeAppView friendsView;
    private HomeAppView momentsView;
    private FrameLayout weather;
    private ImageView weather_img;
    private TextView weather_des;
    private LocationClient locationClient;
    private LocationFetcher locationFetcher;
    private String province;
    private String city;
    private String district;
    private String weatherID;
    private String weatherDes;

    private final int WEATHER_OK=0;
    private final int WEATHER_FAIL=1;

    //request code
    private final int CLOCK_RC=0;
    private final int WEATHER_RC=1;

    private MyHandler myHandler=new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        transparentStatusBar(this);
        init();
    }
    private void init(){
        clockListView=findViewById(R.id.clock_list);
        clockListAdapter=new ClockListAdapter();
        for (int i=0;i<5;i++){
            clockListAdapter.add(new ClockItem("97184","湖南省长沙市",new Date().getTime()));
        }
        clockListView.setAdapter(clockListAdapter);
        clockListView.setLayoutManager(new LinearLayoutManager(this));
        toolbar=findViewById(R.id.home_tool_bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        everydayPhi=findViewById(R.id.everyday_phi);
        everydayPhi.setText(phi);

        homeNavi=findViewById(R.id.home_navigation);
        drawerLayout=findViewById(R.id.home_drawer);
        drawerLayout.addDrawerListener(new MDrawerListener());

        menuImg=findViewById(R.id.home_drawer_menu);
        menuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(homeNavi);
            }
        });
        homeMenuContainer=findViewById(R.id.home_drawer_menu_container);
        momentsView=findViewById(R.id.home_app_share);
        momentsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ShareActivity.class));
            }
        });
        homeMenuContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(homeNavi);
            }
        });
        homeView=findViewById(R.id.home_main_view);

        noteRv=findViewById(R.id.home_note_rv);
        noteAdapter=new NoteAdapter();
        for (int i=0;i<3;i++){
            noteAdapter.add(new NoteItem(new Date().getTime(),"今天没有睡过头","1"));
        }
        noteRv.setAdapter(noteAdapter);
        noteRv.setLayoutManager(new LinearLayoutManager(this));

        clockView=findViewById(R.id.home_app_clock);
        clockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, ClockActivity.class);
                startActivityForResult(intent,CLOCK_RC);
            }
        });
        refreshImg=findViewById(R.id.home_refresh_img);
        Glide.with(this).asGif().load(R.drawable.loading).into(refreshImg);
        refreshImg.setVisibility(View.INVISIBLE);

        messageView=findViewById(R.id.home_app_message);
        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MessageActivity.class));
            }
        });
        friendsView=findViewById(R.id.home_app_friends);
        friendsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,FriendsActivity.class));
            }
        });
        homeNavi.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.account_sec:
                        startActivity(new Intent(MainActivity.this, AccountSecurityActivity.class));
                        break;
                    case R.id.account_info:
                        startActivity(new Intent(MainActivity.this, AccountInfoActivity.class));
                        break;
                    case R.id.setting:
                        startActivity(new Intent(MainActivity.this, SettingActivity.class));
                        break;
                    case R.id.about:
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        break;
                    case R.id.contact_us:
                        startActivity(new Intent(MainActivity.this, ContactActivity.class));
                        break;
                    case R.id.weather:
                        startActivityForResult(new Intent(MainActivity.this,WeatherPicker.class),WEATHER_RC);
                        break;
                }
                return true;
            }
        });
        weather=findViewById(R.id.weather);
        weather_img=findViewById(R.id.weather_img);
        weather_des=findViewById(R.id.weather_des);
        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApplicationSetting.WEATHER_ID!=null){
                    updateWeather(ApplicationSetting.WEATHER_ID);
                }else{
                    Intent intent=new Intent(MainActivity.this,WeatherPicker.class);
                    startActivityForResult(intent,WEATHER_RC);
                }
            }
        });

        locationClient=new LocationClient(this);
        locationFetcher=new LocationFetcher();
        locationClient.registerLocationListener(locationFetcher);
        //get location data
        LocationClientOption option=new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd0911");
        option.setScanSpan(0);
        option.setOpenGps(true);
        option.setIgnoreKillProcess(true);
        option.setWifiCacheTimeOut(5*60*1000);
        option.setEnableSimulateGps(false);
        option.setIsNeedAddress(true);
        locationClient.setLocOption(option);
        locationClient.start();

        fetchWeather();
    }
    private void fetchWeather(){
        if (ApplicationSetting.WEATHER_ID!=null){
            updateWeather(ApplicationSetting.WEATHER_ID);
        }
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case WEATHER_OK:
                    if (refreshImg.getVisibility()==View.VISIBLE){
                        refreshImg.setVisibility(View.INVISIBLE);
                    }
                    //handle data here
                    weather_des.setText(weatherDes);
                    break;
                case WEATHER_FAIL:
                    Toast.makeText(MainActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                    if (refreshImg.getVisibility()==View.VISIBLE){
                        refreshImg.setVisibility(View.INVISIBLE);
                    }
                    break;
            }
        }
    }
    private class LocationFetcher extends BDAbstractLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            province=bdLocation.getProvince();
            city=bdLocation.getCity();
            district=bdLocation.getDistrict();
            locationClient.stop();
        }
    }
    private class MDrawerListener implements DrawerLayout.DrawerListener{
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            float translateX=homeNavi.getMeasuredWidth()*slideOffset;
            homeView.setTranslationX(translateX);
        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {

        }
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(homeNavi)){
            drawerLayout.closeDrawer(homeNavi);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case WEATHER_RC:
                if (data!=null){
                    weatherID=data.getStringExtra("weather_id");
                    refreshImg.setVisibility(View.VISIBLE);
                    updateWeather(weatherID);
                }
                break;
        }
    }
    private void updateWeather(final String weatherID){
        HttpHelper.sendOKHttpRequest("http://guolin.tech/api/weather?cityid=" + weatherID, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message msg=myHandler.obtainMessage();
                msg.what=WEATHER_FAIL;
                myHandler.sendMessage(msg);
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseStr=response.body().string();
                if (responseStr!="" && responseStr!=null){
                    try {
                        JSONObject object=new JSONObject(responseStr);
                        JSONArray array=object.getJSONArray("HeWeather");
                        JSONObject object1=array.getJSONObject(0);
                        JSONObject now=object1.getJSONObject("now");
                        String condText=now.getString("cond_txt");
                        String tmp=now.getString("tmp");
                        weatherDes=tmp+"℃"+condText;
                        //update setting
                        WeatherNow weatherNow=new WeatherNow(tmp,condText);
                        ApplicationSetting.weatherNow=weatherNow;
                        ApplicationSetting.WEATHER_ID=weatherID;
                        //update database for weather id
                        SqliteHelper helper=new SqliteHelper(MainActivity.this,"weather",null,1);
                        SQLiteDatabase db=helper.getWritableDatabase();
                        db.execSQL("delete from weather");
                        db.execSQL("insert into weather values(\""+weatherID+"\")");
                        //update ok

                        Message msg=myHandler.obtainMessage();
                        msg.what=WEATHER_OK;
                        myHandler.sendMessage(msg);
                    }catch (Exception e){
                        Log.e("error",e.toString());
                    }
                }
            }
        });
    }
}
