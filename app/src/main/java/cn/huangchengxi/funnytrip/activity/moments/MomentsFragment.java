package cn.huangchengxi.funnytrip.activity.moments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.adapter.MomentRVAdapter;
import cn.huangchengxi.funnytrip.item.MomentItem;

public class MomentsFragment extends Fragment {
    public static final int CONTENT_ALL=0;
    public static final int CONTENT_MINE=1;

    public int content;
    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout srl;
    private List<MomentItem> list;
    private MomentRVAdapter adapter;
    private String userId;

    public MomentsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MomentsFragment newInstance(int content) {
        MomentsFragment fragment = new MomentsFragment();
        fragment.content=content;
        return fragment;
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
                if (srl.isRefreshing()){
                    srl.setRefreshing(false);
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list=new ArrayList<>();
        for (int i=0;i<10;i++){
            list.add(new MomentItem("0","0","黄承喜",new Date().getTime(),"你好呀",null));
        }
        adapter=new MomentRVAdapter(list);
        recyclerView.setAdapter(adapter);
        return view;
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
}
