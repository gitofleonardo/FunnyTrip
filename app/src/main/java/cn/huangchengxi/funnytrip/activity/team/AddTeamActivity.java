package cn.huangchengxi.funnytrip.activity.team;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.service.WebSocketMessageService;
import cn.huangchengxi.funnytrip.adapter.InviteFriendBottomRVAdapter;
import cn.huangchengxi.funnytrip.adapter.TeamInviteFriendRVAdapter;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.databean.FriendBean;
import cn.huangchengxi.funnytrip.handler.AppHandler;
import cn.huangchengxi.funnytrip.item.BottomRVFriendItem;
import cn.huangchengxi.funnytrip.item.FriendItem;
import cn.huangchengxi.funnytrip.item.TeamInviteFriendItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;

public class AddTeamActivity extends AppCompatActivity implements AppHandler.OnHandleMessage{
    private TeamInviteFriendRVAdapter adapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ImageView back;
    private EditText teamName;
    private List<TeamInviteFriendItem> list;
    private List<BottomRVFriendItem> bottomList;
    private TextView save;
    private TextView inviteFriends;
    private SwipeRefreshLayout bottomSheetSrl;
    private ServiceConnection connection;
    private WebSocketMessageService.WebSocketClientBinder binder;
    //private MyHandler myHandler=new MyHandler();
    private AppHandler myHandler=new AppHandler(this);

    private InviteFriendBottomRVAdapter bottomAdapter;

    private final int CONNECTION_FAILED=0;
    private final int CREATE_SUCCESS=1;
    private final int INVITE_SUCCESS=2;
    private final int INVITE_FAILED=3;
    private final int FETCH_FRIEND_SUCCESS=4;

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
                if (!teamName.getText().toString().equals("") && teamName.getText().length()<20){
                    createTeam(teamName.getText().toString());
                }else{
                    Toast.makeText(AddTeamActivity.this, "队伍名称长度必须大于0小于20", Toast.LENGTH_SHORT).show();
                }
            }
        });
        list=new ArrayList<>();
        adapter=new TeamInviteFriendRVAdapter(list,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        inviteFriends=findViewById(R.id.add_friend);
        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomFriendsSheet();
            }
        });
        bindService();
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
    private void createTeam(String teamName){
        HttpHelper.createTeam(teamName, this, new HttpHelper.OnTeamCreateResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }
            @Override
            public void onReturnSuccess(String teamID) {
                sendMessage(CREATE_SUCCESS);
            }
        });
    }
    private void inviteTeammate(List<TeamInviteFriendItem> list,String teamID){
        String uid=((MainApplication)getApplicationContext()).getUID();
        for (int i=0;i<list.size();i++){
            String data="{" +
                    "\"type\":\"team_invitation\"," +
                    "\"from_uid\":\""+uid+"\"," +
                    "\"from_team_id\":\""+teamID+"\"," +
                    "\"to_uid\":\""+list.get(i).getUserId()+"\"" +
                    "}";
            if (i==list.size()-1){
                binder.getService().sendMessage(data, new WebSocketMessageService.OnMessageCallback() {
                    @Override
                    public void onError() {
                        sendMessage(INVITE_FAILED);
                    }
                    @Override
                    public void onSuccess() {
                        sendMessage(INVITE_SUCCESS);
                    }
                });
            }else{
                binder.getService().sendMessage(data,null);
            }
        }
    }
    private void sendMessage(int what){
        Message msg=myHandler.obtainMessage();
        msg.what=what;
        myHandler.sendMessage(msg);
    }

    @Override
    public void onHandle(Message msg) {
        switch (msg.what){
            case CONNECTION_FAILED:
                if (bottomSheetSrl!=null){
                    bottomSheetSrl.setRefreshing(false);
                }
                Toast.makeText(AddTeamActivity.this, "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                break;
            case CREATE_SUCCESS:
                Toast.makeText(AddTeamActivity.this, "正在邀请好友", Toast.LENGTH_SHORT).show();
                inviteTeammate(list,((MainApplication)getApplicationContext()).getUID());
                break;
            case INVITE_SUCCESS:
                Toast.makeText(AddTeamActivity.this, "邀请成功", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case INVITE_FAILED:
                Toast.makeText(AddTeamActivity.this, "邀请好友失败", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case FETCH_FRIEND_SUCCESS:
                bottomSheetSrl.setRefreshing(false);
                bottomAdapter.notifyDataSetChanged();
                break;
        }
    }

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            try {

            }catch (Exception e){
                Log.e("addteamexception",e.toString());
            }
        }
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
        bottomList=new ArrayList<>();
        bottomAdapter=new InviteFriendBottomRVAdapter(this,bottomList);
        recyclerView.setAdapter(bottomAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        view.findViewById(R.id.save_chosen_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (BottomRVFriendItem item:bottomList){
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
    private boolean containsFriend(String id){
        for (int i=0;i<list.size();i++){
            if (list.get(i).getUserId().equals(id)){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        if (connection!=null){
            unbindService(connection);
        }
        super.onDestroy();
    }
}
