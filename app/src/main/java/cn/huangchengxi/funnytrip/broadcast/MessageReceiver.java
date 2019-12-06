package cn.huangchengxi.funnytrip.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

public class MessageReceiver extends BroadcastReceiver {
    private OnMessageReceived onMessageReceived;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (onMessageReceived!=null){
            String content=intent.getStringExtra("content");
            String fromUID=intent.getStringExtra("from_uid");
            String toUID=intent.getStringExtra("to_uid");
            long time=intent.getLongExtra("time",new Date().getTime());
            String con=intent.getStringExtra("fromActivity");
            onMessageReceived.OnReveived(fromUID,toUID,content,time,con);
        }
    }
    public interface OnMessageReceived{
        void OnReveived(String fromUID,String toUID,String content,long time,String context);
    }

    public void setOnMessageReceived(OnMessageReceived onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }
}
