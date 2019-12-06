package cn.huangchengxi.funnytrip.activity.team;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.adapter.InviteFriendBottomRVAdapter;
import cn.huangchengxi.funnytrip.adapter.TeamInviteFriendRVAdapter;
import cn.huangchengxi.funnytrip.item.BottomRVFriendItem;
import cn.huangchengxi.funnytrip.item.TeamInviteFriendItem;

public class AddTeamActivity extends AppCompatActivity {
    private TeamInviteFriendRVAdapter adapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ImageView back;
    private EditText teamName;
    private List<TeamInviteFriendItem> list;
    private TextView save;
    private TextView inviteFriends;
    private SwipeRefreshLayout bottomSheetSrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team);
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
        recyclerView=findViewById(R.id.add_team_friend);
        teamName=findViewById(R.id.team_name);
        save=findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        list=new ArrayList<>();
        for (int i=0;i<5;i++){
            list.add(new TeamInviteFriendItem("0","黄承喜","null"));
        }
        adapter=new TeamInviteFriendRVAdapter(list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        inviteFriends=findViewById(R.id.add_friend);
        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomFriendsSheet();
            }
        });
    }
    private void showBottomFriendsSheet() {
        View view = View.inflate(this, R.layout.view_add_team_invite_friend_bottom_sheet, null);
        final BottomSheetDialog bsd=new BottomSheetDialog(this);
        RecyclerView recyclerView = view.findViewById(R.id.choose_friends_rv);
        view.findViewById(R.id.collapse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsd.dismiss();
            }
        });
        final List<BottomRVFriendItem> list=new ArrayList<>();
        for (int i=0;i<10;i++){
            list.add(new BottomRVFriendItem(String.valueOf(i),"黄承喜"+i,"null",false));
        }
        InviteFriendBottomRVAdapter adapter=new InviteFriendBottomRVAdapter(list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        view.findViewById(R.id.save_chosen_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (BottomRVFriendItem item:list){
                    if (!containsFriend(item.getUserId()) && item.isCheck()){
                        AddTeamActivity.this.list.add(new TeamInviteFriendItem(item.getUserId(),item.getUserName(),item.getPortraitUrl()));
                    }
                }
                AddTeamActivity.this.adapter.notifyDataSetChanged();
                bsd.dismiss();
            }
        });
        bottomSheetSrl=view.findViewById(R.id.bottom_sheet_srl);
        bottomSheetSrl.setRefreshing(true);
        bsd.setContentView(view);
        bsd.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        bsd.show();
    }
    private boolean containsFriend(String id){
        for (int i=0;i<list.size();i++){
            if (list.get(i).getUserId().equals(id)){
                return true;
            }
        }
        return false;
    }
}
