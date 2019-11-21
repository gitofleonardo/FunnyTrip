package cn.huangchengxi.funnytrip.activity.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.Date;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.adapter.MesssageRVAdapter;
import cn.huangchengxi.funnytrip.item.MessageItem;
import cn.huangchengxi.funnytrip.view.HomeAppView;

public class MessageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MesssageRVAdapter adapter;
    private HomeAppView newFriend;
    private HomeAppView systemMsg;
    private HomeAppView replyMe;
    private ImageView back;
    private Toolbar toolbar;
    private SwipeRefreshLayout srl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        init();
    }
    private void init(){
        back=findViewById(R.id.back);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView=findViewById(R.id.message_rv);
        adapter=new MesssageRVAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.add(new MessageItem("0","Fake News!!!","特朗普",new Date().getTime(),""));
        adapter.add(new MessageItem("0","好男人就是我，我就是曾小贤","曾小贤",new Date().getTime(),""));
        adapter.add(new MessageItem("0","丢雷老母啊","徐金华",new Date().getTime(),""));
        adapter.add(new MessageItem("0","你再说","小学生",new Date().getTime(),""));
        adapter.add(new MessageItem("0","妖怪吧，这都行","张大仙",new Date().getTime(),""));
        adapter.add(new MessageItem("0","吼吼","黄承喜",new Date().getTime(),""));

        newFriend=findViewById(R.id.new_friends);
        systemMsg=findViewById(R.id.system_msg);
        replyMe=findViewById(R.id.reply);

        srl=findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (srl.isRefreshing()){
                    srl.setRefreshing(false);
                }
            }
        });
    }
}
