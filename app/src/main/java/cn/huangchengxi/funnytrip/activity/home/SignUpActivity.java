package cn.huangchengxi.funnytrip.activity.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import cn.huangchengxi.funnytrip.R;

public class SignUpActivity extends AppCompatActivity {
    private LinearLayout textContainer;
    private EditText account;
    private EditText password;
    private EditText inviteCode;
    private Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);
        init();
    }
    private void init(){
        textContainer=findViewById(R.id.text_container);
        account=findViewById(R.id.account);
        password=findViewById(R.id.password);
        inviteCode=findViewById(R.id.invite_code);
        signUp=findViewById(R.id.sign_up_button);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        startAnimate();
    }
    private void startAnimate(){
        int px=Dp2Px(this,100);
        textContainer.animate().translationYBy(px).setDuration(500).setInterpolator(new FastOutSlowInInterpolator()).start();
        signUp.animate().translationYBy(-px).setDuration(500).setInterpolator(new FastOutSlowInInterpolator()).start();
    }
    private int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density; //当前屏幕密度因子
        return (int) (dp * scale + 0.5f);
    }
    private int Px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
