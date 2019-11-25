package cn.huangchengxi.funnytrip.activity.team;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.friend.FriendDetailActivity;
import cn.huangchengxi.funnytrip.adapter.TeamPartnerRVAdapter;
import cn.huangchengxi.funnytrip.item.TeamPartnerItem;

public class TeamDetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView back;
    private TextView teamName;
    private RecyclerView recyclerView;
    private CardView exitTeam;
    private List<TeamPartnerItem> list;
    private TeamPartnerRVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);
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
        teamName=findViewById(R.id.team_name);
        recyclerView=findViewById(R.id.team_partner);
        list=new ArrayList<>();
        for (int i=0;i<6;i++){
            list.add(new TeamPartnerItem("0","黄承喜","null"));
        }
        adapter=new TeamPartnerRVAdapter(list);
        adapter.setOnPortraitClick(new TeamPartnerRVAdapter.OnPortraitClick() {
            @Override
            public void onClick(View view, int position) {
                FriendDetailActivity.startDetailActivity(TeamDetailActivity.this,list.get(position).getUserId());
            }
        });
        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        exitTeam=findViewById(R.id.exit_team);
        exitTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    public static void startTeamDetailActivity(Context context,String teamId,int requestCode){
        Intent intent=new Intent(context,TeamDetailActivity.class);
        intent.putExtra("id",teamId);
        ((AppCompatActivity)context).startActivityForResult(intent,requestCode);
    }
}
