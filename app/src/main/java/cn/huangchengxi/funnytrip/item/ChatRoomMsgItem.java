package cn.huangchengxi.funnytrip.item;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChatRoomMsgItem {
    private String content;
    private String userId;
    private String msgId;
    private long time;
    private boolean isRecieved;
    private String name;

    public ChatRoomMsgItem(String content,String userId,String msgId,long time,boolean isRecieved,String name){
        this.content=content;
        this.userId=userId;
        this.time=time;
        this.msgId=msgId;
        this.isRecieved=isRecieved;
        this.name=name;
    }

    public String getContent() {
        return content;
    }

    public long getTime() {
        return time;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getUserId() {
        return userId;
    }
    public String getFormattedDate(){
        Date now=new Date();
        Date then=new Date(time);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(then);

        Calendar calendar1=Calendar.getInstance();
        calendar1.setTime(now);

        if (calendar1.get(Calendar.DAY_OF_YEAR)>calendar.get(Calendar.DAY_OF_YEAR) && calendar1.get(Calendar.YEAR)>calendar.get(Calendar.YEAR)){
            SimpleDateFormat sdf=new SimpleDateFormat("MM-dd hh:mm");
            return sdf.format(then);
        }else{
            SimpleDateFormat sdf=new SimpleDateFormat("hh:mm");
            return sdf.format(then);
        }
    }

    public String getName() {
        return name;
    }

    public boolean isRecieved() {
        return isRecieved;
    }
}
