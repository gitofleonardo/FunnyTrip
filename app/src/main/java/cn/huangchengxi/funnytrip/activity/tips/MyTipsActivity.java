package cn.huangchengxi.funnytrip.activity.tips;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.home.TipsActivity;
import cn.huangchengxi.funnytrip.adapter.MyTipsRVAdapter;
import cn.huangchengxi.funnytrip.item.TipsItem;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;

public class MyTipsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ImageView back;
    private SwipeRefreshLayout srl;
    private MyTipsRVAdapter adapter;
    private List<TipsItem> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tips);
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
        recyclerView=findViewById(R.id.tips_rv);
        list=new ArrayList<>();
        adapter=new MyTipsRVAdapter(list);
        adapter.setOnTipsRemoved(new MyTipsRVAdapter.OnTipsRemoved() {
            @Override
            public void onRemoved(int position) {
                SqliteHelper helper=new SqliteHelper(MyTipsActivity.this,"tips",null,1);
                SQLiteDatabase db=helper.getWritableDatabase();
                db.execSQL("delete from tips where id="+list.get(position).getTime());
            }
        });
        adapter.setOnItemClickListener(new MyTipsRVAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                TipsActivity.startTipsActivity(MyTipsActivity.this,list.get(position).getUrl());
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper.Callback callback=new TipsCallback(adapter);
        ItemTouchHelper helper=new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);

        srl=findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateTips();
            }
        });
        updateTips();
    }
    private void updateTips(){
        list.clear();
        SqliteHelper helper=new SqliteHelper(this,"tips",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("tips",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do{
                long id=cursor.getLong(cursor.getColumnIndex("id"));
                String name=cursor.getString(cursor.getColumnIndex("title"));
                String url=cursor.getString(cursor.getColumnIndex("url"));
                TipsItem item=new TipsItem(name,url,id);
                list.add(item);
            }while (cursor.moveToNext());
            adapter.notifyDataSetChanged();
        }
        if (srl.isRefreshing()){
            srl.setRefreshing(false);
        }
    }
}
