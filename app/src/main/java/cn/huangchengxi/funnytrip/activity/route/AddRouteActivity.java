package cn.huangchengxi.funnytrip.activity.route;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.base.BaseAppCompatActivity;
import cn.huangchengxi.funnytrip.adapter.BottomPosAdapter;
import cn.huangchengxi.funnytrip.adapter.SearchResultAdapter;
import cn.huangchengxi.funnytrip.item.PositionItem;
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
    private List<PositionItem> positionItems;
    private RecyclerView bottomRecyclerView;
    private BottomPosAdapter bottomPosAdapter;
    private BottomSheetDialog bsd;
    private ImageView collapse;
    private TextView save;
    private TextView clear;

    private boolean isFromAutoFill=false;


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
        View view=View.inflate(this,R.layout.view_bottom_pos,null);
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
                saveToDatabaseAndCloud("路线");
                Toast.makeText(AddRouteActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        positionItems=new ArrayList<>();
        bottomPosAdapter=new BottomPosAdapter(positionItems);
        bottomRecyclerView.setAdapter(bottomPosAdapter);
        bottomRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bsd=new BottomSheetDialog(this);
        bsd.setContentView(view);
    }
    private void saveToDatabaseAndCloud(String name){
        Date now=new Date();
        SqliteHelper helper=new SqliteHelper(this,"routes",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        db.execSQL("insert into routes values(\""+name+"\","+now.getTime()+")");

        helper=new SqliteHelper(this,"positions",null,1);
        db=helper.getWritableDatabase();
        for (int i=0;i<positionItems.size();i++){
            PositionItem item=positionItems.get(i);
            db.execSQL("insert into positions values(\""+item.getName()+"\","+item.getLatitude()+","+item.getLongitude()+","+now.getTime()+","+i+")");
        }
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
    private void init(){
        recyclerView=findViewById(R.id.sug_rv);
        adapter=new SearchResultAdapter();
        adapter.setOnAddressClick(new SearchResultAdapter.OnAddressClick() {
            @Override
            public void onClick(View view, SuggestionResult.SuggestionInfo info) {
                baiduMap.clear();
                setMapCenterTo(info.pt);
                markMap(info.pt);
                showRecordSnackBar(info);
                searchBar.setText(info.key);
                searchBar.clearFocus();
                isFromAutoFill=true;
                adapter.clear();
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
        showButton=findViewById(R.id.show);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });
    }
    private void showBottomSheet(){
        bsd.show();
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
            LatLng latLng=new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
            MapStatus mapStatus=new MapStatus.Builder().target(latLng).zoom(15).build();
            MapStatusUpdate mapStatusUpdate= MapStatusUpdateFactory.newMapStatus(mapStatus);
            baiduMap.setMapStatus(mapStatusUpdate);
            setMapCenterTo(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()));
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
            }
        }).show();
    }
    private void setMapCenterTo(LatLng latLng){
        MapStatus mapStatus=new MapStatus.Builder().target(latLng).zoom(15).build();
        MapStatusUpdate mapStatusUpdate= MapStatusUpdateFactory.newMapStatus(mapStatus);
        baiduMap.setMapStatus(mapStatusUpdate);
    }
    private void markMap(LatLng point){
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.marker_small);
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        baiduMap.addOverlay(option);
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
}
