package cn.huangchengxi.funnytrip.item;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockItem implements Serializable {
    private String id;
    private String location;
    private long time;
    private double latitude;
    private double longitude;
    public ClockItem(String id,String location,long time,double latitude,double longitude){
        this.id=id;
        this.location = location;
        this.time=time;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
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
