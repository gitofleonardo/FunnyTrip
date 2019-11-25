package cn.huangchengxi.funnytrip.activity.account;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import cn.huangchengxi.funnytrip.R;

public class ChangePasswordActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView back;
    private EditText password;
    private EditText repassword;
    private EditText code;
    private Button sendCode;
    private Button submit;
    private MyHandler myHandler=new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
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
        password=findViewById(R.id.new_passwd);
        repassword=findViewById(R.id.new_passwd_repeat);
        code=findViewById(R.id.code);
        sendCode=findViewById(R.id.send_code);
        submit=findViewById(R.id.change_submit);
        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ChangePasswordActivity.this);
                builder.setMessage("我们已经将验证码发送至您的邮箱，请您查收").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
                sendCode.setEnabled(false);
                new Thread(new CountDownRunnable()).start();
            }
        });
    }
    private class CountDownRunnable implements Runnable{
        @Override
        public void run() {
            for (int i=60;i>=0;i--){
                Message msg=myHandler.obtainMessage();
                msg.what=0;
                msg.arg1=i;
                myHandler.sendMessage(msg);

                try {
                    Thread.sleep(1000);
                }catch (Exception e){}
            }
        }
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    if (msg.arg1==0){
                        sendCode.setEnabled(true);
                        sendCode.setText("发送验证码");
                    }else{
                        sendCode.setText("发送验证码("+msg.arg1+"s)");
                    }
                    break;
            }
        }
    }
}
