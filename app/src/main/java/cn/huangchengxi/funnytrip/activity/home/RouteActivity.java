package cn.huangchengxi.funnytrip.activity.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.route.AddRouteActivity;
import cn.huangchengxi.funnytrip.adapter.RouteAdapter;
import cn.huangchengxi.funnytrip.item.PositionItem;
import cn.huangchengxi.funnytrip.item.RouteItem;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;

public class RouteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RouteAdapter adapter;
    private List<RouteItem> list;
    private Toolbar toolbar;
    private ImageView back;
    private ImageView add;
    private SwipeRefreshLayout srl;

    private final int ADD_RC=0;
    private final int EDIT_RC=1;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnRouteClick(new RouteAdapter.OnRouteClick() {
            @Override
            public void onClick(View view, int position) {
                AddRouteActivity.startAddRouteActivityForResult(RouteActivity.this,EDIT_RC,list.get(position).getRouteId());
            }
        });
        srl=findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRoutes();
                srl.setRefreshing(false);
            }
        });
        updateRoutes();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ADD_RC:
                //do update list process
                updateRoutes();
                break;
            case EDIT_RC:
                updateRoutes();
                break;
        }
    }
    private void updateRoutes(){
        SqliteHelper helper1=new SqliteHelper(this,"routes",null,1);
        SqliteHelper helper2=new SqliteHelper(this,"positions",null,1);
        SQLiteDatabase db1=helper1.getWritableDatabase();
        SQLiteDatabase db2=helper2.getWritableDatabase();
        Cursor c1=db1.query("routes",null,null,null,null,null,null);
        if (c1.moveToFirst()){
            List<RouteItem> routes=new ArrayList<>();
            do{
                long time=c1.getLong(c1.getColumnIndex("route_time"));
                String name=c1.getString(c1.getColumnIndex("name"));
                Cursor c2=db2.query("positions",null,"route=?",new String[]{String.valueOf(time)},null,null,"pos_index");
                if (c2.moveToFirst()){
                    List<PositionItem> positions=new ArrayList<>();
                    do{
                        PositionItem item=new PositionItem(String.valueOf(time),c2.getString(c2.getColumnIndex("name")),c2.getDouble(c2.getColumnIndex("latitude")),c2.getDouble(c2.getColumnIndex("longitude")));
                        positions.add(item);
                    }while (c2.moveToNext());
                    routes.add(new RouteItem(positions,String.valueOf(time),name));
                }
            }while(c1.moveToNext());
            list.clear();
            list.addAll(routes);
            adapter.notifyDataSetChanged();
        }
    }
}
