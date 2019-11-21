package cn.huangchengxi.funnytrip.item;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MomentItem {
    private String momentId;
    private String senderId;
    private String name;
    private long time;
    private String content;
    private String imgUrl;

    public MomentItem(String momentId,String senderId,String name,long time,String content,String imgUrl){
        this.content=content;
        this.momentId=momentId;
        this.name=name;
        this.time=time;
        this.senderId=senderId;
        this.imgUrl=imgUrl;
    }

    public String getName() {
        return name;
    }

    public long getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public String getMomentId() {
        return momentId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getImgUrl() {
        return imgUrl;
    }
    public String getFormattedTime(){
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
}
