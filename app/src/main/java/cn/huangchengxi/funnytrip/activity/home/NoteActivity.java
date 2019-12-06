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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.item.NoteItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;

public class NoteActivity extends AppCompatActivity {
    public static final int INSERT_OR_UPDATE_SUCCESS=0;
    public static final int INSERT_OR_UPDATE_FAILED=1;

    private final int CONNECTION_FAILED=2;
    private final int DELETE_SUCCESS=3;
    private final int COMMIT_SUCCESS=4;

    private Toolbar toolbar;
    private ImageView back;
    private TextView save;
    private EditText content;
    private TextView tip;
    private NoteItem note;
    private FloatingActionButton deleteButton;
    private MyHandler myHandler=new MyHandler();

    private AlertDialog dialog;

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
                    if (note!=null){
                        updateNote(content.getText().toString(),note.getId());
                    }else{
                        dialog=new AlertDialog.Builder(NoteActivity.this).setTitle("正在保存").setView(R.layout.view_processing_dialog).setCancelable(false).show();
                        commitNote(content.getText().toString());
                    }
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
        String noteID=getIntent().getStringExtra("note_id");
        if (fromContent!=null){
            note=new NoteItem(noteID,time,fromContent);
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
                            NoteActivity.this.dialog=new AlertDialog.Builder(NoteActivity.this).setTitle("正在删除").setView(R.layout.view_processing_dialog).setCancelable(false).show();
                            deleteNote(note.getId());
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
    private void updateNote(String content,String id){
        HttpHelper.updateNote(content, id,this, new HttpHelper.OnCommonResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }

            @Override
            public void onReturnSuccess() {
                sendMessage(COMMIT_SUCCESS);
            }
        });
    }
    private void deleteNote(String id){
        HttpHelper.deleteNote(id, this, new HttpHelper.OnCommonResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }

            @Override
            public void onReturnSuccess() {
                sendMessage(DELETE_SUCCESS);
            }
        });
    }
    private void commitNote(String content){
        HttpHelper.commitNote(content, this, new HttpHelper.OnCommonResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }

            @Override
            public void onReturnSuccess() {
                sendMessage(COMMIT_SUCCESS);
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
            try {
                switch (msg.what){
                    case CONNECTION_FAILED:
                        Toast.makeText(NoteActivity.this, "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                        break;
                    case COMMIT_SUCCESS:
                        if (dialog!=null){
                            dialog.dismiss();
                        }
                        Toast.makeText(NoteActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        setResult(INSERT_OR_UPDATE_SUCCESS,new Intent());
                        finish();
                        break;
                    case DELETE_SUCCESS:
                        if (dialog!=null){
                            dialog.dismiss();
                        }
                        setResult(INSERT_OR_UPDATE_SUCCESS,new Intent());
                        finish();
                        break;
                }
            }catch (Exception e){}
        }
    }
    public static void startNoteActivityForResult(Context context,int requestCode,NoteItem note){
        Intent intent=new Intent(context,NoteActivity.class);
        if (note!=null){
            intent.putExtra("time",note.getTime());
            intent.putExtra("content",note.getContent());
            intent.putExtra("note_id",note.getId());
        }
        ((AppCompatActivity)context).startActivityForResult(intent,requestCode);
    }
}
