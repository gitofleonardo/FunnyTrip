package cn.huangchengxi.funnytrip.activity.friend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.adapter.ChatRVAdapter;
import cn.huangchengxi.funnytrip.item.ChatMessageItem;

public class ChattingActivity extends AppCompatActivity {
    private String userId;
    private Toolbar toolbar;
    private ImageView back;
    private SwipeRefreshLayout srl;
    private RecyclerView recyclerView;
    private ChatRVAdapter adapter;
    private List<ChatMessageItem> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        init();
    }
    private void init(){
        userId=getIntent().getStringExtra("id");

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        srl=findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl.setRefreshing(false);
            }
        });
        recyclerView=findViewById(R.id.chat_rv);
        list=new ArrayList<>();
        for (int i=0;i<10;i++){
            list.add(new ChatMessageItem("你好呀，一定要记得我哟","0","0",new Date().getTime(),i%2==0?true:false));
        }
        adapter=new ChatRVAdapter(list);
        adapter.setOnPortraitClick(new ChatRVAdapter.OnPortraitClick() {
            @Override
            public void onClick(View view, int position) {
                ChatMessageItem item=list.get(position);
                if (item.isRecieved()){
                    FriendDetailActivity.startDetailActivity(ChattingActivity.this,item.getUserId());
                }else{

                }
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    public static void startChattingActivity(Context context,String userId){
        Intent intent=new Intent(context,ChattingActivity.class);
        intent.putExtra("id",userId);
        context.startActivity(intent);
    }
}
