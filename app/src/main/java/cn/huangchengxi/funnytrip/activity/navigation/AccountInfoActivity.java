package cn.huangchengxi.funnytrip.activity.navigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.account.AccountInfoFragment;
import cn.huangchengxi.funnytrip.activity.network.NetworkLoadingFragment;
import cn.huangchengxi.funnytrip.activity.network.NetworkNotAvailableFragment;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.databean.UserInformationBean;
import cn.huangchengxi.funnytrip.handler.AppHandler;
import cn.huangchengxi.funnytrip.utils.HttpHelper;

public class AccountInfoActivity extends AppCompatActivity implements NetworkNotAvailableFragment.OnFragmentInteractionListener,AppHandler.OnHandleMessage{

    private Toolbar toolbar;
    private ImageView back;

    //private MyHandler myHandler=new MyHandler();
    private AppHandler myHandler=new AppHandler(this);

    private final int FETCH_MY_INFORMATION_SUCCESS=3;
    private final int FETCH_ERROR=4;
    private final int CONNECTION_FAILED=5;
    private final int NOT_LOGIN=6;
    private final int CHANGE_FAILED=7;
    private final int CHANGE_SUCCESS=8;

    private int newGender=0;
    private long newBirthday=0;
    private String newHomeland=null;
    private String newLocation=null;
    private String newCareer=null;
    private String newInterest=null;

    private boolean changed=false;

    private Fragment currentFragment=null;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        init();
        fetchMyInformation();
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
        currentFragment= NetworkLoadingFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_layout,currentFragment).commitAllowingStateLoss();
    }
    private void fetchMyInformation(){
        String uid=((MainApplication)getApplicationContext()).getUID();
        HttpHelper.fetchMyInformation(uid, this, new HttpHelper.OnFetchUserInformation() {
            @Override
            public void onReturnSuccess(UserInformationBean bean) {
                Message msg=myHandler.obtainMessage();
                msg.what=FETCH_MY_INFORMATION_SUCCESS;
                msg.obj=bean;
                myHandler.sendMessage(msg);
            }
            @Override
            public void onReturnFail() {
                sendMessage(CONNECTION_FAILED);
            }
        });
    }
    private void sendMessage(int what){
        Message msg=myHandler.obtainMessage();
        msg.what=what;
        myHandler.sendMessage(msg);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
    @Override
    public void onBackPressed() {
        Log.e("changed",changed+" ");
        if (!changed){
            finish();
            return;
        }
        String uid=((MainApplication)getApplicationContext()).getUID();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        dialog=builder.setTitle("正在改哦,等一下...").setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        }).setCancelable(false).show();
        HttpHelper.changeMoreInformation(uid, this, newGender, newBirthday, newHomeland, newLocation, newCareer, newInterest, new HttpHelper.OnAfterChangeMore() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }
            @Override
            public void onReturnSuccess() {
                sendMessage(CHANGE_SUCCESS);
            }
        });
    }

    @Override
    public void onHandle(Message msg) {
        switch (msg.what){
            case CONNECTION_FAILED:
            case FETCH_ERROR:
                if (currentFragment!=null){
                    getSupportFragmentManager().beginTransaction().remove(currentFragment).commitAllowingStateLoss();
                }

                NetworkNotAvailableFragment networkNotAvailableFragment=NetworkNotAvailableFragment.newInstance();
                networkNotAvailableFragment.setOnRefreshListener(new NetworkNotAvailableFragment.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if(currentFragment!=null){
                            getSupportFragmentManager().beginTransaction().remove(currentFragment).commitAllowingStateLoss();
                        }
                        currentFragment= NetworkLoadingFragment.newInstance();
                        getSupportFragmentManager().beginTransaction().add(R.id.fragment_layout,currentFragment).commitAllowingStateLoss();
                        fetchMyInformation();
                    }
                });
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_layout,networkNotAvailableFragment).commitAllowingStateLoss();
                currentFragment=networkNotAvailableFragment;
                break;
            case CHANGE_FAILED:
                Toast.makeText(AccountInfoActivity.this, "更改失败", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case CHANGE_SUCCESS:
                Toast.makeText(AccountInfoActivity.this, "更改成功", Toast.LENGTH_SHORT).show();
                setResult(1);
                finish();
                break;
            case FETCH_MY_INFORMATION_SUCCESS:
                if (currentFragment!=null){
                    getSupportFragmentManager().beginTransaction().remove(currentFragment).commitAllowingStateLoss();
                }

                AccountInfoFragment accountInfoFragment=AccountInfoFragment.newInstance((UserInformationBean) msg.obj);
                accountInfoFragment.setOnDataChanged(new AccountInfoFragment.OnDataChanged() {
                    @Override
                    public void onChange(int newGender, long newBirthday, String newHomeland, String newLocation, String newCareer, String newInterest) {
                        changed=true;
                        AccountInfoActivity.this.newGender=newGender;
                        AccountInfoActivity.this.newBirthday=newBirthday;
                        AccountInfoActivity.this.newHomeland=newHomeland;
                        AccountInfoActivity.this.newLocation=newLocation;
                        AccountInfoActivity.this.newCareer=newCareer;
                        AccountInfoActivity.this.newInterest=newInterest;
                    }
                });
                currentFragment=accountInfoFragment;
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_layout,accountInfoFragment).commitAllowingStateLoss();
                break;
        }
    }

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {

        }
    }
}
