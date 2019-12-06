package cn.huangchengxi.funnytrip.activity.route;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.base.BaseAppCompatActivity;
import cn.huangchengxi.funnytrip.adapter.BottomPosAdapter;
import cn.huangchengxi.funnytrip.adapter.SearchResultAdapter;
import cn.huangchengxi.funnytrip.item.PositionItem;
import cn.huangchengxi.funnytrip.item.RouteItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;

public class AddRouteActivity extends BaseAppCompatActivity {
    private RecyclerView recyclerView;
    private SearchResultAdapter adapter;
    private EditText searchBar;
    private MapView mapView;
    private BaiduMap baiduMap;
    private SuggestionSearch suggestionSearch;
    private OnGetResult onGetResult;
    private String city;
    private LocationClient client;
    private MyLocationListener myLocationListener;
    private FloatingActionButton showButton;
    private List<PositionItem> positionItems=new ArrayList<>();
    private RecyclerView bottomRecyclerView;
    private BottomPosAdapter bottomPosAdapter;
    private BottomSheetDialog bsd;
    private ImageView collapse;
    private TextView save;
    private TextView clear;
    private ItemTouchHelper touchHelper;
    private EditText routeName;
    private FloatingActionButton delete;

    private RouteItem routeItem;

    private boolean isFromAutoFill=false;
    private MyHandler myHandler=new MyHandler();

    private final int COMMIT_SUCCESS=0;
    private final int CONNECTION_FAILED=1;
    private final int DELETE_SUCCESS=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_route);
        transparentStatusBar(this);
        init();
        initSearch();
        initBottomSheet();
    }
    private void initBottomSheet(){
        final View view=View.inflate(this,R.layout.view_bottom_pos,null);
        bottomRecyclerView=view.findViewById(R.id.bottom_pos_rv);
        collapse=view.findViewById(R.id.collapse);
        collapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bsd.isShowing()){
                    bsd.dismiss();
                }
            }
        });
        clear=view.findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionItems.clear();
                bottomPosAdapter.notifyDataSetChanged();
            }
        });
        save=view.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (routeName.getText().toString().length()>0 && routeName.getText().toString().length()<20){
                    if (positionItems.size()<2){
                        AlertDialog.Builder builder=new AlertDialog.Builder(AddRouteActivity.this);
                        builder.setTitle("位置必须大于2个").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }else{
                        //saveToDatabaseAndCloud(routeName.getText().toString());
                        commitRoutes(routeName.getText().toString());
                        Toast.makeText(AddRouteActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else{
                    routeName.setError("名称长度错误");
                }
            }
        });
        bottomPosAdapter=new BottomPosAdapter(positionItems);
        bottomRecyclerView.setAdapter(bottomPosAdapter);
        bottomRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bsd=new BottomSheetDialog(this);
        bsd.setContentView(view);
        bsd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                routeName.clearFocus();
            }
        });
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        bottomPosAdapter.setOnDragListionListener(new BottomPosAdapter.OnDragListionListener() {
            @Override
            public void onDrag(RecyclerView.ViewHolder holder) {
                touchHelper.startDrag(holder);
            }
        });
        bottomPosAdapter.setOnItemChangedListener(new BottomPosAdapter.OnItemChangedListener() {
            @Override
            public void onChanged() {
                updateMapOverlay();
            }
        });
        ItemTouchHelper.Callback callback=new BottomSheetHelperCallback(bottomPosAdapter);
        touchHelper=new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(bottomRecyclerView);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeName.clearFocus();
            }
        });
        routeName=view.findViewById(R.id.name);
        routeName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == event.KEYCODE_ENTER) {
                    InputMethodManager manager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(view.getWindowToken(),0);
                    routeName.clearFocus();
                }
                return false;
            }
        });
        routeName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getKeyCode()==KeyEvent.KEYCODE_ENTER){
                    return true;
                }
                return false;
            }
        });
    }
    private class MyMarkClickListener implements BaiduMap.OnMarkerClickListener{
        @Override
        public boolean onMarkerClick(Marker marker) {
            String name=marker.getExtraInfo().getString("name","null");
            showMarkerSnackInfo(name);
            return true;
        }
    }
    private void showMarkerSnackInfo(String name){
        Snackbar.make(findViewById(R.id.coordinator),name,Snackbar.LENGTH_INDEFINITE).show();
    }
    private void updateMapOverlay(){
        if (positionItems.size()>=2){
            baiduMap.clear();
            List<LatLng> points=new ArrayList<>();
            for (int i=0;i<positionItems.size();i++){
                points.add(new LatLng(positionItems.get(i).getLatitude(),positionItems.get(i).getLongitude()));
            }
            OverlayOptions options=new PolylineOptions().width(10).color(Color.rgb(255,0,0)).points(points);
            Overlay mOverlay=baiduMap.addOverlay(options);
            for (int i=0;i<positionItems.size();i++){
                Log.e("name",positionItems.get(i).getName());
                markMap(new LatLng(positionItems.get(i).getLatitude(), positionItems.get(i).getLongitude()),positionItems.get(i).getName());
            }
            baiduMap.setOnMarkerClickListener(new MyMarkClickListener());
            LatLng latLng=points.get(points.size()/2);
            MapStatus mapStatus=new MapStatus.Builder().target(latLng).zoom(15).build();
            MapStatusUpdate mapStatusUpdate= MapStatusUpdateFactory.newMapStatus(mapStatus);
            baiduMap.setMapStatus(mapStatusUpdate);
            setMapCenterTo(new LatLng(latLng.latitude,latLng.longitude));
        }
    }
    private void commitRoutes(String name){
        String routeID=routeItem==null?"null":routeItem.getRouteId();
        HttpHelper.commitRoute(name, positionItems,routeID ,this, new HttpHelper.OnCommonResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }
            @Override
            public void onReturnSuccess() {
                sendMessage(COMMIT_SUCCESS);
            }
        });
    }
    private void sendMessage(int what){
        Message msg=myHandler.obtainMessage();
        msg.what=what;
        myHandler.sendMessage(msg);
    }
    private void initSearch(){
        myLocationListener=new MyLocationListener();
        client=new LocationClient(this);
        baiduMap.setMyLocationEnabled(true);
        client.registerLocationListener(myLocationListener);
        LocationClientOption option=new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd0911");
        option.setScanSpan(0);
        option.setOpenGps(true);
        option.setIgnoreKillProcess(true);
        option.setWifiCacheTimeOut(5*60*1000);
        option.setEnableSimulateGps(false);
        option.setIsNeedAddress(true);
        client.setLocOption(option);
        client.start();

        suggestionSearch=SuggestionSearch.newInstance();
        onGetResult=new OnGetResult();
        suggestionSearch.setOnGetSuggestionResultListener(onGetResult);
    }
    private void hideKeyboard(){
        InputMethodManager manager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),0);
    }
    private void deleteRoute(){
        HttpHelper.deleteRoute(routeItem.getRouteId(), this, new HttpHelper.OnCommonResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }
            @Override
            public void onReturnSuccess() {
                sendMessage(DELETE_SUCCESS);
            }
        });
    }
    private void init(){
        routeItem=(RouteItem) getIntent().getSerializableExtra("route");
        delete=findViewById(R.id.delete);
        if (routeItem!=null){
            positionItems.addAll(routeItem.getRoute());

            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(AddRouteActivity.this);
                    builder.setTitle("确定删除").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteRoute();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            });
        }else{
            delete.setVisibility(View.GONE);
            //do nothing
        }
        recyclerView=findViewById(R.id.sug_rv);
        adapter=new SearchResultAdapter();
        adapter.setOnAddressClick(new SearchResultAdapter.OnAddressClick() {
            @Override
            public void onClick(View view, SuggestionResult.SuggestionInfo info) {
                baiduMap.clear();
                setMapCenterTo(info.pt);
                markMap(info.pt,info.key);
                showRecordSnackBar(info);
                searchBar.setText(info.key);
                searchBar.clearFocus();
                isFromAutoFill=true;
                adapter.clear();
                hideKeyboard();
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchBar=findViewById(R.id.search);
        mapView=findViewById(R.id.map);
        mapView.showZoomControls(false);
        mapView.setLogoPosition(LogoPosition.logoPostionCenterBottom);
        baiduMap=mapView.getMap();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                isFromAutoFill=false;
                if (city!=null){
                    suggestionSearch.requestSuggestion(new SuggestionSearchOption().city(city).keyword(s.toString()));
                }
            }
        });
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getKeyCode()==KeyEvent.KEYCODE_ENTER){
                    searchBar.clearFocus();
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });
        showButton=findViewById(R.id.show);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });
        updateMapOverlay();
    }
    private void showBottomSheet(){
        bsd.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        bsd.show();
        //bottomPosAdapter.highLightedItem(sugIndex);
    }
    private class OnGetResult implements OnGetSuggestionResultListener{
        @Override
        public void onGetSuggestionResult(SuggestionResult suggestionResult) {
            if (!isFromAutoFill && suggestionResult.getAllSuggestions()!=null){
                adapter.addAll(suggestionResult.getAllSuggestions());
            }
        }
    }
    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            double latitude=bdLocation.getLatitude();
            double longitude=bdLocation.getLongitude();
            city=bdLocation.getCity();
            MyLocationData.Builder builder=new MyLocationData.Builder();
            builder.latitude(latitude).longitude(longitude).accuracy(bdLocation.getRadius()).direction(bdLocation.getDirection());
            MyLocationData data=builder.build();
            baiduMap.setMyLocationData(data);
            if (routeItem==null){
                LatLng latLng=new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
                MapStatus mapStatus=new MapStatus.Builder().target(latLng).zoom(15).build();
                MapStatusUpdate mapStatusUpdate= MapStatusUpdateFactory.newMapStatus(mapStatus);
                baiduMap.setMapStatus(mapStatusUpdate);
                setMapCenterTo(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()));
            }
        }
    }
    private void showRecordSnackBar(final SuggestionResult.SuggestionInfo info){
        Snackbar.make(findViewById(R.id.coordinator),info.key,Snackbar.LENGTH_INDEFINITE).setAction("添加地点", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date=new Date();
                PositionItem item=new PositionItem(String.valueOf(date.getTime()),info.key,info.pt.latitude,info.pt.longitude);
                positionItems.add(item);
                bottomPosAdapter.notifyDataSetChanged();
                updateMapOverlay();
            }
        }).show();
    }
    private void setMapCenterTo(LatLng latLng){
        MapStatus mapStatus=new MapStatus.Builder().target(latLng).zoom(15).build();
        MapStatusUpdate mapStatusUpdate= MapStatusUpdateFactory.newMapStatus(mapStatus);
        baiduMap.setMapStatus(mapStatusUpdate);
    }
    private void markMap(LatLng point,String posName){
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.marker_small);
        Bundle bundle=new Bundle();
        bundle.putString("name",posName);
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .extraInfo(bundle)
                .icon(bitmap);
        baiduMap.addOverlay(option);
    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what){
                    case CONNECTION_FAILED:
                        Toast.makeText(AddRouteActivity.this, "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                        break;
                    case COMMIT_SUCCESS:
                        Toast.makeText(AddRouteActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case DELETE_SUCCESS:
                        Toast.makeText(AddRouteActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                }
            }catch (Exception e){}
        }
    }
    @Override
    public void onBackPressed() {
        if (adapter.getItemCount()>0){
            adapter.clear();
        }else{
            super.onBackPressed();
        }

    }
    @Override
    protected void onDestroy() {
        suggestionSearch.destroy();
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    public static void startAddRouteActivityForResult(Context context, int requestCode, RouteItem item){
        Intent intent=new Intent(context,AddRouteActivity.class);
        intent.putExtra("route",item);
        ((AppCompatActivity)context).startActivityForResult(intent,requestCode);
    }
}
