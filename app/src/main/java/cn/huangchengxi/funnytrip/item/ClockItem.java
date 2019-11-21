package cn.huangchengxi.funnytrip.item;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockItem implements Serializable {
    private String id;
    private String location;
    private long time;
    public ClockItem(String id,String location,long time){
        this.id=id;
        this.location = location;
        this.time=time;
    }

    public String getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }
    public String getFormattedTime(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(new Date(time));
    }
}
