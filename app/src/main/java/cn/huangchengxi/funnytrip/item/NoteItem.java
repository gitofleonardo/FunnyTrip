package cn.huangchengxi.funnytrip.item;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteItem {
    private long time;
    private String content;
    private String id;

    public NoteItem(String id,long time,String content){
        this.content=content;
        this.time=time;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public long getTime() {
        return time;
    }

    public String getFormattedTime(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(time));
    }
}
