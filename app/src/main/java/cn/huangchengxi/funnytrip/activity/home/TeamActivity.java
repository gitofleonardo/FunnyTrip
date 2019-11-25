package cn.huangchengxi.funnytrip.activity.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.team.AddTeamActivity;
import cn.huangchengxi.funnytrip.activity.team.TeamRoomActivity;
import cn.huangchengxi.funnytrip.adapter.TeamRVAdapter;
import cn.huangchengxi.funnytrip.item.TeamItem;

public class TeamActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView back;
    private RecyclerView teamRV;
    private FloatingActionButton addTeamButton;
    private TeamRVAdapter adapter;
    private List<TeamItem> list;
    private SwipeRefreshLayout srl;

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
        teamRV=findViewById(R.id.team_rv);
        addTeamButton=findViewById(R.id.add_team_button);
        addTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeamActivity.this, AddTeamActivity.class));
            }
        });
        list=new ArrayList<>();
        for (int i=0;i<5;i++){
            list.add(new TeamItem("0","有趣的旅行",false,"有趣的旅行:欢迎使用"));
        }
        adapter=new TeamRVAdapter(list);
        adapter.setOnTeamClick(new TeamRVAdapter.OnTeamClick() {
            @Override
            public void onClick(View view, int position) {
                TeamRoomActivity.startTeamRoomActivity(TeamActivity.this,list.get(position).getTeamId());
            }
        });
        teamRV.setLayoutManager(new LinearLayoutManager(this));
        teamRV.setAdapter(adapter);
    }
}
