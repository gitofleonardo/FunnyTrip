package cn.huangchengxi.funnytrip.activity.friend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.moments.MomentsFragment;

public class UserMomentActivity extends AppCompatActivity implements MomentsFragment.OnFragmentInteractionListener{
    private Toolbar toolbar;
    private ImageView back;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_moment);
        init();
    }
    private void init(){
        id=getIntent().getStringExtra("id");
        toolbar=findViewById(R.id.toolbar);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.add(R.id.fragment_layout, MomentsFragment.newInstance(id));
        transaction.commit();
    }
    public static void startUserMomentActivity(Context context,String id){
        Intent intent=new Intent(context,UserMomentActivity.class);
        intent.putExtra("id",id);
        context.startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
