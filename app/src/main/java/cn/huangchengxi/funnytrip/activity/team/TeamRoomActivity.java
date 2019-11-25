package cn.huangchengxi.funnytrip.activity.team;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.friend.FriendDetailActivity;
import cn.huangchengxi.funnytrip.adapter.TeamRoomRVAdapter;
import cn.huangchengxi.funnytrip.item.ChatRoomMsgItem;
import cn.huangchengxi.funnytrip.viewholder.ChatRoomRVHolder;

public class TeamRoomActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView back;
    private ImageView teamInfo;
    private RecyclerView msgRv;
    private SwipeRefreshLayout srl;
    private EditText message;
    private Button sendButton;
    private TextView teamName;
    private TeamRoomRVAdapter adapter;
    private String teamID;
    private List<ChatRoomMsgItem> list;

    private final int TEAM_DETAIL_RC=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_room);
        init();
    }
    private void init(){
        teamID=getIntent().getStringExtra("id");

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        teamInfo=findViewById(R.id.team_info);
        teamInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeamDetailActivity.startTeamDetailActivity(TeamRoomActivity.this,teamID,TEAM_DETAIL_RC);
            }
        });
        srl=findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
        msgRv=findViewById(R.id.team_room_rv);
        list=new ArrayList<>();
        for (int i=0;i<20;i++){
            list.add(new ChatRoomMsgItem("欢迎使用有趣的旅行客户端","0","0",new Date().getTime(),i%2==0?true:false,"黄承喜"));
        }
        adapter=new TeamRoomRVAdapter(list);
        adapter.setOnPortraitClick(new TeamRoomRVAdapter.OnPortraitClick() {
            @Override
            public void onClick(View view, int position) {
                FriendDetailActivity.startDetailActivity(TeamRoomActivity.this,list.get(position).getUserId());
            }
        });
        msgRv.setAdapter(adapter);
        msgRv.setLayoutManager(new LinearLayoutManager(this));
        message=findViewById(R.id.message);
        sendButton=findViewById(R.id.send_button);
        teamName=findViewById(R.id.team_name);
    }
    public static void startTeamRoomActivity(Context context,String teamId){
        Intent intent=new Intent(context,TeamRoomActivity.class);
        intent.putExtra("id",teamId);
        context.startActivity(intent);
    }
}
