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
import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.moments.MomentsFragment;
import cn.huangchengxi.funnytrip.activity.moments.ViewNotAllowFragment;
import cn.huangchengxi.funnytrip.activity.network.NetworkNotAvailableFragment;

public class UserMomentActivity extends AppCompatActivity implements MomentsFragment.OnFragmentInteractionListener, ViewNotAllowFragment.OnFragmentInteractionListener {
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
        setSupportActionBar(toolbar);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        MomentsFragment fragment=MomentsFragment.newInstance(id);
        fragment.setOnNotAllowListener(new MomentsFragment.OnNotAllowListener() {
            @Override
            public void onCommit() {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,ViewNotAllowFragment.newInstance()).commitAllowingStateLoss();
            }
        });
        transaction.add(R.id.fragment_layout,fragment);
        transaction.commitAllowingStateLoss();
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
