package cn.huangchengxi.funnytrip.activity.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.adapter.PositionRVAdapter;
import cn.huangchengxi.funnytrip.handler.AppHandler;
import cn.huangchengxi.funnytrip.item.WeatherItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherPicker extends AppCompatActivity implements AppHandler.OnHandleMessage{
    private RecyclerView recyclerView;
    private PositionRVAdapter adapter;
    private Toolbar toolbar;
    private ImageView back;
    private TextView title;
    private TextView conform;
    private ArrayList<WeatherItem> list;
    private String weatherID;
    private Stack<String> backStack;
    private SwipeRefreshLayout srl;
    //private MyHandler myHandler=new MyHandler();
    private AppHandler myHandler=new AppHandler(this);

    private String address="http://guolin.tech/api/china";
    private String currentAddress="http://guolin.tech/api/china";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_picker);
        init();
    }
    private void init(){
        list=new ArrayList<>();
        backStack=new Stack<>();
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        title=findViewById(R.id.title);
        conform=findViewById(R.id.conform);
        conform.setVisibility(View.INVISIBLE);
        recyclerView=findViewById(R.id.position_rv);
        adapter=new PositionRVAdapter(list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnPositionClick(new PositionRVAdapter.OnPositionClick() {
            @Override
            public void onClick(View view, int pos) {
                if (list.get(pos).getWeatherID()==null){
                    String a=currentAddress+"/"+list.get(pos).getId();
                    backStack.push(currentAddress);
                    currentAddress=a;
                    requestForPosition(a);
                }else{
                    weatherID=list.get(pos).getWeatherID();
                    Intent intent=new Intent();
                    intent.putExtra("weather_id",weatherID);
                    setResult(0,intent);
                    finish();
                }
            }
        });
        srl=findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestForPosition(currentAddress);
            }
        });
        requestForPosition(address);
    }

    @Override
    public void onHandle(Message msg) {
        switch (msg.what){
            case 1:
                adapter.notifyDataSetChanged();
                srl.setRefreshing(false);
                break;
            case 2:
                Toast.makeText(WeatherPicker.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                srl.setRefreshing(false);
                break;
        }
    }

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {

        }
    }
    private void requestForPosition(String address){
        list.clear();
        adapter.notifyDataSetChanged();
        srl.setRefreshing(true);

        HttpHelper.sendOKHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message msg=myHandler.obtainMessage();
                msg.what=2;
                myHandler.sendMessage(msg);
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseStr=response.body().string();
                if (responseStr!="" && responseStr!=null){
                    try {
                        JSONArray array=new JSONArray(responseStr);
                        for (int i=0;i<array.length();i++){
                            JSONObject weatherItem=array.getJSONObject(i);
                            String name=weatherItem.getString("name");
                            int id=weatherItem.getInt("id");
                            String weatherId=null;
                            if (weatherItem.has("weather_id")){
                                weatherId=weatherItem.getString("weather_id");
                            }
                            WeatherItem item=new WeatherItem(name,String.valueOf(id));
                            if (weatherId!=null){
                                item.setWeatherID(weatherId);
                            }
                            list.add(item);
                        }
                        Message msg=myHandler.obtainMessage();
                        msg.what=1;
                        myHandler.sendMessage(msg);
                    }catch (Exception e){
                        Log.e("error",e.toString());
                    }
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (backStack.empty()){
            super.onBackPressed();
        }else{
            String a=backStack.pop();
            currentAddress=a;
            requestForPosition(a);
        }
    }
}
