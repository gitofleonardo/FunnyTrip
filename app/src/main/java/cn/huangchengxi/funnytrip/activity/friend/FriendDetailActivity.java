package cn.huangchengxi.funnytrip.activity.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.base.BaseAppCompatActivity;
import cn.huangchengxi.funnytrip.view.OptionView;

public class FriendDetailActivity extends BaseAppCompatActivity {
    //fetch user info by user id
    private String userId;
    private Toolbar toolbar;
    private ImageView back;
    private ImageView portrait;
    private OptionView moments;
    private OptionView sendMsg;
    private OptionView addFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparentStatusBar(this);
        setContentView(R.layout.activity_friend_detail);
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
        portrait=findViewById(R.id.portrait);
        moments=findViewById(R.id.moments);
        moments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserMomentActivity.startUserMomentActivity(FriendDetailActivity.this,userId);
            }
        });
        sendMsg=findViewById(R.id.chat);
        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChattingActivity.startChattingActivity(FriendDetailActivity.this,userId);
            }
        });
        addFriend=findViewById(R.id.add_friend);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFriendActivity.startAddFriendActivity(FriendDetailActivity.this,userId);
            }
        });
    }
    public static void startDetailActivity(Context context,String userId){
        Intent intent=new Intent(context,FriendDetailActivity.class);
        intent.putExtra("id",userId);
        context.startActivity(intent);
    }
}
