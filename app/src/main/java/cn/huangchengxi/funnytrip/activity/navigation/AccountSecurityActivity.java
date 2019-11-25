package cn.huangchengxi.funnytrip.activity.navigation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.account.ChangePasswordActivity;
import cn.huangchengxi.funnytrip.view.OptionView;
import cn.huangchengxi.funnytrip.view.SwitchOptionView;

public class AccountSecurityActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView back;
    private SwitchOptionView allowUnknownViewMoments;
    private SwitchOptionView allowFriendViewMoments;
    private SwitchOptionView allowUnknownComment;
    private SwitchOptionView acceptAddFriend;
    private SwitchOptionView acceptMessage;
    private SwitchOptionView acceptTeamUp;
    private OptionView changePasswd;
    private SwitchOptionView showAddress;
    private SwitchOptionView showGender;
    private SwitchOptionView showBirth;
    private SwitchOptionView showHomeland;
    private SwitchOptionView showCareer;
    private SwitchOptionView showInterest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);
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
        allowFriendViewMoments=findViewById(R.id.allow_moments_viewed);
        allowUnknownViewMoments=findViewById(R.id.allow_moments_viewed_for_unknown);
        allowUnknownComment=findViewById(R.id.allow_moments_comment_for_unknown);
        acceptAddFriend=findViewById(R.id.allow_add_friend);
        acceptMessage=findViewById(R.id.allow_message);
        acceptTeamUp=findViewById(R.id.allow_team);
        changePasswd=findViewById(R.id.change_password);
        changePasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountSecurityActivity.this, ChangePasswordActivity.class));
            }
        });
        showAddress=findViewById(R.id.show_address);
        showGender=findViewById(R.id.show_gender);
        showBirth=findViewById(R.id.show_birth);
        showHomeland=findViewById(R.id.show_homeland);
        showCareer=findViewById(R.id.show_career);
        showInterest=findViewById(R.id.show_interest);
    }
}
