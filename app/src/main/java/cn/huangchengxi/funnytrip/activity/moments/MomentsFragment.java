package cn.huangchengxi.funnytrip.activity.moments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.friend.FriendDetailActivity;
import cn.huangchengxi.funnytrip.adapter.MomentRVAdapter;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.databean.MomentsBean;
import cn.huangchengxi.funnytrip.item.MomentItem;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MomentsFragment extends Fragment {
    private final int CONNECTION_FAILED=2;
    private final int FETCH_SUCCESS=3;
    private final int FETCH_FAILED=4;
    private final int VIEW_NOT_ALLOW=5;
    private final int NOT_LOGIN=6;
    private final int ADD_FETCH_SUCCESS=7;
    private final int LIKE_SUCCESS=8;
    private final int ALREADY_LIKE=9;
    private final int FETCH_NOT_ALLOW=10;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout srl;
    private List<MomentItem> list;
    private List<MomentItem> newList;
    private MomentRVAdapter adapter;
    private String userId;
    private MyHandler myHandler=new MyHandler();

    private OnNotAllowListener onNotAllowListener;
    private OnNetworkNotAvailableListener onNetworkNotAvailableListener;

    public MomentsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MomentsFragment newInstance(String userId) {
        MomentsFragment fragment = new MomentsFragment();
        fragment.userId=userId;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_moments, container, false);
        recyclerView=view.findViewById(R.id.moment_rv);
        srl=view.findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //do refresh
                fetchMoments(new Date().getTime(),true);
            }
        });
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        list=new ArrayList<>();
        adapter=new MomentRVAdapter(list,getContext());
        adapter.setOnPortraitClick(new MomentRVAdapter.OnPortraitClick() {
            @Override
            public void onClick(View view, int position) {
                FriendDetailActivity.startDetailActivity(getContext(),list.get(position).getSenderId());
            }
        });
        adapter.setOnLikeClick(new MomentRVAdapter.OnLikeClick() {
            @Override
            public void onClick(View view, int position) {
                commitLike(list.get(position).getMomentId(),position);
            }
        });
        recyclerView.setAdapter(adapter);
        srl.setRefreshing(true);
        fetchMoments(new Date().getTime(),true);
        return view;
    }
    private void commitLike(String momentID,final int position){
        String uid=((MainApplication)getContext().getApplicationContext()).getUID();
        HttpHelper.commitLike(uid, momentID, getContext(), new HttpHelper.OnLikeResult() {
            @Override
            public void onReturnSuccess() {
                sendMessage(LIKE_SUCCESS,position,list.get(position).getMomentId());
            }

            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }

            @Override
            public void onReturnAlreadyLike() {
                sendMessage(ALREADY_LIKE);
            }
        });
    }
    private void fetchMoments(long timeLimit,final boolean clear){
        String uid=((MainApplication)getContext().getApplicationContext()).getUID();
        HttpHelper.fetchMoments(uid, userId, timeLimit, getContext(), new HttpHelper.OnMomentsResult() {
            @Override
            public void onReturnFailure() {
                sendMessage(CONNECTION_FAILED);
            }
            @Override
            public void onReturnSuccess(MomentsBean bean) {
                newList=bean.getList();
                if (clear){
                    sendMessage(FETCH_SUCCESS);
                }else{
                    sendMessage(ADD_FETCH_SUCCESS);
                }
            }
        });
    }
    private void sendMessage(int what){
        Message m=myHandler.obtainMessage();
        m.what=what;
        myHandler.sendMessage(m);
    }
    private void sendMessage(int what,int position,String momentID){
        Message m=myHandler.obtainMessage();
        m.what=what;
        m.arg1=position;
        m.obj=momentID;
        myHandler.sendMessage(m);
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            try {
                if (srl.isRefreshing()){
                    srl.setRefreshing(false);
                }
                switch (msg.what){
                    case CONNECTION_FAILED:
                        Toast.makeText(getContext() , "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                        if (onNetworkNotAvailableListener!=null){
                            onNetworkNotAvailableListener.onCommit();
                        }
                        break;
                    case FETCH_FAILED:
                        Toast.makeText(getContext() , "获取数据失败", Toast.LENGTH_SHORT).show();
                        break;
                    case FETCH_SUCCESS:
                        list.clear();
                        adapter.notifyDataSetChanged();
                        list.addAll(newList);
                        adapter.notifyDataSetChanged();
                        break;
                    case ADD_FETCH_SUCCESS:
                        list.addAll(newList);
                        adapter.notifyDataSetChanged();
                        break;
                    case LIKE_SUCCESS:
                        int position=msg.arg1;
                        String momentID=(String) msg.obj;
                        if (list.get(position)!=null && list.get(position).getMomentId().equals(momentID)){
                            list.get(position).setLikeCount(list.get(position).getLikeCount()+1);
                            adapter.notifyItemChanged(position);
                        }
                        break;
                    case ALREADY_LIKE:
                        Toast.makeText(getContext(), "您已经赞过了哦...", Toast.LENGTH_SHORT).show();
                        break;
                    case FETCH_NOT_ALLOW:
                        if (onNotAllowListener!=null){
                            onNotAllowListener.onCommit();
                        }
                        break;
                }
            }catch (Exception e){}
            if (srl.isRefreshing()){
                srl.setRefreshing(false);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
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
    public interface OnNotAllowListener{
        void onCommit();
    }

    public void setOnNotAllowListener(OnNotAllowListener onNotAllowListener) {
        this.onNotAllowListener = onNotAllowListener;
    }
    public interface OnNetworkNotAvailableListener{
        void onCommit();
    }

    public void setOnNetworkNotAvailableListener(OnNetworkNotAvailableListener onNetworkNotAvailableListener) {
        this.onNetworkNotAvailableListener = onNetworkNotAvailableListener;
    }
}
