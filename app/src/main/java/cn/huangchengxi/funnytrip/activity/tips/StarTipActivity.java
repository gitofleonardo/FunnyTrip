package cn.huangchengxi.funnytrip.activity.tips;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.Date;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.item.MessageItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class StarTipActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView back;
    private TextView save;
    private EditText urlET;
    private EditText name;
    private String url;
    private TextView counter;

    private final int CONNECTION_FAILED=0;
    private final int FETCH_SUCCESS=1;
    private final int FETCH_FAILED=2;
    private final int NOT_LOGIN=3;

    private MyHandler myHandler=new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_tip);
        init();
    }
    private void init(){
        url=getIntent().getStringExtra("url");
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        save=findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do save process
                if (name.getText().toString().equals("") || name.getText().toString().length()>20){
                    name.setError("检查名称");
                }else{
                    /*
                    Date now=new Date();
                    SqliteHelper helper=new SqliteHelper(StarTipActivity.this,"tips",null,1);
                    SQLiteDatabase db=helper.getWritableDatabase();
                    db.execSQL("insert into tips values("+now.getTime()+",\""+url+"\",\""+name.getText().toString()+"\")");
                    Toast.makeText(StarTipActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    finish();
                     */
                    commitTips(url,name.getText().toString());
                }
            }
        });
        urlET=findViewById(R.id.url);
        urlET.setText(url);
        name=findViewById(R.id.name);
        counter=findViewById(R.id.text_count);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                counter.setText(s.toString().length()+"/20");
            }
        });
    }
    private void commitTips(String url,String name){
        String uid=((MainApplication)getApplicationContext()).getUID();
        HttpHelper.commitTips(uid, url, name, this, new HttpHelper.OnCommonResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }

            @Override
            public void onReturnSuccess() {
                sendMessage(FETCH_SUCCESS);
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
                    Toast.makeText(StarTipActivity.this, "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    break;
                case FETCH_FAILED:
                    Toast.makeText(StarTipActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                    break;
                case NOT_LOGIN:
                    break;
                case FETCH_SUCCESS:
                    Toast.makeText(StarTipActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }
    }
    public static void startStarTipsActivity(Context context,String url){
        Intent intent=new Intent(context,StarTipActivity.class);
        intent.putExtra("url",url);
        context.startActivity(intent);
    }
}
