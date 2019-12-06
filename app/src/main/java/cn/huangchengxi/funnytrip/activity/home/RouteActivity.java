package cn.huangchengxi.funnytrip.activity.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.route.AddRouteActivity;
import cn.huangchengxi.funnytrip.adapter.RouteAdapter;
import cn.huangchengxi.funnytrip.databean.RoutesResultBean;
import cn.huangchengxi.funnytrip.item.RouteItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;

public class RouteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RouteAdapter adapter;
    private List<RouteItem> list;
    private Toolbar toolbar;
    private ImageView back;
    private ImageView add;
    private SwipeRefreshLayout srl;
    private LinearLayoutManager layoutManager;

    private final int ADD_RC=0;
    private final int EDIT_RC=1;

    private final int CONNECTION_FAILED=2;
    private final int FETCH_ROUTES_SUCCESS=3;
    private MyHandler myHandler=new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        init();
    }
    private void init(){
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        add=findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivityForResult(new Intent(RouteActivity.this, AddRouteActivity.class),ADD_RC);
                AddRouteActivity.startAddRouteActivityForResult(RouteActivity.this,ADD_RC,null);
            }
        });
        recyclerView=findViewById(R.id.route_rv);
        list=new ArrayList<>();
        adapter=new RouteAdapter(list,this);
        recyclerView.setAdapter(adapter);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter.setOnRouteClick(new RouteAdapter.OnRouteClick() {
            @Override
            public void onClick(View view, int position) {
                AddRouteActivity.startAddRouteActivityForResult(RouteActivity.this,EDIT_RC,list.get(position));
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState==RecyclerView.SCROLL_STATE_IDLE){
                    if (layoutManager.findFirstCompletelyVisibleItemPosition()==list.size()-1){
                        if (list.size()==0){
                            fetchRoutes(new Date().getTime(),true);
                        }else{
                            fetchRoutes(list.get(list.size()-1).getTime(),false);
                        }
                    }
                }
            }
        });
        srl=findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchRoutes(new Date().getTime(),true);
                srl.setRefreshing(false);
            }
        });
        fetchRoutes(new Date().getTime(),true);
    }
    private void fetchRoutes(long timeLimit, final boolean remove){
        HttpHelper.fetchRoutes(timeLimit, this, new HttpHelper.OnRoutesResult() {
            @Override
            public void onReturnFailure() {
                Message msg=myHandler.obtainMessage();
                msg.what=CONNECTION_FAILED;
                myHandler.sendMessage(msg);
            }
            @Override
            public void onReturnSuccess(RoutesResultBean bean) {
                Message msg=myHandler.obtainMessage();
                msg.what=FETCH_ROUTES_SUCCESS;
                msg.obj=bean;
                msg.arg1=remove?1:0;
                myHandler.sendMessage(msg);
            }
        });
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what){
                    case CONNECTION_FAILED:
                        srl.setRefreshing(false);
                        Toast.makeText(RouteActivity.this, "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                        break;
                    case FETCH_ROUTES_SUCCESS:
                        srl.setRefreshing(false);
                        RoutesResultBean bean=(RoutesResultBean)msg.obj;
                        boolean remove=msg.arg1==1?true:false;
                        if (remove){
                            list.clear();
                        }
                        list.addAll(bean.getList());
                        adapter.notifyDataSetChanged();
                        break;
                }
            }catch (Exception e){}
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ADD_RC:
                //do update list process
            case EDIT_RC:
                fetchRoutes(new Date().getTime(),true);
                break;
        }
    }
}
