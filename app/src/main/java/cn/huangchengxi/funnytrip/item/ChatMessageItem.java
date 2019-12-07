package cn.huangchengxi.funnytrip.item;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChatMessageItem {
    private String messageId;
    private String content;
    private String userId;
    private String portraitUrl;
    private long time;
    private boolean isReceived;
    private boolean sent;

    public ChatMessageItem(String messageId,String content,String userId,String portraitUrl,long time,boolean isReceived,boolean isSent){
        this.content=content;
        this.userId=userId;
        this.time=time;
        this.isReceived=isReceived;
        this.portraitUrl=portraitUrl;
        this.messageId=messageId;
        this.sent=isSent;
    }

    public String getContent() {
        return content;
    }

    public long getTime() {
        return time;
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

    public String getPortraitUrl() {
        return portraitUrl;
    }

    public void setPortraitUrl(String portraitUrl) {
        this.portraitUrl = portraitUrl;
    }

    public boolean isReceived() {
        return isReceived;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public boolean isSent() {
        return sent;
    }
}
