package cn.huangchengxi.funnytrip.activity.home;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.NoteItem;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;

public class NoteActivity extends AppCompatActivity {
    public static final int INSERT_OR_UPDATE_SUCCESS=0;
    public static final int INSERT_OR_UPDATE_FAILED=1;

    private Toolbar toolbar;
    private ImageView back;
    private TextView save;
    private EditText content;
    private TextView tip;
    private NoteItem note;
    private FloatingActionButton deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        init();
    }
    private void init(){
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
                if (!content.getText().toString().equals("") && content.getText().toString().length()<=180){
                    saveToLocalDatabaseAndCloud(content.getText().toString());
                    finish();
                }
            }
        });
        tip=findViewById(R.id.tip);
        content=findViewById(R.id.content);
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length()>180){
                    content.setTextColor(Color.rgb(255,0,0));
                }else{
                    content.setTextColor(Color.rgb(0,0,0));
                }
                tip.setText(s.toString().length()+"/180");
            }
        });
        long time=getIntent().getLongExtra("time",0);
        String fromContent=getIntent().getStringExtra("content");
        if (fromContent!=null){
            note=new NoteItem(time,fromContent);
            content.setText(fromContent);
        }
        deleteButton=findViewById(R.id.delete);
        if (note==null){
            deleteButton.setVisibility(View.GONE);
        }else{
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(NoteActivity.this);
                    builder.setTitle("确定删除").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SqliteHelper helper=new SqliteHelper(NoteActivity.this,"notes",null,1);
                            SQLiteDatabase db=helper.getWritableDatabase();
                            db.execSQL("delete from notes where note_time="+note.getTime());
                            Toast.makeText(NoteActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            setResult(INSERT_OR_UPDATE_SUCCESS);
                            finish();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog=builder.create();
                    dialog.show();
                }
            });
        }
    }
    private void saveToLocalDatabaseAndCloud(String content){
        SqliteHelper helper=new SqliteHelper(this,"notes",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        if (note==null){
            Date now=new Date();
            db.execSQL("insert into notes values("+now.getTime()+",\""+content+"\")");
            Log.e("insert","insert");
        }else{
            db.execSQL("update notes set content=\""+content+"\" where note_time="+note.getTime());
            Log.e("update","update");
        }
        //do upload-to-cloud process

        setResult(INSERT_OR_UPDATE_SUCCESS);
    }
    public static void startNoteActivityForResult(Context context,int requestCode,NoteItem note){
        Intent intent=new Intent(context,NoteActivity.class);
        if (note!=null){
            intent.putExtra("time",note.getTime());
            intent.putExtra("content",note.getContent());
        }
        ((AppCompatActivity)context).startActivityForResult(intent,requestCode);
    }
}
