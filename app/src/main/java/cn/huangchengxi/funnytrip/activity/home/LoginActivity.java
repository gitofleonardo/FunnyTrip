package cn.huangchengxi.funnytrip.activity.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.account.ChangePasswordActivity;
import cn.huangchengxi.funnytrip.activity.service.WebSocketMessageService;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.utils.TextValidator;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText account;
    private EditText password;
    private LinearLayout editTextContainer;
    private Button loginButton;
    private TextView signUp;
    private TextView forgetPasswd;
    private LinearLayout buttonContainer;
    private MyHandler myHandler=new MyHandler();
    private AlertDialog processingDialog;

    private final int SIGN_UP_RC=0;
    private final int LOGIN_CONNECTION_FAILED=1;
    private final int LOGIN_SUCCESS=2;
    private final int LOGIN_NOT_CORRECT=3;

    private String accountStr;
    private String passwordStr;

    private ServiceConnection connection;

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
                if (!TextValidator.validateEmail(account.getText().toString())){
                    account.setError("邮箱格式不正确");
                    return;
                }
                if (!TextValidator.validatePassword(password.getText().toString())){
                    password.setError("密码格式不正确");
                    return;
                }
                accountStr=account.getText().toString();
                passwordStr=password.getText().toString();
                startLogin(accountStr,passwordStr);
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
        readAccountFromLocal();
        startAnimate();
    }
    private void readAccountFromLocal(){
        SqliteHelper helper=new SqliteHelper(this,"users",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("users",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            String email=cursor.getString(cursor.getColumnIndex("email"));
            String password_d=cursor.getString(cursor.getColumnIndex("passwd"));
            account.setText(email);
            password.setText(password_d);
        }
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if (processingDialog!=null && processingDialog.isShowing()){
                processingDialog.dismiss();
            }
            switch (msg.what){
                case LOGIN_CONNECTION_FAILED:
                    Toast.makeText(LoginActivity.this, "连接失败，清检查网络连接", Toast.LENGTH_SHORT).show();
                    break;
                case LOGIN_NOT_CORRECT:
                    Toast.makeText(LoginActivity.this, "账号或密码不正确", Toast.LENGTH_SHORT).show();
                    break;
                case LOGIN_SUCCESS:
                    updateLocalUserDatabaseAndSetAppData();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    setResult(1,new Intent());
                    finish();
                    break;
            }
        }
    }
    private void updateLocalUserDatabaseAndSetAppData(){
        SqliteHelper helper=new SqliteHelper(this,"users",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("users",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            db.execSQL("delete from users");
            db.execSQL("insert into users values(\""+accountStr+"\",\""+passwordStr+"\")");
        }else{
            db.execSQL("insert into users values(\""+accountStr+"\",\""+passwordStr+"\")");
        }
        MainApplication app=(MainApplication)getApplicationContext();
        app.setLogin(true);
        app.setCurrentAccount(accountStr);
        app.setCurrentPassword(passwordStr);

        connection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                ((WebSocketMessageService.WebSocketClientBinder)iBinder).getService().requestConnection();
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            }
        };
        Intent intent=new Intent(this,WebSocketMessageService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }

    private void startLogin(String account,String password){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        processingDialog=builder.setTitle("登录中").setMessage("等一下呀...").setView(R.layout.view_processing_dialog).setCancelable(false).show();

        HttpHelper.sendLoginRequest(account, password, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                sendMessage(LOGIN_CONNECTION_FAILED);
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject json=new JSONObject(response.body().string());
                    String result=json.getString("result");
                    if (result.equals("error")){
                        sendMessage(LOGIN_NOT_CORRECT);
                    }else if (result.equals("ok")){
                        String cookie=response.header("Set-Cookie");
                        Pattern pattern=Pattern.compile("^.*JSESSIONID=(.+);.*$");
                        Matcher matcher=pattern.matcher(cookie);
                        if (matcher.find()){
                            ((MainApplication)getApplicationContext()).setJSESSIONID(matcher.group(1));
                        }
                        String uid=json.getString("uid");
                        ((MainApplication)getApplicationContext()).setUID(uid);
                        sendMessage(LOGIN_SUCCESS);
                    }else{
                        sendMessage(LOGIN_NOT_CORRECT);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    sendMessage(LOGIN_NOT_CORRECT);
                }
            }
        });
    }
    private void sendMessage(int what){
        Message msg=myHandler.obtainMessage();
        msg.what=what;
        myHandler.sendMessage(msg);
    }
    private void startAnimate(){
        int dp=Dp2Px(this,100);
        editTextContainer.animate().translationYBy(dp).setDuration(500).setInterpolator(new FastOutSlowInInterpolator()).start();
        buttonContainer.animate().translationYBy(-dp).setDuration(500).setInterpolator(new FastOutSlowInInterpolator()).start();
    }
    private int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density; //当前屏幕密度因子
        return (int) (dp * scale + 0.5f);
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
                    accountStr=accounts;
                    passwordStr=passwords;
                    startLogin(accounts,passwords);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (connection!=null){
            unbindService(connection);
        }
        super.onDestroy();
    }
}
