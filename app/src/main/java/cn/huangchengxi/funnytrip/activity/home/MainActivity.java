package cn.huangchengxi.funnytrip.activity.home;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import androidx.appcompat.app.AlertDialog;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.account.ChangePasswordActivity;
import cn.huangchengxi.funnytrip.activity.friend.FriendDetailActivity;
import cn.huangchengxi.funnytrip.activity.service.WebSocketMessageService;
import cn.huangchengxi.funnytrip.activity.weather.WeatherActivity;
import cn.huangchengxi.funnytrip.activity.base.BaseAppCompatActivity;
import cn.huangchengxi.funnytrip.activity.clock.BottomClocksCallback;
import cn.huangchengxi.funnytrip.activity.navigation.AboutActivity;
import cn.huangchengxi.funnytrip.activity.navigation.AccountInfoActivity;
import cn.huangchengxi.funnytrip.activity.navigation.AccountSecurityActivity;
import cn.huangchengxi.funnytrip.activity.navigation.SettingActivity;
import cn.huangchengxi.funnytrip.activity.note.MyNoteActivity;
import cn.huangchengxi.funnytrip.adapter.ClockListAdapter;
import cn.huangchengxi.funnytrip.adapter.NoteAdapter;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.broadcast.MessageSocketStateReceiver;
import cn.huangchengxi.funnytrip.databean.ClocksResultBean;
import cn.huangchengxi.funnytrip.databean.NotesResultBean;
import cn.huangchengxi.funnytrip.databean.UserInformationBean;
import cn.huangchengxi.funnytrip.handler.AppHandler;
import cn.huangchengxi.funnytrip.item.ClockItem;
import cn.huangchengxi.funnytrip.item.NoteItem;
import cn.huangchengxi.funnytrip.item.WeatherNow;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;
import cn.huangchengxi.funnytrip.view.HomeAppView;
import cn.huangchengxi.funnytrip.viewholder.ClockListHolder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseAppCompatActivity implements AppHandler.OnHandleMessage {
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
    private HomeAppView weatherView;
    private HomeAppView teamView;
    private ImageView navPortrait;
    private TextView userName;
    private FrameLayout networkState;
    private MessageSocketStateReceiver stateReceiver;
    private ServiceConnection messageServiceConnection;
    private WebSocketMessageService messageService;

    private final int WEATHER_OK=0;
    private final int WEATHER_FAIL=1;

    //request code
    private final int CLOCK_RC=0;
    private final int WEATHER_RC=1;
    private final int NOTE_RC=2;
    private final int SETTINGS_RC=3;
    private final int LOGIN_RC=4;
    private final int CHANGE_PASSWORD_RC=5;
    private final int ACCOUNT_SEC_RC=6;

    private final int FETCH_MY_INFORMATION_FAILED=7;
    private final int FETCH_MY_INFORMATION_SUCCESS=8;
    private final int CONNECTION_ERROR=9;
    private final int NOT_LOGIN=10;

    private final int FETCH_CLOCKS_SUCCESS=11;
    private final int FETCH_CLOCKS_FOR_BOTTOM_SHEET_SUCCESS=12;
    private final int CLOCK_DELETE_FAILED=13;
    private final int CLOCK_DELETE_SUCCESS=14;
    private final int FETCH_NOTES_SUCCESS=15;
    private final int RECONNECT=16;
    private final int ACCOUNT_INFO_CHANGE=17;

    private String nickname;
    private String portrait_url;

    private MainApplication app;

    //private MyHandler myHandler=new MyHandler();
    private AppHandler myHandler=new AppHandler(this);
    private ClockListAdapter cla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        transparentStatusBar(this);
        app=(MainApplication)getApplicationContext();
        init();
    }
    private void init(){
        networkState=findViewById(R.id.network_state);
        networkState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (messageService!=null){
                    messageService.requestConnection();
                }
            }
        });
        clockListView=findViewById(R.id.clock_list);
        clockListAdapter=new ClockListAdapter(false);
        for (int i=0;i<MainApplication.clocks.size();i++){
            clockListAdapter.add(MainApplication.clocks.get(i));
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
        for (int i=0;i<MainApplication.notes.size();i++){
            noteAdapter.add(MainApplication.notes.get(i));
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
                        startActivityForResult(new Intent(MainActivity.this, AccountSecurityActivity.class),ACCOUNT_SEC_RC);
                        break;
                    case R.id.account_info:
                        startActivityForResult(new Intent(MainActivity.this, AccountInfoActivity.class),ACCOUNT_INFO_CHANGE);
                        break;
                    case R.id.setting:
                        startActivityForResult(new Intent(MainActivity.this, SettingActivity.class),SETTINGS_RC);
                        break;
                    case R.id.about:
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
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
                    case R.id.logout:
                        new AlertDialog.Builder(MainActivity.this).setTitle("确定注销").setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                unregisterReceiver(stateReceiver);
                                stateReceiver=null;
                                messageService.closeConnection();
                                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                                finish();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
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
                if (app.getWEATHER_ID()!=null){
                    updateWeather(app.getWEATHER_ID());
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
        weatherView=findViewById(R.id.home_app_weather);
        weatherView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WeatherActivity.class));
            }
        });

        showAllClocks=findViewById(R.id.show_more_clock);
        showAllClocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClocksBottomSheet();
            }
        });
        teamView=findViewById(R.id.home_app_team);
        teamView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,TeamActivity.class));
            }
        });
        navPortrait=homeNavi.getHeaderView(0).findViewById(R.id.nav_portrait);
        navPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.e("logined",app.getJSESSIONID());
                if (app.isLogin()){
                    FriendDetailActivity.startDetailActivity(MainActivity.this,app.getUID());
                }else{
                    startActivityForResult(new Intent(MainActivity.this,LoginActivity.class),LOGIN_RC);
                }
            }
        });
        userName=homeNavi.getHeaderView(0).findViewById(R.id.home_nav_user_name);
        userName.setText(((MainApplication)getApplicationContext()).getUID());
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

        bindMessageService();
        registerStateReceiver();
        fetchWeather();
        fetchMyInformation();
        fetchClocks(new Date().getTime(),true);
        fetchNotes(new Date().getTime());
    }
    private void bindMessageService(){
        Intent intent=new Intent(this,WebSocketMessageService.class);
        messageServiceConnection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                messageService=((WebSocketMessageService.WebSocketClientBinder)iBinder).getService();
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        bindService(intent,messageServiceConnection,BIND_AUTO_CREATE);
    }
    private void registerStateReceiver(){
        stateReceiver=new MessageSocketStateReceiver();
        stateReceiver.setOnStateChangeListener(new MessageSocketStateReceiver.OnStateChangeListener() {
            @Override
            public void onStateChange(int newState) {
                if (newState==MessageSocketStateReceiver.STATE_CONNECTED){
                    networkState.setVisibility(View.GONE);
                }else if (newState==MessageSocketStateReceiver.STATE_DISCONNECTED){
                    networkState.setVisibility(View.VISIBLE);
                    /*
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            sendMessage(RECONNECT);
                        }
                    },5000,1);
                     */
                }
            }
        });
        IntentFilter intentFilter=new IntentFilter("cn.huangchengxi.funnytrip.ON_STATE_CHANGE");
        registerReceiver(stateReceiver,intentFilter);
    }
    private void showClocksBottomSheet(){
        final View view=View.inflate(this,R.layout.view_my_clock_all,null);
        RecyclerView rv=view.findViewById(R.id.bottom_clock_rv);
        rv.setItemAnimator(new DefaultItemAnimator());
        cla=new ClockListAdapter(true);
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
                deleteClock(clockId);
            }
        });
        rv.setAdapter(cla);
        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState==RecyclerView.SCROLL_STATE_IDLE){
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition()==cla.getItemCount()-1){
                        fetchClocksForBottomSheet(cla.getItemCount()==0?new Date().getTime():cla.getClockItem(cla.getItemCount()-1).getTime(),false);
                    }
                }
            }
        });
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
        bsd.getBehavior().setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset >= 0) {
                    float scale=1.0f-slideOffset*0.1f;
                    homeView.setScaleX(scale);
                    homeView.setScaleY(scale);
                }else if (slideOffset==-1.0f){
                    bsd.dismiss();
                }
            }
        });
        bsd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                homeView.setScaleX(1);
                homeView.setScaleY(1);
            }
        });
        bsd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                homeView.setScaleX(1);
                homeView.setScaleY(1);
            }
        });
        bsd.show();
        fetchClocksForBottomSheet(new Date().getTime(),true);
    }
    private void deleteClock(String id){
        String uid=((MainApplication)getApplicationContext()).getUID();
        HttpHelper.deleteClock(uid, id, this, new HttpHelper.OnCommonResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CLOCK_DELETE_FAILED);
            }
            @Override
            public void onReturnSuccess() {
                sendMessage(CLOCK_DELETE_SUCCESS);
            }
        });
    }
    private void fetchClocksForBottomSheet(long timeLimit,final boolean removed){
        String uid=((MainApplication)getApplicationContext()).getUID();
        HttpHelper.fetchClocks(uid, timeLimit, this, new HttpHelper.OnClocksResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_ERROR);
            }
            @Override
            public void onReturnSuccess(ClocksResultBean bean) {
                Message m=myHandler.obtainMessage();
                m.what=FETCH_CLOCKS_FOR_BOTTOM_SHEET_SUCCESS;
                m.obj=bean;
                m.arg1=removed?1:0;
                myHandler.sendMessage(m);
            }
        });
    }
    private void fetchClocks(long timeLimit, final boolean removed){
        String uid=((MainApplication)getApplicationContext()).getUID();
        HttpHelper.fetchClocks(uid, timeLimit, this, new HttpHelper.OnClocksResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_ERROR);
            }
            @Override
            public void onReturnSuccess(ClocksResultBean bean) {
                Message m=myHandler.obtainMessage();
                m.what=FETCH_CLOCKS_SUCCESS;
                m.obj=bean;
                m.arg1=removed?1:0;
                myHandler.sendMessage(m);
            }
        });
    }
    private void fetchWeather(){
        if (app.getWEATHER_ID()!=null){
            updateWeather(app.getWEATHER_ID());
        }
    }
    private void fetchMyInformation(){
        String uid=((MainApplication)getApplicationContext()).getUID();
        HttpHelper.fetchMyInformation(uid, this, new HttpHelper.OnFetchUserInformation() {
            @Override
            public void onReturnSuccess(UserInformationBean bean) {
                portrait_url=bean.getPortraitUrl();
                nickname=bean.getNickname();
                ((MainApplication)getApplicationContext()).setPortraitUrl(portrait_url);
                sendMessage(FETCH_MY_INFORMATION_SUCCESS);
            }
            @Override
            public void onReturnFail() {
                sendMessage(CONNECTION_ERROR);
            }
        });
    }
    private void fetchNotes(long timeLimit){
        HttpHelper.fetchNotes(timeLimit, this, new HttpHelper.OnNotesResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_ERROR);
            }
            @Override
            public void onReturnSuccess(NotesResultBean bean) {
                Message msg=myHandler.obtainMessage();
                msg.what=FETCH_NOTES_SUCCESS;
                msg.obj=bean;
                myHandler.sendMessage(msg);
            }
        });
    }
    private void sendMessage(int what){
        Message msg=myHandler.obtainMessage();
        msg.what=what;
        myHandler.sendMessage(msg);
    }

    @Override
    public void onHandle(Message msg) {
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
            case FETCH_MY_INFORMATION_FAILED:
                Toast.makeText(MainActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                break;
            case FETCH_MY_INFORMATION_SUCCESS:
                if (portrait_url!=null && !portrait_url.equals("") && !portrait_url.equals("null")){
                    Glide.with(MainActivity.this).load(HttpHelper.PIC_SERVER_HOST+portrait_url).into(navPortrait);
                }
                if (nickname!=null && !nickname.equals("null") && !nickname.equals("")){
                    userName.setText(nickname);
                }
                break;
            case FETCH_CLOCKS_SUCCESS:
                List<ClockItem> list=((ClocksResultBean)msg.obj).getList();
                boolean removed=msg.arg1==1?true:false;
                if (removed){
                    clockListAdapter.clear();
                }
                for (int i=0;i<list.size() && i<((MainApplication)getApplicationContext()).getMAX_CLOCK_COUNT();i++){
                    clockListAdapter.add(list.get(i));
                }
                break;
            case FETCH_CLOCKS_FOR_BOTTOM_SHEET_SUCCESS:
                if (cla!=null){
                    List<ClockItem> lists=((ClocksResultBean)msg.obj).getList();
                    boolean remove=msg.arg1==1?true:false;
                    if (remove){
                        cla.clear();
                    }
                    for (int i=0;i<lists.size();i++){
                        cla.add(lists.get(i));
                    }
                }
                break;
            case CLOCK_DELETE_FAILED:
                Toast.makeText(MainActivity.this    , "删除失败", Toast.LENGTH_SHORT).show();
                fetchClocksForBottomSheet(new Date().getTime(),true);
                break;
            case CLOCK_DELETE_SUCCESS:
                fetchClocks(new Date().getTime(),true);
                break;
            case FETCH_NOTES_SUCCESS:
                NotesResultBean bean=(NotesResultBean)msg.obj;
                noteAdapter.clear();
                for (int i=0;i<bean.getList().size() && i<((MainApplication)getApplicationContext()).getMAX_NOTE_COUNT();i++){
                    noteAdapter.add(bean.getList().get(i));
                }
                break;
            case RECONNECT:
                //messageService.requestConnection();
                break;
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
    protected void onDestroy() {
        if (stateReceiver!=null){
            unregisterReceiver(stateReceiver);
        }
        if (messageServiceConnection!=null){
            unbindService(messageServiceConnection);
        }
        super.onDestroy();
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
                fetchNotes(new Date().getTime());
                break;
            case CLOCK_RC:
                //updateClockList();
                fetchClocks(new Date().getTime(),true);
                break;
            case SETTINGS_RC:
                fetchNotes(new Date().getTime());
                fetchClocks(new Date().getTime(),true);
                //updateClockList();
                break;
            case LOGIN_RC:
                //do update user info process
                break;
            case ACCOUNT_SEC_RC:
                Log.e("result",resultCode+"  "+ChangePasswordActivity.CHANGE_PASSWORD_SUCCESS);
                if (resultCode== ChangePasswordActivity.CHANGE_PASSWORD_SUCCESS){
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                }
                break;
            case ACCOUNT_INFO_CHANGE:
                if (resultCode==1){
                    fetchMyInformation();
                }
                break;

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
                        app.setWeatherNow(weatherNow);
                        app.setWEATHER_ID(weatherID);
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
