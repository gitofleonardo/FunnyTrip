package cn.huangchengxi.funnytrip.item;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeMomentItem {
    private long time;
    private String name;
    private String userID;
    private String momentID;
    private String content;

    public HomeMomentItem(String momentID,String userID,String content,String name,long time){
        this.content=content;
        this.momentID=momentID;
        this.name=name;
        this.time=time;
        this.userID=userID;
    }

    public long getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public String getMomentID() {
        return momentID;
    }

    public String getName() {
        return name;
    }

    public String getUserID() {
        return userID;
    }
    public String getFormattedDate(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(new Date(time));
    }
}
