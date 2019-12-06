package cn.huangchengxi.funnytrip.activity.home;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.utils.TextValidator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {
    private LinearLayout textContainer;
    private EditText account;
    private EditText password;
    private EditText inviteCode;
    private Button signUp;
    private Button sendCode;
    private MyHandler myHandler=new MyHandler();
    private AlertDialog processingDialog;

    private final int REGISTER_SUCCESS=0;
    private final int CONNECTION_FAILED=1;
    private final int REGISTER_EMAIL_EXISTED=2;
    private final int REGISTER_VALIDATE_ERROR=3;

    private final int VALIDATE_CODE_SENT=6;
    private final int VALIDATE_CODE_SEND_FAILED=5;

    private final int UPDATE_CODE_BUTTON=4;

    private String accountStr;
    private String passwordStr;
    private Thread countDownThread;

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
        sendCode=findViewById(R.id.send_code);
        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextValidator.validateEmail(account.getText().toString())){
                    account.setError("邮箱格式不正确");
                    return;
                }
                sendCode.setEnabled(false);
                sendCode.setText("发送中...");
                HttpHelper.sendValidateCode(account.getText().toString(), new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        sendMessage(VALIDATE_CODE_SEND_FAILED);
                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        //Log.e("response",response.body().string());
                        try {
                            JSONObject json=new JSONObject(response.body().string());
                            String result=json.getString("result");
                            if (result.equals("ok")){
                                sendMessage(VALIDATE_CODE_SENT);
                            }else{
                                sendMessage(VALIDATE_CODE_SEND_FAILED);
                            }
                        }catch (Exception e){
                            Log.e("exception",e.toString());
                            sendMessage(VALIDATE_CODE_SEND_FAILED);
                        }
                    }
                });
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextValidator.validateEmail(account.getText().toString())){
                    account.setError("邮箱格式不正确");
                    return;
                }
                if (!TextValidator.validatePassword(password.getText().toString())){
                    password.setError("密码只能是数字、字母、下划线");
                    return;
                }
                if (inviteCode.getText().toString().length()!=5){
                    inviteCode.setError("验证码格式不正确");
                    return;
                }
                AlertDialog.Builder builder=new AlertDialog.Builder(SignUpActivity.this);
                builder.setTitle("正在注册").setMessage("稍等呀").setCancelable(false);
                processingDialog=builder.show();
                register(account.getText().toString(), password.getText().toString(), inviteCode.getText().toString());
            }
        });
    }
    private void register(String email,String password,String validate){
        this.accountStr=email;
        this.passwordStr=password;

        HttpHelper.registerRequest(email,password,validate, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                sendMessage(CONNECTION_FAILED);
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject json=new JSONObject(response.body().string());
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        sendMessage(REGISTER_SUCCESS);
                    }else if (result.equals("email_existed")){
                        sendMessage(REGISTER_EMAIL_EXISTED);
                    }else if (result.equals("validate_error")){
                        sendMessage(REGISTER_VALIDATE_ERROR);
                    }else{
                        sendMessage(CONNECTION_FAILED);
                    }
                }catch (Exception e){
                    Log.e("exception",e.toString());
                    sendMessage(CONNECTION_FAILED);
                }
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
            if (processingDialog!=null && processingDialog.isShowing()){
                processingDialog.dismiss();
            }
            switch (msg.what){
                case CONNECTION_FAILED:
                    if (countDownThread!=null){
                        countDownThread.interrupt();
                    }
                    sendCode.setEnabled(true);
                    sendCode.setText("发送验证码");
                    Toast.makeText(SignUpActivity.this, "连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    break;
                case REGISTER_VALIDATE_ERROR:
                    inviteCode.setError("验证码不正确");
                    break;
                case REGISTER_EMAIL_EXISTED:
                    account.setError("邮箱已注册");
                    break;
                case REGISTER_SUCCESS:
                    Toast.makeText(SignUpActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    Intent data=new Intent();
                    data.putExtra("account",accountStr);
                    data.putExtra("password",passwordStr);
                    setResult(1,data);
                    finish();
                    break;
                case UPDATE_CODE_BUTTON:
                    int seconds=msg.arg1;
                    if (seconds>0){
                        sendCode.setText("发送验证码("+seconds+"s)");
                    }else{
                        sendCode.setText("发送验证码");
                        sendCode.setEnabled(true);
                    }
                    break;
                case VALIDATE_CODE_SENT:
                    AlertDialog.Builder builder=new AlertDialog.Builder(SignUpActivity.this);
                    builder.setTitle("发送验证").setMessage("我们已发送一封带有验证码的邮件到您的邮箱，请注意查收").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                    countDownThread=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                int seconds=60;
                                while (seconds>=0){
                                    Message msg=myHandler.obtainMessage();
                                    msg.what=UPDATE_CODE_BUTTON;
                                    msg.arg1=seconds;
                                    myHandler.sendMessage(msg);
                                    seconds--;
                                    Thread.sleep(1000);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    countDownThread.start();
                    break;
                case VALIDATE_CODE_SEND_FAILED:
                    if (countDownThread!=null){
                        countDownThread.interrupt();
                    }
                    Toast.makeText(SignUpActivity.this, "发送失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    sendCode.setEnabled(true);
                    sendCode.setText("发送验证码");
                    break;
            }
        }
    }
    /*
    private void startAnimate(){
        int px=Dp2Px(this,100);
        textContainer.animate().translationYBy(px).setDuration(500).setInterpolator(new FastOutSlowInInterpolator()).start();
        signUp.animate().translationYBy(-px).setDuration(500).setInterpolator(new FastOutSlowInInterpolator()).start();
    }
     */

    private int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density; //当前屏幕密度因子
        return (int) (dp * scale + 0.5f);
    }
}
