package cn.huangchengxi.funnytrip.activity.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.base.BaseAppCompatActivity;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.databean.UserInformationBean;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.view.OptionView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FriendDetailActivity extends BaseAppCompatActivity {
    //fetch user info by user id
    private String userId;
    private Toolbar toolbar;
    private ImageView back;
    private ImageView portrait;
    private OptionView moments;
    private OptionView sendMsg;
    private OptionView addFriend;
    private TextView address;
    private TextView email;
    private TextView gender;
    private TextView birth;
    private TextView homeland;
    private TextView career;
    private TextView interest;
    private TextView name;
    private MyHandler myHandler=new MyHandler();

    private UserInformationBean userInformationBean;

    private final int CONNECTION_FAILED=0;
    private final int FETCH_SUCCESS=3;

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
        name=findViewById(R.id.name);
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
        email=findViewById(R.id.user_id);
        address=findViewById(R.id.address);
        gender=findViewById(R.id.gender);
        birth=findViewById(R.id.birth);
        homeland=findViewById(R.id.homeland);
        career=findViewById(R.id.career);
        interest=findViewById(R.id.interest);

        String myID=((MainApplication)getApplicationContext()).getUID();
        String sessionID=((MainApplication)getApplicationContext()).getJSESSIONID();
        fetchUserInfo(myID,userId,sessionID);
    }
    private void fetchUserInfo(String fromUid,String toUid,String session){
        HttpHelper.fetchUserInformation(fromUid, toUid, this, new HttpHelper.OnFetchUserInformation() {
            @Override
            public void onReturnSuccess(UserInformationBean bean) {
                userInformationBean=bean;
                sendMessage(FETCH_SUCCESS);
            }
            @Override
            public void onReturnFail() {
                sendMessage(CONNECTION_FAILED);
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
                        Toast.makeText(FriendDetailActivity.this, "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                        break;
                    case FETCH_SUCCESS:
                        if (!userInformationBean.getPortraitUrl().equals("null")){
                            Glide.with(FriendDetailActivity.this).load(HttpHelper.SERVER_HOST+userInformationBean.getPortraitUrl()).into(portrait);
                        }
                        if (userInformationBean.getNickname().equals("null")){
                            name.setText(userId);
                        }else{
                            name.setText(userInformationBean.getNickname());
                        }
                        email.setText(userInformationBean.getMail());
                        if (!userInformationBean.getAddress().equals("null")){
                            address.setVisibility(View.VISIBLE);
                            address.setText("地址:"+userInformationBean.getAddress());
                        }
                        if (userInformationBean.getGender()!=0){
                            gender.setVisibility(View.VISIBLE);
                            gender.setText("性别:"+(userInformationBean.getGender()==1?"男":"女"));
                        }
                        if (userInformationBean.getBirthday()!=0){
                            birth.setVisibility(View.VISIBLE);
                            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                            birth.setText("生日:"+sdf.format(new Date(userInformationBean.getBirthday())));
                        }
                        if (!userInformationBean.getHomeland().equals("null")){
                            homeland.setVisibility(View.VISIBLE);
                            homeland.setText("故乡:"+userInformationBean.getHomeland());
                        }
                        if (!userInformationBean.getCareer().equals("null")){
                            career.setVisibility(View.VISIBLE);
                            career.setText("职业:"+userInformationBean.getCareer());
                        }
                        if (!userInformationBean.getInterest().equals("null")){
                            interest.setVisibility(View.VISIBLE);
                            interest.setText("兴趣:"+userInformationBean.getInterest());
                        }
                        if (userInformationBean.isShowAddFriend()){
                            addFriend.setVisibility(View.VISIBLE);
                        }
                        if (userInformationBean.isShowSendMessage()){
                            sendMsg.setVisibility(View.VISIBLE);
                        }
                        moments.setVisibility(View.VISIBLE);
                        break;
                }
            }catch (Exception e){}
        }
    }
    public static void startDetailActivity(Context context,String userId){
        Intent intent=new Intent(context,FriendDetailActivity.class);
        intent.putExtra("id",userId);
        context.startActivity(intent);
    }
}
