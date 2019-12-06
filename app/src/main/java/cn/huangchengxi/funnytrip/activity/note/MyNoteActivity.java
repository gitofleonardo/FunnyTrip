package cn.huangchengxi.funnytrip.activity.note;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Date;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.home.NoteActivity;
import cn.huangchengxi.funnytrip.adapter.NoteAdapter;
import cn.huangchengxi.funnytrip.databean.NotesResultBean;
import cn.huangchengxi.funnytrip.item.NoteItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;

public class MyNoteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private Toolbar toolbar;
    private ImageView back;
    private ImageView add;
    private ImageView synchronize;
    private final int CONNECTION_FAILED=0;
    private final int FETCH_NOTES_SUCCESS=1;

    private final int NOTE_RC=0;
    private MyHandler myHandler=new MyHandler();

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
                fetchNotes(new Date().getTime(),true);
            }
        });
        fetchNotes(new Date().getTime(),true);
    }
    private void fetchNotes(long timeLimit,final boolean remove){
        HttpHelper.fetchNotes(timeLimit, this, new HttpHelper.OnNotesResult() {
            @Override
            public void onReturnFailure() {
                Message msg=myHandler.obtainMessage();
                msg.what=CONNECTION_FAILED;
                myHandler.sendMessage(msg);
            }
            @Override
            public void onReturnSuccess(NotesResultBean bean) {
                Message msg=myHandler.obtainMessage();
                msg.what=FETCH_NOTES_SUCCESS;
                msg.obj=bean;
                msg.arg1=remove?1:0;
                myHandler.sendMessage(msg);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case NOTE_RC:
                if (data!=null){
                    fetchNotes(new Date().getTime(),true);
                }
                break;
        }
    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what){
                    case CONNECTION_FAILED:
                        Toast.makeText(MyNoteActivity.this, "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                        break;
                    case FETCH_NOTES_SUCCESS:
                        boolean remove=msg.arg1==1?true:false;
                        NotesResultBean bean=(NotesResultBean) msg.obj;
                        if (remove){
                            adapter.clear();
                        }
                        for (int i=0;i<bean.getList().size();i++){
                            adapter.add(bean.getList().get(i));
                        }
                        break;
                }
            }catch (Exception e){}
        }
    }
}
