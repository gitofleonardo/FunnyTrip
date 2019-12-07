package cn.huangchengxi.funnytrip.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MessageSocketStateReceiver extends BroadcastReceiver {
    private OnStateChangeListener onStateChangeListener;
    public static final int STATE_CONNECTED=0;
    public static final int STATE_DISCONNECTED=1;

    @Override
    public void onReceive(Context context, Intent intent) {
        int state=intent.getIntExtra("state",1);
        if (onStateChangeListener!=null){
            onStateChangeListener.onStateChange(state);
        }
    }
    public interface OnStateChangeListener{
        void onStateChange(int newState);
    }
    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }
}
