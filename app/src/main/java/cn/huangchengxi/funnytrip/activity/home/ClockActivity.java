package cn.huangchengxi.funnytrip.activity.home;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.base.BaseAppCompatActivity;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.handler.AppHandler;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ClockActivity extends BaseAppCompatActivity implements AppHandler.OnHandleMessage{
    public static final int SUCCESS=0;
    public static final int FAILURE=1;

    private BaiduMap baiduMap;
    private MapView mapView;
    private FloatingActionButton recordButton;
    private String locationMsg;
    private AlertDialog alertDialog;
    private LocationClient locationClient=null;
    private MyLocationListener myLocationListener=new MyLocationListener();
    private CoordinatorLayout coordinatorLayout;

    private double latitude;
    private double longitude;

    private final int CONNECTION_FAILED=0;
    private final int CLOCK_SUCCESS=1;
    private final int CLOCK_FAILED=2;
    private final int NOT_LOGIN=3;

    //private MyHandler myHandler=new MyHandler();
    private AppHandler myHandler=new AppHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        transparentStatusBar(this);
        init();
        initBDMap();
    }
    private class MyLocationListener extends BDAbstractLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            latitude=bdLocation.getLatitude();
            longitude=bdLocation.getLongitude();
            locationMsg=bdLocation.getAddrStr();

            MyLocationData.Builder builder=new MyLocationData.Builder();
            builder.latitude(latitude).longitude(longitude).accuracy(bdLocation.getRadius()).direction(bdLocation.getDirection());
            MyLocationData data=builder.build();
            baiduMap.setMyLocationData(data);

            LatLng latLng=new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
            MapStatus mapStatus=new MapStatus.Builder().target(latLng).zoom(15).build();
            MapStatusUpdate mapStatusUpdate= MapStatusUpdateFactory.newMapStatus(mapStatus);
            baiduMap.setMapStatus(mapStatusUpdate);
            showBottomSheet();
        }
    }
    private void initBDMap(){
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
    }
    private void showBottomSheet(){
        Snackbar.make(coordinatorLayout,locationMsg,Snackbar.LENGTH_INDEFINITE).setAction("打卡", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationMsg==null){
                    Toast.makeText(ClockActivity.this, "定位中...", Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder builder=new AlertDialog.Builder(ClockActivity.this);
                    builder.setTitle("确定在此地点打卡")
                            .setCancelable(false)
                            .setMessage(locationMsg+"\n"+"经度:"+latitude+"\n"+"纬度:"+longitude)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //do process
                                    //insertIntoTableAndUpdate(locationMsg,latitude,longitude);
                                    //return to main
                                    //setResult(SUCCESS);
                                    //finish();
                                    clock(locationMsg,latitude,longitude);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    locationClient.requestLocation();
                                }
                            });
                    alertDialog=builder.create();
                    alertDialog.show();
                }
            }
        }).setActionTextColor(Color.rgb(255,255,255)).setBackgroundTint(Color.rgb(3,169,244)).show();
    }
    private void clock(String location,double latitude,double longitude){
        String uid=((MainApplication)getApplicationContext()).getUID();
        HttpHelper.commitClock(uid, location, new Date().getTime(), latitude, longitude, this, new HttpHelper.OnCommonResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }

            @Override
            public void onReturnSuccess() {
                sendMessage(CLOCK_SUCCESS);
            }
        });
    }
    private void sendMessage(int what){
        Message msg=myHandler.obtainMessage();
        msg.what=what;
        myHandler.sendMessage(msg);
    }
    private void insertIntoTableAndUpdate(String location,double latitude,double longitude){
        SqliteHelper helper=new SqliteHelper(this,"clocks",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Date now=new Date();
        db.execSQL("insert into clocks values("+now.getTime()+",\""+location+"\","+latitude+","+longitude+")");
    }
    private void init(){
        mapView=findViewById(R.id.clock_map);
        baiduMap=mapView.getMap();
        mapView.showZoomControls(false);
        baiduMap.setMyLocationEnabled(true);
        LogoPosition pos=LogoPosition.logoPostionCenterBottom;
        mapView.setLogoPosition(pos);
        recordButton=findViewById(R.id.clock_record_button);
        coordinatorLayout=findViewById(R.id.coordinator);
        locationClient=new LocationClient(this);
        locationClient.registerLocationListener(myLocationListener);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationClient.requestLocation();
            }
        });
    }

    @Override
    public void onHandle(Message msg) {
        switch (msg.what){
            case CONNECTION_FAILED:
                Toast.makeText(ClockActivity.this, "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                break;
            case CLOCK_FAILED:
                Toast.makeText(ClockActivity.this, "打卡失败", Toast.LENGTH_SHORT).show();
                break;
            case CLOCK_SUCCESS:
                Toast.makeText(ClockActivity.this, "打卡成功", Toast.LENGTH_SHORT).show();
                setResult(SUCCESS);
                finish();
                break;
            case NOT_LOGIN:
                break;
        }
    }

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {

        }
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        locationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        super.onDestroy();
    }
}
