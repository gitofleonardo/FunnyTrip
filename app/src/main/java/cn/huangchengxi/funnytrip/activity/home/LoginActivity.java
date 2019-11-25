package cn.huangchengxi.funnytrip.activity.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.account.ChangePasswordActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText account;
    private EditText password;
    private LinearLayout editTextContainer;
    private Button loginButton;
    private TextView signUp;
    private TextView forgetPasswd;
    private LinearLayout buttonContainer;

    private final int SIGN_UP_RC=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        init();
    }
    private void init(){
        account=findViewById(R.id.account);
        password=findViewById(R.id.password);
        editTextContainer=findViewById(R.id.text_container);
        loginButton=findViewById(R.id.login_button);
        signUp=findViewById(R.id.sign_up);
        forgetPasswd=findViewById(R.id.forget_password);
        buttonContainer=findViewById(R.id.button_container);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoginRunnable();
            }
        });
        forgetPasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ChangePasswordActivity.class));
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(LoginActivity.this,SignUpActivity.class),SIGN_UP_RC);
            }
        });
        startAnimate();
    }
    private void startAnimate(){
        int dp=Dp2Px(this,100);
        editTextContainer.animate().translationYBy(dp).setDuration(500).setInterpolator(new FastOutSlowInInterpolator()).start();
        buttonContainer.animate().translationYBy(-dp).setDuration(500).setInterpolator(new FastOutSlowInInterpolator()).start();
    }
    private void startLoginRunnable(){

    }
    private int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density; //当前屏幕密度因子
        return (int) (dp * scale + 0.5f);
    }
    private int Px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case SIGN_UP_RC:
                if (data!=null){
                    String accounts=data.getStringExtra("account");
                    String passwords=data.getStringExtra("password");
                    account.setText(accounts);
                    password.setText(passwords);

                    startLoginRunnable();
                }
                break;
        }
    }
}
