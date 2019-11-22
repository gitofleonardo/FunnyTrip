package cn.huangchengxi.funnytrip.activity.navigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.view.AboutOption;

public class AboutActivity extends AppCompatActivity {
    private AboutOption homepage;
    private AboutOption mail;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        init();
    }
    public void init(){
        homepage=findViewById(R.id.my_homepage);
        mail=findViewById(R.id.my_mail);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri=Uri.parse("http://www.huangchengxi.cn");
                Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:971840889@qq.com"));
                startActivity(Intent.createChooser(intent, "发送邮件"));
            }
        });
    }
}
