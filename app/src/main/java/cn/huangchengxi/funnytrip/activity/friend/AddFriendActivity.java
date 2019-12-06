package cn.huangchengxi.funnytrip.activity.friend;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.service.WebSocketMessageService;
import cn.huangchengxi.funnytrip.application.MainApplication;

public class AddFriendActivity extends AppCompatActivity {
    private String id;
    private EditText message;
    private TextView send;
    private ImageView back;
    private Toolbar toolbar;
    private AlertDialog dialog;
    private ServiceConnection connection=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend2);
        init();
    }
    private void init(){
        id=getIntent().getStringExtra("id");

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        message=findViewById(R.id.message);
        send=findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message.getText().toString().length()>20){
                    Toast.makeText(AddFriendActivity.this, "消息长度不能超过20", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder=new AlertDialog.Builder(AddFriendActivity.this);
                dialog=builder.setTitle("正在发送").setView(R.layout.view_processing_dialog).setCancelable(false).show();

                //get service first
                final String uid=((MainApplication)getApplicationContext()).getUID();
                final String to_uid=id;
                connection=new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                        String data="{" +
                                "\"type\":\"add_a_friend\"," +
                                "\"from_uid\":\""+uid+"\"," +
                                "\"to_uid\":\""+to_uid+"\"," +
                                "\"message\":\""+message.getText().toString()+"\"" +
                                "}";
                        ((WebSocketMessageService.WebSocketClientBinder)iBinder).getService().sendAddFriendMessage(data, new WebSocketMessageService.OnMessageCallback() {
                            @Override
                            public void onError() {
                                if (dialog!=null){
                                    dialog.dismiss();
                                }
                                Toast.makeText(AddFriendActivity.this, "发送请求失败，请检查连接", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onSuccess() {
                                if (dialog!=null){
                                    dialog.dismiss();
                                }
                                Toast.makeText(AddFriendActivity.this, "发送请求成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {

                    }
                };
                Intent intent=new Intent(AddFriendActivity.this,WebSocketMessageService.class);
                bindService(intent,connection,BIND_AUTO_CREATE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (connection!=null){
            unbindService(connection);
        }
        super.onDestroy();
    }

    public static void startAddFriendActivity(Context context, String userId){
        Intent intent=new Intent(context,AddFriendActivity.class);
        intent.putExtra("id",userId);
        context.startActivity(intent);
    }
}
