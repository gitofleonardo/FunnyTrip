package cn.huangchengxi.funnytrip.activity.navigation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.utils.setting.ApplicationSetting;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;
import cn.huangchengxi.funnytrip.view.OptionView;

public class SettingActivity extends AppCompatActivity {
    private OptionView maxClock;
    private OptionView maxNote;
    private ImageView back;
    private Toolbar toolbar;
    private String[] maxCounts={"1","2","3","4","5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
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
        maxClock=findViewById(R.id.max_clock);
        maxNote=findViewById(R.id.max_note);
        maxClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(SettingActivity.this);
                builder.setItems(maxCounts, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SqliteHelper helper=new SqliteHelper(SettingActivity.this,"settings",null,1);
                        SQLiteDatabase db=helper.getWritableDatabase();
                        db.execSQL("update settings set max_clock="+Integer.parseInt(maxCounts[which]));
                        ApplicationSetting.MAX_CLOCK_COUNT=Integer.parseInt(maxCounts[which]);
                    }
                }).show();
            }
        });
        maxNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(SettingActivity.this);
                builder.setItems(maxCounts, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SqliteHelper helper=new SqliteHelper(SettingActivity.this,"settings",null,1);
                        SQLiteDatabase db=helper.getWritableDatabase();
                        db.execSQL("update settings set max_note="+Integer.parseInt(maxCounts[which]));
                        ApplicationSetting.MAX_NOTE_COUNT=Integer.parseInt(maxCounts[which]);
                    }
                }).show();
            }
        });
    }
}
