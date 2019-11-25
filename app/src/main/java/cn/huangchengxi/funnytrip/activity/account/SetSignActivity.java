package cn.huangchengxi.funnytrip.activity.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.huangchengxi.funnytrip.R;

public class SetSignActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView back;
    private TextView save;
    private EditText content;
    private TextView hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_sign);
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
        save=findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do save process
            }
        });
        content=findViewById(R.id.sign);
        hint=findViewById(R.id.content_hint);
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length()>20){
                    hint.setTextColor(Color.rgb(255,0,0));
                }else{
                    hint.setTextColor(Color.rgb(0,0,0));
                }
                hint.setText(editable.toString().length()+"/200");
            }
        });
    }
}
