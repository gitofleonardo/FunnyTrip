package cn.huangchengxi.funnytrip.activity.team;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.adapter.TeamUpMessageAdapter;
import cn.huangchengxi.funnytrip.databean.TeamInvitationResultBean;
import cn.huangchengxi.funnytrip.item.TeamInvitationItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;

public class TeamInvitationActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView back;
    private RecyclerView recyclerView;
    private List<TeamInvitationItem> list;
    private TeamUpMessageAdapter adapter;
    private MyHandler myHandler=new MyHandler();
    private AlertDialog dialog;

    private final int CONNECTION_FAILED=0;
    private final int FETCH_SUCCESS=1;
    private final int AGREE_SUCCESS=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_invitation);
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
        recyclerView=findViewById(R.id.team_up_message_rv);
        list=new ArrayList<>();
        adapter=new TeamUpMessageAdapter(list,this);
        adapter.setOnAgreeListener(new TeamUpMessageAdapter.OnAgreeListener() {
            @Override
            public void onAgree(int position) {
                dialog=new AlertDialog.Builder(TeamInvitationActivity.this).setTitle("正在同意").setCancelable(false).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
                agreeInvitation(list.get(position).getTeamID());
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchTeamInvitations();
    }
    private void agreeInvitation(final String teamID){
        HttpHelper.agreeTeamUp(teamID, this, new HttpHelper.OnCommonResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }

            @Override
            public void onReturnSuccess() {
                Message m=myHandler.obtainMessage();
                m.what=AGREE_SUCCESS;
                m.obj=teamID;
                myHandler.sendMessage(m);
            }
        });
    }
    private void fetchTeamInvitations(){
        HttpHelper.fetchTeamInvitations(this, new HttpHelper.OnTeamInvitationsResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }

            @Override
            public void onReturnSuccess(TeamInvitationResultBean bean) {
                list.clear();
                list.addAll(bean.getList());
                sendMessage(FETCH_SUCCESS);
            }
        });
    }
    private void sendMessage(int what){
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
                        Toast.makeText(TeamInvitationActivity.this, "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                        break;
                    case FETCH_SUCCESS:
                        adapter.notifyDataSetChanged();
                        break;
                    case AGREE_SUCCESS:
                        if (dialog!=null){
                            dialog.dismiss();
                        }
                        for (int i=0;i<list.size();i++){
                            if (list.get(i).getTeamID().equals(msg.obj)){
                                list.get(i).setAgreed(true);
                                adapter.notifyItemChanged(i);
                            }
                        }
                        break;
                }
            }catch (Exception e){}
        }
    }
}
