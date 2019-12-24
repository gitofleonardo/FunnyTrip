package cn.huangchengxi.funnytrip.handler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

public class AppHandler extends Handler {
    private WeakReference<OnHandleMessage> activity;
    public AppHandler(OnHandleMessage onHandleMessage){
        activity=new WeakReference<>(onHandleMessage);
    }
    @Override
    public void handleMessage(Message msg) {
        try{
            if (activity!=null){
                activity.get().onHandle(msg);
            }
        }catch (Exception e){
            Log.e("Handler exception",e.toString());
        }
    }
    public interface OnHandleMessage{
        void onHandle(Message msg);
    }
}
