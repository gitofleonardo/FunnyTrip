package cn.huangchengxi.funnytrip.item;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MessageItem {
    private String hisID;
    private long latestTime;
    private String hisName;
    private String latestContent;
    private String portraitUrl;

    public MessageItem(String hisID,String latestContent,String hisName,long latestTime,String portraitUrl){
        this.hisID=hisID;
        this.hisName=hisName;
        this.latestContent=latestContent;
        this.latestTime=latestTime;
        this.portraitUrl=portraitUrl;
    }

    public long getLatestTime() {
        return latestTime;
    }

    public String getHisID() {

        return hisID;
    }

    public String getHisName() {
        return hisName;
    }

    public String getLatestContent() {
        return latestContent;
    }
    public String getFormattedDate(){
        Date now=new Date();
        Date then=new Date(latestTime);
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
}
