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
            boolean isReturn=intent.getBooleanExtra("is_return_message",true);
            if (isReturn){
                String messageID=intent.getStringExtra("message_id");
                String toUID=intent.getStringExtra("to_uid");
                onMessageReceived.onSuccessSent(messageID);
            }else{
                String content=intent.getStringExtra("content");
                String fromUID=intent.getStringExtra("from_uid");
                String toUID=intent.getStringExtra("to_uid");
                String messageID=intent.getStringExtra("message_id");
                long time=intent.getLongExtra("time",new Date().getTime());
                String con=intent.getStringExtra("fromActivity");
                onMessageReceived.OnReveived(messageID,fromUID,toUID,content,time,con);
            }
        }
    }
    public interface OnMessageReceived{
        void OnReveived(String messageID,String fromUID,String toUID,String content,long time,String context);
        void onSuccessSent(String messageID);
    }

    public void setOnMessageReceived(OnMessageReceived onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }
}
