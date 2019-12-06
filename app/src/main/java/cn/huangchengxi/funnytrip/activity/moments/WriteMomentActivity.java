package cn.huangchengxi.funnytrip.activity.moments;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.home.MessageActivity;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.utils.StorageHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WriteMomentActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView back;
    private TextView commit;
    private TextView choosePic;
    private ImageView background;
    private EditText content;
    private String filePath=null;
    private TextView counter;

    private final int RC_CHOOSE_PIC=0;

    private final int CONNECTION_FAILED=1;
    private final int COMMIT_FAILED=2;
    private final int COMMIT_SUCCESS=3;
    private final int NOT_LOGIN=4;

    private MyHandler myHandler=new MyHandler();
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_moment);
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
        commit=findViewById(R.id.commit);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!content.getText().toString().equals("") && content.getText().toString().length()<1000){
                    commitShare();
                }
            }
        });
        choosePic=findViewById(R.id.choose_img);
        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();
            }
        });
        background=findViewById(R.id.content_img);
        counter=findViewById(R.id.counter);
        content=findViewById(R.id.content);
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length()>1000){
                    counter.setTextColor(Color.rgb(255,0,0));
                    counter.setText(editable.toString().length()+"/1000");
                }else{
                    counter.setTextColor(Color.rgb(0,0,0));
                    counter.setText(editable.toString().length()+"/1000");
                }
            }
        });
    }
    private void commitShare(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("正在上传...").setMessage("等一下哦").setView(R.layout.view_processing_dialog).setCancelable(false);
        dialog=builder.show();

        String uid=((MainApplication)getApplicationContext()).getUID();
        HttpHelper.commitShare(content.getText().toString(), filePath, this, new HttpHelper.OnCommitShareResult() {
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
    private void choosePhoto(){
        Intent intent=new Intent(Intent.ACTION_PICK,null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(intent,RC_CHOOSE_PIC);
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if (dialog!=null && dialog.isShowing()){
                dialog.dismiss();
            }
            switch (msg.what){
                case CONNECTION_FAILED:
                    Toast.makeText(WriteMomentActivity.this, "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    break;
                case COMMIT_FAILED:
                    Toast.makeText(WriteMomentActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    break;
                case COMMIT_SUCCESS:
                    Toast.makeText(WriteMomentActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case NOT_LOGIN:
                    break;
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case RC_CHOOSE_PIC:
                if (data!=null){
                    Uri uri=data.getData();
                    filePath= StorageHelper.getRealFilePath(WriteMomentActivity.this,uri);
                    Glide.with(WriteMomentActivity.this).load(filePath).into(background);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
