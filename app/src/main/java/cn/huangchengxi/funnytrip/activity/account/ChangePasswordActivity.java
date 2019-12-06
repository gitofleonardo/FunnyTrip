package cn.huangchengxi.funnytrip.activity.account;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.utils.TextValidator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView back;
    private EditText password;
    private EditText repassword;
    private EditText email;
    private EditText code;
    private Button sendCode;
    private Button submit;
    private MyHandler myHandler=new MyHandler();
    private AlertDialog processingDialog;

    private final int BUTTON_COUNT_DOWN=0;
    private final int VALIDATE_SENT=1;
    private final int VALIDATE_ERROR=2;
    private final int CONNECTION_ERROR=3;
    public static final int CHANGE_PASSWORD_SUCCESS=4;

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
        email=findViewById(R.id.email);
        code=findViewById(R.id.code);
        sendCode=findViewById(R.id.send_code);
        submit=findViewById(R.id.change_submit);
        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextValidator.validateEmail(email.getText().toString())){
                    email.setError("请检查邮箱格式");
                    return;
                }
                sendCode.setEnabled(false);
                sendCode.setText("发送中");
                HttpHelper.sendValidateCode(email.getText().toString(), new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        sendMessage(CONNECTION_ERROR);
                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try {
                            JSONObject json=new JSONObject(response.body().string());
                            String result=json.getString("result");
                            if (result.equals("ok")){
                                sendMessage(VALIDATE_SENT);
                            }else{
                                sendMessage(CONNECTION_ERROR);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            sendMessage(CONNECTION_ERROR);
                        }
                    }
                });
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextValidator.validateEmail(email.getText().toString())){
                    email.setError("请检查邮箱格式");
                    return;
                }
                if (!password.getText().toString().equals(repassword.getText().toString())){
                    Toast.makeText(ChangePasswordActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!TextValidator.validatePassword(password.getText().toString())){
                    password.setError("密码只能为数字、字母、下划线且包含10到20个字符");
                    return;
                }
                if (!TextValidator.validateCode(code.getText().toString())){
                    code.setError("验证码格式不正确");
                    return;
                }
                changePassword(email.getText().toString(),password.getText().toString(),code.getText().toString());
            }
        });
    }
    private void changePassword(String email,String newPassword,String validate){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        processingDialog=builder.setTitle("正在更改密码").setMessage("等一下呀...").setCancelable(false).show();

        HttpHelper.sendChangePassword(email, newPassword, validate, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                sendMessage(CONNECTION_ERROR);
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject json=new JSONObject(response.body().string());
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        sendMessage(CHANGE_PASSWORD_SUCCESS);
                    }else{
                        sendMessage(VALIDATE_ERROR);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    sendMessage(VALIDATE_ERROR);
                }
            }
        });
    }
    private void sendMessage(int what){
        Message msg=myHandler.obtainMessage();
        msg.what=what;
        myHandler.sendMessage(msg);
    }
    private class CountDownRunnable implements Runnable{
        @Override
        public void run() {
            for (int i=60;i>=0;i--){
                Message msg=myHandler.obtainMessage();
                msg.what=BUTTON_COUNT_DOWN;
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
            if (processingDialog!=null && processingDialog.isShowing()){
                processingDialog.dismiss();
            }
            switch (msg.what){
                case BUTTON_COUNT_DOWN:
                    if (msg.arg1==0){
                        sendCode.setEnabled(true);
                        sendCode.setText("发送验证码");
                    }else{
                        sendCode.setText("发送验证码("+msg.arg1+"s)");
                    }
                    break;
                case VALIDATE_SENT:
                    AlertDialog.Builder builder=new AlertDialog.Builder(ChangePasswordActivity.this);
                    builder.setMessage("我们已经将验证码发送至您的邮箱，请您查收").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                    sendCode.setEnabled(false);
                    new Thread(new CountDownRunnable()).start();
                    break;
                case CONNECTION_ERROR:
                    Toast.makeText(ChangePasswordActivity.this, "连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    sendCode.setEnabled(true);
                    sendCode.setText("发送验证码");
                    break;
                case VALIDATE_ERROR:
                    Toast.makeText(ChangePasswordActivity.this, "验证码不正确", Toast.LENGTH_SHORT).show();
                    break;
                case CHANGE_PASSWORD_SUCCESS:
                    Toast.makeText(ChangePasswordActivity.this, "更改密码成功，请重新登录", Toast.LENGTH_SHORT).show();
                    setResult(CHANGE_PASSWORD_SUCCESS,new Intent());
                    finish();
                    break;
            }
        }
    }
}
