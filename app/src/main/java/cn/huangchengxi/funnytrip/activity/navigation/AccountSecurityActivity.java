package cn.huangchengxi.funnytrip.activity.navigation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.account.ChangePasswordActivity;
import cn.huangchengxi.funnytrip.activity.network.NetworkLoadingFragment;
import cn.huangchengxi.funnytrip.activity.network.NetworkNotAvailableFragment;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.databean.UserPropertiesBean;
import cn.huangchengxi.funnytrip.utils.HttpHelper;

public class AccountSecurityActivity extends AppCompatActivity implements AccountSecurityFragment.OnFragmentInteractionListener,NetworkNotAvailableFragment.OnFragmentInteractionListener{
    private Toolbar toolbar;
    private ImageView back;
    private ImageView loading;

    private MyHandler myHandler=new MyHandler();

    private final int LOGIN_RC=0;
    public static final int CHANGE_PASSWORD_RC=1;

    private final int FETCH_FAILED=2;
    private final int CONNECTION_FAILED=3;
    private final int FETCH_SUCCESS=4;
    private final int NOT_LOGIN=5;

    private Fragment currentFragment=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);
        init();
    }
    private void init(){
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back=findViewById(R.id.back);
        loading=findViewById(R.id.loading);
        Glide.with(this).load(R.drawable.loading).into(loading);
        loading.setVisibility(View.GONE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        currentFragment= NetworkLoadingFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.account_sec_fragment,currentFragment).commitAllowingStateLoss();
        fetchMyProperties();
    }
    private void fetchMyProperties(){
        HttpHelper.fetchUserProperties(((MainApplication) getApplicationContext()).getUID(), this, new HttpHelper.OnPropertiesResult() {
            @Override
            public void onReturnSuccess(UserPropertiesBean bean) {
                Message msg=myHandler.obtainMessage();
                msg.what=FETCH_SUCCESS;
                msg.obj=bean;
                myHandler.sendMessage(msg);
            }
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }
        });
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if (loading!=null){
                loading.setVisibility(View.GONE);
            }
            switch (msg.what){
                case CONNECTION_FAILED:
                case FETCH_FAILED:
                    if (currentFragment!=null){
                        getSupportFragmentManager().beginTransaction().remove(currentFragment).commitAllowingStateLoss();
                    }

                    NetworkNotAvailableFragment fragment1=NetworkNotAvailableFragment.newInstance();
                    fragment1.setOnRefreshListener(new NetworkNotAvailableFragment.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            if (currentFragment!=null){
                                getSupportFragmentManager().beginTransaction().remove(currentFragment).commitAllowingStateLoss();
                            }
                            currentFragment= NetworkLoadingFragment.newInstance();
                            getSupportFragmentManager().beginTransaction().add(R.id.account_sec_fragment,currentFragment).commitAllowingStateLoss();
                            fetchMyProperties();
                        }
                    });
                    FragmentManager manager1=getSupportFragmentManager();
                    FragmentTransaction transaction1=manager1.beginTransaction();
                    transaction1.add(R.id.account_sec_fragment,fragment1);
                    transaction1.commitAllowingStateLoss();
                    currentFragment=fragment1;
                    break;
                case FETCH_SUCCESS:
                    if (currentFragment!=null){
                        getSupportFragmentManager().beginTransaction().remove(currentFragment).commitAllowingStateLoss();
                    }

                    AccountSecurityFragment fragment=AccountSecurityFragment.newInstance((UserPropertiesBean) msg.obj);
                    fragment.setOnCommitChangesListener(new AccountSecurityFragment.OnCommitChangesListener() {
                        @Override
                        public void onChange() {
                            loading.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onFinish() {
                            loading.setVisibility(View.GONE);
                        }
                    });
                    FragmentManager manager=getSupportFragmentManager();
                    FragmentTransaction transaction=manager.beginTransaction();
                    transaction.add(R.id.account_sec_fragment, fragment);
                    transaction.commitAllowingStateLoss();

                    currentFragment=fragment;
                    break;
                case NOT_LOGIN:
                    break;
            }
        }
    }
    private void sendMessage(int what){
        Message m=myHandler.obtainMessage();
        m.what=what;
        myHandler.sendMessage(m);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case LOGIN_RC:
                if (data!=null){
                    startActivity(new Intent(AccountSecurityActivity.this, ChangePasswordActivity.class));
                }
                break;
            case CHANGE_PASSWORD_RC:
                if (data!=null){
                    setResult(ChangePasswordActivity.CHANGE_PASSWORD_SUCCESS);
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
