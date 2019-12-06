package cn.huangchengxi.funnytrip.activity.network;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cn.huangchengxi.funnytrip.R;

public class NetworkNotAvailableFragment extends Fragment {
    private OnResultOkDataReceive onResultOkDataReceive;
    private OnRefreshListener onRefreshListener;
    private OnFragmentInteractionListener mListener;
    private LinearLayout layout;
    public NetworkNotAvailableFragment() {
        // Required empty public constructor
    }
    public static NetworkNotAvailableFragment newInstance() {
        NetworkNotAvailableFragment fragment = new NetworkNotAvailableFragment();
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
        View view=inflater.inflate(R.layout.fragment_network_not_available, container, false);
        layout=view.findViewById(R.id.fragment_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRefreshListener!=null){
                    onRefreshListener.onRefresh();
                }
            }
        });
        return view;
    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){

            }
        }
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
    public interface OnResultOkDataReceive{
        void onReceived(String body);
    }
    public interface OnRefreshListener{
        void onRefresh();
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public void setOnResultOkDataReceive(OnResultOkDataReceive onResultOkDataReceive) {
        this.onResultOkDataReceive = onResultOkDataReceive;
    }
}
