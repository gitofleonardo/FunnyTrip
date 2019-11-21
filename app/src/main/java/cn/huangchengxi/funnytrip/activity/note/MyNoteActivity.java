package cn.huangchengxi.funnytrip.activity.note;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.home.NoteActivity;
import cn.huangchengxi.funnytrip.adapter.NoteAdapter;
import cn.huangchengxi.funnytrip.item.NoteItem;
import cn.huangchengxi.funnytrip.utils.setting.ApplicationSetting;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;

public class MyNoteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private Toolbar toolbar;
    private ImageView back;
    private ImageView add;
    private ImageView synchronize;

    private final int NOTE_RC=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_note);
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
        add=findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteActivity.startNoteActivityForResult(MyNoteActivity.this,NOTE_RC,null);
            }
        });
        adapter=new NoteAdapter();
        recyclerView=findViewById(R.id.note_rv);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnNoteClickListener(new NoteAdapter.OnNoteClickListener() {
            @Override
            public void onClick(View view, NoteItem item) {
                NoteActivity.startNoteActivityForResult(MyNoteActivity.this,NOTE_RC,item);
            }
        });
        synchronize=findViewById(R.id.synchronize);
        synchronize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do upload and down process
            }
        });
        updateList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case NOTE_RC:
                updateList();
                break;
        }
    }
    private void updateList(){
        SqliteHelper helper=new SqliteHelper(this,"notes",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("notes",null,null,null,null,null,null);
        ApplicationSetting.notes.clear();
        adapter.clear();
        if (cursor.moveToFirst()){
            int i=0;
            do{
                long time=cursor.getLong(cursor.getColumnIndex("note_time"));
                String content=cursor.getString(cursor.getColumnIndex("content"));
                NoteItem item=new NoteItem(time,content);
                ApplicationSetting.notes.add(item);
                adapter.add(item);
            }while(cursor.moveToNext() && ++i<ApplicationSetting.MAX_NOTE_COUNT);
        }
    }
}
