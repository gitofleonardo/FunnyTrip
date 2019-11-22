package cn.huangchengxi.funnytrip.activity.tips;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;

public class StarTipActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView back;
    private TextView save;
    private EditText urlET;
    private EditText name;
    private String url;
    private TextView counter;

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
                    Date now=new Date();
                    SqliteHelper helper=new SqliteHelper(StarTipActivity.this,"tips",null,1);
                    SQLiteDatabase db=helper.getWritableDatabase();
                    db.execSQL("insert into tips values("+now.getTime()+",\""+url+"\",\""+name.getText().toString()+"\")");
                    Toast.makeText(StarTipActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    finish();
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
    public static void startStarTipsActivity(Context context,String url){
        Intent intent=new Intent(context,StarTipActivity.class);
        intent.putExtra("url",url);
        context.startActivity(intent);
    }
}
