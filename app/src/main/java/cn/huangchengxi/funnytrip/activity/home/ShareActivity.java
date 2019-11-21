package cn.huangchengxi.funnytrip.activity.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.moments.MomentsFragment;
import cn.huangchengxi.funnytrip.adapter.MomentPagerAdapter;

public class ShareActivity extends AppCompatActivity implements MomentsFragment.OnFragmentInteractionListener{
    private Toolbar toolbar;
    private ImageView back;
    private TabLayout tabLayout;
    private ViewPager pager;
    private MomentPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
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
        tabLayout=findViewById(R.id.tab_layout);
        pager=findViewById(R.id.pager);
        adapter=new MomentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.add(MomentsFragment.newInstance(MomentsFragment.CONTENT_ALL),"动态");
        adapter.add(MomentsFragment.newInstance(MomentsFragment.CONTENT_MINE),"我的");
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
