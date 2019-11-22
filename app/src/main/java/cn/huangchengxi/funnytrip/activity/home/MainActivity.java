package cn.huangchengxi.funnytrip.activity.home;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.base.BaseAppCompatActivity;
import cn.huangchengxi.funnytrip.activity.clock.BottomClocksCallback;
import cn.huangchengxi.funnytrip.activity.navigation.AboutActivity;
import cn.huangchengxi.funnytrip.activity.navigation.AccountInfoActivity;
import cn.huangchengxi.funnytrip.activity.navigation.AccountSecurityActivity;
import cn.huangchengxi.funnytrip.activity.navigation.ContactActivity;
import cn.huangchengxi.funnytrip.activity.navigation.SettingActivity;
import cn.huangchengxi.funnytrip.activity.note.MyNoteActivity;
import cn.huangchengxi.funnytrip.adapter.ClockListAdapter;
import cn.huangchengxi.funnytrip.adapter.NoteAdapter;
import cn.huangchengxi.funnytrip.item.ClockItem;
import cn.huangchengxi.funnytrip.item.NoteItem;
import cn.huangchengxi.funnytrip.item.WeatherNow;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.utils.setting.ApplicationSetting;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;
import cn.huangchengxi.funnytrip.view.HomeAppView;
import cn.huangchengxi.funnytrip.viewholder.ClockListHolder;
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
    private HomeAppView routeView;
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
    private FrameLayout noteWrite;
    private CardView myNote;
    private FrameLayout showAllClocks;
    private HomeAppView tipsView;

    private final int WEATHER_OK=0;
    private final int WEATHER_FAIL=1;

    //request code
    private final int CLOCK_RC=0;
    private final int WEATHER_RC=1;
    private final int NOTE_RC=2;
    private final int SETTINGS_RC=3;

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
        clockListAdapter=new ClockListAdapter(false);
        for (int i=0;i<ApplicationSetting.clocks.size();i++){
            clockListAdapter.add(ApplicationSetting.clocks.get(i));
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
        for (int i=0;i<ApplicationSetting.notes.size();i++){
            noteAdapter.add(ApplicationSetting.notes.get(i));
        }
        noteAdapter.setOnNoteClickListener(new NoteAdapter.OnNoteClickListener() {
            @Override
            public void onClick(View view, NoteItem item) {
                NoteActivity.startNoteActivityForResult(MainActivity.this,NOTE_RC,item);
            }
        });
        noteWrite=findViewById(R.id.write_note);
        noteWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteActivity.startNoteActivityForResult(MainActivity.this,NOTE_RC,null);
            }
        });
        myNote=findViewById(R.id.no_note_to_show);
        myNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, MyNoteActivity.class),NOTE_RC);
            }
        });
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
                        startActivityForResult(new Intent(MainActivity.this, SettingActivity.class),SETTINGS_RC);
                        break;
                    case R.id.about:
                        startActivityForResult(new Intent(MainActivity.this, AboutActivity.class),SETTINGS_RC);
                        break;
                    case R.id.contact_us:
                        //startActivity(new Intent(MainActivity.this, ContactActivity.class));
                        try {
                            String url = "mqqwpa://im/chat?chat_type=wpa&uin=971840889";
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "调起QQ失败", Toast.LENGTH_SHORT).show();
                        }
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
        tipsView=findViewById(R.id.home_app_tips);
        tipsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,TipsActivity.class));
            }
        });
        routeView=findViewById(R.id.home_app_route);
        routeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RouteActivity.class));
            }
        });
        showAllClocks=findViewById(R.id.show_more_clock);
        showAllClocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClocksBottomSheet();
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
    private void showClocksBottomSheet(){
        final View view=View.inflate(this,R.layout.view_my_clock_all,null);
        RecyclerView rv=view.findViewById(R.id.bottom_clock_rv);
        rv.setItemAnimator(new DefaultItemAnimator());
        final ClockListAdapter cla=new ClockListAdapter(true);
        ItemTouchHelper.Callback callback=new BottomClocksCallback(cla);
        final ItemTouchHelper helper=new ItemTouchHelper(callback);
        cla.setOnSwipeListener(new ClockListAdapter.OnSwipeListener() {
            @Override
            public void onSwipe(ClockListHolder holder) {
                helper.startSwipe(holder);
            }
        });
        helper.attachToRecyclerView(rv);
        cla.setOnItemDelete(new ClockListAdapter.OnItemDelete() {
            @Override
            public void onDelete(String clockId) {
                SqliteHelper helper=new SqliteHelper(MainActivity.this,"clocks",null,1);
                SQLiteDatabase db=helper.getWritableDatabase();
                db.execSQL("delete from clocks where clock_time="+clockId);
                updateClockList();
            }
        });
        rv.setAdapter(cla);
        rv.setLayoutManager(new LinearLayoutManager(this));
        final BottomSheetDialog bsd=new BottomSheetDialog(this);
        bsd.setContentView(view);
        ImageView back=view.findViewById(R.id.collapse);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsd.dismiss();
            }
        });
        //load data
        SqliteHelper dbhelper=new SqliteHelper(MainActivity.this,"clocks",null,1);
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        Cursor cursor=db.query("clocks",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do{
                long time=cursor.getLong(cursor.getColumnIndex("clock_time"));
                String location=cursor.getString(cursor.getColumnIndex("location"));
                ClockItem item=new ClockItem(String.valueOf(time),location,time);
                cla.add(item);
            }while(cursor.moveToNext());
        }
        bsd.show();
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
            case NOTE_RC:
                updateNoteList();
                break;
            case CLOCK_RC:
                updateClockList();
                break;
            case SETTINGS_RC:
                updateNoteList();
                updateClockList();
                break;
        }
    }
    private void updateClockList(){
        SqliteHelper helper=new SqliteHelper(this,"clocks",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("clocks",null,null,null,null,null,null);
        ApplicationSetting.clocks.clear();
        clockListAdapter.clear();
        if (cursor.moveToFirst()){
            int i=0;
            do{
                long time=cursor.getLong(cursor.getColumnIndex("clock_time"));
                String location=cursor.getString(cursor.getColumnIndex("location"));
                ClockItem item=new ClockItem(String.valueOf(time),location,time);
                ApplicationSetting.clocks.add(item);
                clockListAdapter.add(item);
            }while(cursor.moveToNext() && ++i<ApplicationSetting.MAX_CLOCK_COUNT);
        }
    }
    private void updateNoteList(){
        SqliteHelper helper=new SqliteHelper(this,"notes",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("notes",null,null,null,null,null,null);
        ApplicationSetting.notes.clear();
        noteAdapter.clear();
        if (cursor.moveToFirst()){
            int i=0;
            do{
                long time=cursor.getLong(cursor.getColumnIndex("note_time"));
                String content=cursor.getString(cursor.getColumnIndex("content"));
                NoteItem item=new NoteItem(time,content);
                ApplicationSetting.notes.add(item);
                noteAdapter.add(item);
            }while(cursor.moveToNext() && ++i<ApplicationSetting.MAX_NOTE_COUNT);
        }
    }
    private void updateWeather(final String weatherID){
        HttpHelper.sendOKHttpRequest("https://api.heweather.net/s6/weather/now?location="+weatherID+"&key=557905c76c73431882f6823b7e2336d8" + weatherID, new Callback() {
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
