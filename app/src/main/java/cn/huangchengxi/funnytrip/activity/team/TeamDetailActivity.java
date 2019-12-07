package cn.huangchengxi.funnytrip.activity.team;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.friend.FriendDetailActivity;
import cn.huangchengxi.funnytrip.activity.service.WebSocketMessageService;
import cn.huangchengxi.funnytrip.adapter.InviteFriendBottomRVAdapter;
import cn.huangchengxi.funnytrip.adapter.TeamPartnerRVAdapter;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.databean.FriendBean;
import cn.huangchengxi.funnytrip.databean.TeamInformationResultBean;
import cn.huangchengxi.funnytrip.item.BottomRVFriendItem;
import cn.huangchengxi.funnytrip.item.FriendItem;
import cn.huangchengxi.funnytrip.item.TeamPartnerItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;

public class TeamDetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView back;
    private TextView teamName;
    private RecyclerView recyclerView;
    private CardView exitTeam;
    private CardView addTeammate;
    private List<TeamPartnerItem> list;
    private TeamPartnerRVAdapter adapter;
    private String teamID;
    private MyHandler myHandler=new MyHandler();
    private TeamInformationResultBean bean;
    private SwipeRefreshLayout bottomSheetSrl;
    private List<BottomRVFriendItem> bottomList;
    private InviteFriendBottomRVAdapter bottomAdapter;
    private ServiceConnection connection;
    private WebSocketMessageService.WebSocketClientBinder binder;

    private final int CONNECTION_FAILED=0;
    private final int FETCH_SUCCESS=1;
    private final int TEAM_NOT_FOUND=2;
    private final int EXIT_SUCCESS=3;
    private final int FETCH_FRIEND_SUCCESS=4;

    public static final int REFRESH=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);
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
        teamName=findViewById(R.id.team_name);
        recyclerView=findViewById(R.id.team_partner);
        list=new ArrayList<>();
        adapter=new TeamPartnerRVAdapter(this,list);
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
                if (bean.getTeamCreatorUID().equals(((MainApplication)getApplicationContext()).getUID())){
                    AlertDialog.Builder builder=new AlertDialog.Builder(TeamDetailActivity.this);
                    builder.setTitle("确定退出队伍").setMessage("由于您是队长，退出队伍将解散队伍，您确定要继续吗?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            exitTeam(true);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                }else{
                    AlertDialog.Builder builder=new AlertDialog.Builder(TeamDetailActivity.this);
                    builder.setTitle("确定退出队伍").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            exitTeam(false);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                }
            }
        });
        addTeammate=findViewById(R.id.invite_new);
        addTeammate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFriendBottomSheet();
            }
        });
        bindService();
        fetchTeamInformation();
    }
    private void showFriendBottomSheet(){
        View view = View.inflate(this, R.layout.view_add_team_invite_friend_bottom_sheet, null);
        final BottomSheetDialog bsd=new BottomSheetDialog(this);
        RecyclerView recyclerView = view.findViewById(R.id.choose_friends_rv);
        view.findViewById(R.id.collapse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsd.dismiss();
            }
        });
        bottomList=new ArrayList<>();
        bottomAdapter=new InviteFriendBottomRVAdapter(this,bottomList);
        recyclerView.setAdapter(bottomAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        view.findViewById(R.id.save_chosen_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (BottomRVFriendItem item:bottomList){
                    if (item.isCheck()){
                        String data="{" +
                                "\"type\":\"team_invitation\"," +
                                "\"from_uid\":\""+((MainApplication)getApplicationContext()).getUID()+"\"," +
                                "\"from_team_id\":\""+teamID+"\"," +
                                "\"to_uid\":\""+item.getUserId()+"\"" +
                                "}";
                        binder.getService().sendMessage(data,null);
                    }
                }
                bsd.dismiss();
            }
        });
        bottomSheetSrl=view.findViewById(R.id.bottom_sheet_srl);
        bottomSheetSrl.setRefreshing(true);
        bsd.setContentView(view);
        bsd.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        bsd.show();
        fetchMyFriends();
    }
    private void fetchMyFriends(){
        String uid=((MainApplication)getApplicationContext()).getUID();
        HttpHelper.fetchMyFriends(uid, this, new HttpHelper.OnFetchFriendResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }
            @Override
            public void onReturnSuccess(FriendBean bean) {
                bottomList.clear();
                for (int i=0;i<bean.getList().size();i++){
                    FriendItem friend=bean.getList().get(i);
                    BottomRVFriendItem item=new BottomRVFriendItem(friend.getUserID(),friend.getUserName(),friend.getPortraitUrl(),false);
                    bottomList.add(item);
                }
                sendMessage(FETCH_FRIEND_SUCCESS);
            }
        });
    }
    private void bindService(){
        connection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                binder=(WebSocketMessageService.WebSocketClientBinder)iBinder;
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        Intent intent=new Intent(this,WebSocketMessageService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }
    private void exitTeam(boolean delete){
        HttpHelper.exitTeam(teamID, delete, this, new HttpHelper.OnCommonResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }
            @Override
            public void onReturnSuccess() {
                sendMessage(EXIT_SUCCESS);
            }
        });
    }
    private void fetchTeamInformation(){
        HttpHelper.fetchTeamInformation(teamID, this, new HttpHelper.OnTeamInformationResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }

            @Override
            public void onReturnNotFound() {
                sendMessage(TEAM_NOT_FOUND);
            }
            @Override
            public void onReturnSuccess(TeamInformationResultBean bean) {
                Message msg=myHandler.obtainMessage();
                msg.what=FETCH_SUCCESS;
                msg.obj=bean;
                myHandler.sendMessage(msg);
            }
        });
    }
    private void sendMessage(int what){
        Message m=myHandler.obtainMessage();
        m.what=what;
        myHandler.sendMessage(m);
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what){
                    case CONNECTION_FAILED:
                        Toast.makeText(TeamDetailActivity.this, "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                        break;
                    case TEAM_NOT_FOUND:
                        Toast.makeText(TeamDetailActivity.this, "队伍不存在或者已经被解散", Toast.LENGTH_SHORT).show();
                        break;
                    case FETCH_SUCCESS:
                        bean=(TeamInformationResultBean) msg.obj;
                        teamName.setText(bean.getTeamName());
                        list.clear();
                        list.addAll(bean.getList());
                        adapter.notifyDataSetChanged();
                        exitTeam.setVisibility(View.VISIBLE);
                        addTeammate.setVisibility(View.VISIBLE);
                        break;
                    case EXIT_SUCCESS:
                        Toast.makeText(TeamDetailActivity.this, "您已经退出队伍", Toast.LENGTH_SHORT).show();
                        setResult(REFRESH);
                        finish();
                        break;
                    case FETCH_FRIEND_SUCCESS:
                        bottomSheetSrl.setRefreshing(false);
                        bottomAdapter.notifyDataSetChanged();
                        break;
                }
            }catch (Exception e){}
        }
    }
    public static void startTeamDetailActivity(Context context,String teamId,int requestCode){
        Intent intent=new Intent(context,TeamDetailActivity.class);
        intent.putExtra("id",teamId);
        ((AppCompatActivity)context).startActivityForResult(intent,requestCode);
    }
}
