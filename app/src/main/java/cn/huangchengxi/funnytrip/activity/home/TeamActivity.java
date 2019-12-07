package cn.huangchengxi.funnytrip.activity.home;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.team.AddTeamActivity;
import cn.huangchengxi.funnytrip.activity.team.TeamDetailActivity;
import cn.huangchengxi.funnytrip.adapter.TeamRVAdapter;
import cn.huangchengxi.funnytrip.databean.TeamResultBean;
import cn.huangchengxi.funnytrip.item.TeamItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;

public class TeamActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView back;
    private RecyclerView teamRV;
    private FloatingActionButton addTeamButton;
    private TeamRVAdapter adapter;
    private List<TeamItem> list;
    private SwipeRefreshLayout srl;
    private MyHandler myHandler=new MyHandler();

    private final int CONNECTION_FAILED=0;
    private final int FETCH_SUCCESS=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        init();
    }
    private void init(){
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        srl=findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchMyTeam();
            }
        });
        teamRV=findViewById(R.id.team_rv);
        addTeamButton=findViewById(R.id.add_team_button);
        addTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeamActivity.this, AddTeamActivity.class));
            }
        });
        list=new ArrayList<>();
        adapter=new TeamRVAdapter(list);
        adapter.setOnTeamClick(new TeamRVAdapter.OnTeamClick() {
            @Override
            public void onClick(View view, int position) {
                //TeamRoomActivity.startTeamRoomActivity(TeamActivity.this,list.get(position).getTeamId());
                TeamDetailActivity.startTeamDetailActivity(TeamActivity.this,list.get(position).getTeamId(),0);
            }
        });
        teamRV.setLayoutManager(new LinearLayoutManager(this));
        teamRV.setAdapter(adapter);

        fetchMyTeam();
    }
    private void fetchMyTeam(){
        HttpHelper.fetchTeams(this, new HttpHelper.OnTeamResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }
            @Override
            public void onReturnSuccess(TeamResultBean bean) {
                list.clear();
                list.addAll(bean.getList());
                sendMessage(FETCH_SUCCESS);
            }
        });
    }
    public void sendMessage(int what){
        Message msg=myHandler.obtainMessage();
        msg.what=what;
        myHandler.sendMessage(msg);
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what){
                    case CONNECTION_FAILED:
                        srl.setRefreshing(false);
                        Toast.makeText(TeamActivity.this, "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                        break;
                    case FETCH_SUCCESS:
                        srl.setRefreshing(false);
                        adapter.notifyDataSetChanged();
                        break;
                }
            }catch (Exception e){}
        }
    }
}
