package cn.huangchengxi.funnytrip.activity.navigation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.account.ChangePasswordActivity;
import cn.huangchengxi.funnytrip.activity.home.LoginActivity;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.databean.UserPropertiesBean;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.view.OptionView;
import cn.huangchengxi.funnytrip.view.SwitchOptionView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AccountSecurityFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    
    private MyHandler myHandler=new MyHandler();
    private SwitchOptionView allowUnknownViewMoments;
    private SwitchOptionView allowFriendViewMoments;
    private SwitchOptionView allowUnknownComment;
    private SwitchOptionView acceptAddFriend;
    private SwitchOptionView acceptMessage;
    private SwitchOptionView acceptTeamUp;
    private OptionView changePasswd;
    private SwitchOptionView showAddress;
    private SwitchOptionView showGender;
    private SwitchOptionView showBirth;
    private SwitchOptionView showHomeland;
    private SwitchOptionView showCareer;
    private SwitchOptionView showInterest;

    private final int CONNECTION_FAILED=3;
    private final int CHANGE_PROPERTY_SUCCESS=6;

    private final int CHANGE_PASSWORD_RC=1;
    private final int LOGIN_RC=0;
    
    private OnCommitChangesListener onCommitChangesListener;
    private UserPropertiesBean bean;
    
    
    public AccountSecurityFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static AccountSecurityFragment newInstance(UserPropertiesBean bean) {
        AccountSecurityFragment fragment = new AccountSecurityFragment();
        fragment.setBean(bean);
        return fragment;
    }

    public void setBean(UserPropertiesBean bean) {
        this.bean = bean;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_account_security, container, false);
        init(view);
        return view;
    }
    private void init(View view){
        allowFriendViewMoments=view.findViewById(R.id.allow_moments_viewed);
        allowUnknownViewMoments=view.findViewById(R.id.allow_moments_viewed_for_unknown);
        allowUnknownComment=view.findViewById(R.id.allow_moments_comment_for_unknown);
        acceptAddFriend=view.findViewById(R.id.allow_add_friend);
        acceptMessage=view.findViewById(R.id.allow_message);
        acceptTeamUp=view.findViewById(R.id.allow_team);
        showAddress=view.findViewById(R.id.show_address);
        showGender=view.findViewById(R.id.show_gender);
        showBirth=view.findViewById(R.id.show_birth);
        showHomeland=view.findViewById(R.id.show_homeland);
        showCareer=view.findViewById(R.id.show_career);
        showInterest=view.findViewById(R.id.show_interest);
        changePasswd=view.findViewById(R.id.change_password);
        changePasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((MainApplication)getContext().getApplicationContext()).isLogin()){
                    startActivityForResult(new Intent(getContext(), ChangePasswordActivity.class),CHANGE_PASSWORD_RC);
                }else{
                    Toast.makeText(getContext(), "还没有登录呢...", Toast.LENGTH_SHORT).show();
                    startActivityForResult(new Intent(getContext(), LoginActivity.class),LOGIN_RC);
                }
            }
        });

        allowUnknownViewMoments.setSwitchButton(bean.isAllowViewMomentsForUnknown());
        allowUnknownComment.setSwitchButton(bean.isAllowCommentForUnknown());
        allowFriendViewMoments.setSwitchButton(bean.isAllowViewMoments());
        acceptAddFriend.setSwitchButton(bean.isAcceptNewFriend());
        acceptMessage.setSwitchButton(bean.isAcceptMessage());
        acceptTeamUp.setSwitchButton(bean.isAcceptTeam());
        showAddress.setSwitchButton(bean.isShowAddress());
        showBirth.setSwitchButton(bean.isShowBirthday());
        showGender.setSwitchButton(bean.isShowGender());
        showHomeland.setSwitchButton(bean.isShowHomeland());
        showCareer.setSwitchButton(bean.isShowCareer());
        showInterest.setSwitchButton(bean.isShowInterest());
        addChangeListener();
    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (onCommitChangesListener!=null){
                onCommitChangesListener.onFinish();
            }
            switch (msg.what){
                case CHANGE_PROPERTY_SUCCESS:
                    addChangeListener();
                    break;
                case CONNECTION_FAILED:
                    Toast.makeText(getContext(), "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    SwitchOptionView sov1=getView().findViewById(msg.arg1);
                    if (sov1!=null){
                        sov1.setSwitchButton(!sov1.getSwitchState());
                    }
                    addChangeListener();
                    break;
            }
        }
    }
    private void changeProperty(String property, boolean value, final int switchId){
        removeListener(switchId);

        String uid=((MainApplication)getContext().getApplicationContext()).getUID();
        if (onCommitChangesListener!=null){
            onCommitChangesListener.onChange();
        }
        HttpHelper.updateUserProperties(uid, property, value, getContext(), new HttpHelper.OnUpdatePropertiesResult() {
            @Override
            public void onReturnSuccess() {
                sendMessage(CHANGE_PROPERTY_SUCCESS);
            }
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED,switchId);
            }
        });
    }
    private void removeListener(int switchId){
        SwitchOptionView optionView=getView().findViewById(switchId);
        optionView.setOnCheckStateChangedListener(null);
    }
    private void addChangeListener(){
        allowFriendViewMoments.setOnCheckStateChangedListener(new SwitchOptionView.OnCheckStateChangedListener() {
            @Override
            public void onChanged(boolean on) {
                changeProperty("allow_view_moments",on,allowFriendViewMoments.getId());
            }
        });
        allowUnknownViewMoments.setOnCheckStateChangedListener(new SwitchOptionView.OnCheckStateChangedListener() {
            @Override
            public void onChanged(boolean on) {
                changeProperty("allow_view_moments_for_unknown",on,allowUnknownViewMoments.getId());
            }
        });
        allowUnknownComment.setOnCheckStateChangedListener(new SwitchOptionView.OnCheckStateChangedListener() {
            @Override
            public void onChanged(boolean on) {
                changeProperty("allow_comment_unkonwn",on,allowUnknownComment.getId());
            }
        });
        acceptAddFriend.setOnCheckStateChangedListener(new SwitchOptionView.OnCheckStateChangedListener() {
            @Override
            public void onChanged(boolean on) {
                changeProperty("accept_new_friend",on,acceptAddFriend.getId());
            }
        });
        acceptMessage.setOnCheckStateChangedListener(new SwitchOptionView.OnCheckStateChangedListener() {
            @Override
            public void onChanged(boolean on) {
                changeProperty("accept_message",on,acceptMessage.getId());
            }
        });
        acceptTeamUp.setOnCheckStateChangedListener(new SwitchOptionView.OnCheckStateChangedListener() {
            @Override
            public void onChanged(boolean on) {
                changeProperty("accept_team",on,acceptTeamUp.getId());
            }
        });
        showAddress.setOnCheckStateChangedListener(new SwitchOptionView.OnCheckStateChangedListener() {
            @Override
            public void onChanged(boolean on) {
                changeProperty("show_address",on,showAddress.getId());
            }
        });
        showGender.setOnCheckStateChangedListener(new SwitchOptionView.OnCheckStateChangedListener() {
            @Override
            public void onChanged(boolean on) {
                changeProperty("show_gender",on,showGender.getId());
            }
        });
        showBirth.setOnCheckStateChangedListener(new SwitchOptionView.OnCheckStateChangedListener() {
            @Override
            public void onChanged(boolean on) {
                changeProperty("show_birthday",on,showBirth.getId());
            }
        });
        showHomeland.setOnCheckStateChangedListener(new SwitchOptionView.OnCheckStateChangedListener() {
            @Override
            public void onChanged(boolean on) {
                changeProperty("show_homeland",on,showHomeland.getId());
            }
        });
        showCareer.setOnCheckStateChangedListener(new SwitchOptionView.OnCheckStateChangedListener() {
            @Override
            public void onChanged(boolean on) {
                changeProperty("show_career",on,showCareer.getId());
            }
        });
        showInterest.setOnCheckStateChangedListener(new SwitchOptionView.OnCheckStateChangedListener() {
            @Override
            public void onChanged(boolean on) {
                changeProperty("show_interest",on,showInterest.getId());
            }
        });
    }
    private void sendMessage(int what){
        Message m=myHandler.obtainMessage();
        m.what=what;
        myHandler.sendMessage(m);
    }
    private void sendMessage(int what,int arg1){
        Message m=myHandler.obtainMessage();
        m.what=what;
        m.arg1=arg1;
        myHandler.sendMessage(m);
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public interface OnCommitChangesListener{
        void onChange();
        void onFinish();
    }
    public void setOnCommitChangesListener(OnCommitChangesListener onCommitChangesListener) {
        this.onCommitChangesListener = onCommitChangesListener;
    }
}
