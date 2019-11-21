package cn.huangchengxi.funnytrip.activity.friend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.huangchengxi.funnytrip.R;

public class AddFriendActivity extends AppCompatActivity {
    private String id;
    private EditText message;
    private TextView send;
    private ImageView back;
    private Toolbar toolbar;

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
    }
    public static void startAddFriendActivity(Context context,String userId){
        Intent intent=new Intent(context,AddFriendActivity.class);
        intent.putExtra("id",userId);
        context.startActivity(intent);
    }
}
