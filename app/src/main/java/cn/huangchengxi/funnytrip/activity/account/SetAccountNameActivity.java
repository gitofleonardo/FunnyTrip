package cn.huangchengxi.funnytrip.activity.account;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SetAccountNameActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView back;
    private TextView save;
    private EditText name;
    private TextView hint;

    private final int CONNECTION_FAILED=0;
    private final int CHANGE_FAILED=1;
    private final int CHANGE_SUCCESS=2;
    private final int NOT_LOGIN=3;

    private AlertDialog dialog;

    private MyHandler myHandler=new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_account_name);
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
        save=findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do save process
                int len=name.getText().toString().length();
                if (len>=5 && len<=20){
                    String myID=((MainApplication)getApplicationContext()).getUID();
                    changeNickname(myID,name.getText().toString());
                }else{
                    name.setError("昵称长度为5到20");
                }
            }
        });
        name=findViewById(R.id.name);
        hint=findViewById(R.id.name_hint);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length()>20){
                    hint.setTextColor(Color.rgb(255,0,0));
                }else{
                    hint.setTextColor(Color.rgb(0,0,0));
                }
                hint.setText(editable.toString().length()+"/10");
            }
        });
    }
    private void changeNickname(String myID,String newName){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        dialog=builder.setTitle("正在改...等一下哦").setView(R.layout.view_processing_dialog).setCancelable(false).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                onBackPressed();
            }
        }).show();
        HttpHelper.changeNickname(myID, newName, this, new HttpHelper.OnAfterChangeName() {
            @Override
            public void onReturnSuccess() {
                sendMessage(CHANGE_SUCCESS);
            }
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }
        });
    }
    private void sendMessage(int what){
        Message m=myHandler.obtainMessage();
        m.what=what;
        myHandler.sendMessage(m);
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CONNECTION_FAILED:
                    if (dialog!=null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                    Toast.makeText(SetAccountNameActivity.this, "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    break;
                case CHANGE_FAILED:
                    if (dialog!=null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                    Toast.makeText(SetAccountNameActivity.this, "更改失败", Toast.LENGTH_SHORT).show();
                    break;
                case CHANGE_SUCCESS:
                    if (dialog!=null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                    Toast.makeText(SetAccountNameActivity.this, "昵称更改成功", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent();
                    intent.putExtra("newName",name.getText().toString());
                    setResult(1,intent);
                    finish();
                    break;
                case NOT_LOGIN:

                    break;
            }
        }
    }
}
